# plan_4_debug_mode.md (DEBUG_MODE_PLAN) — Debug-mode tooling

> Plan for in-app developer tooling: capture API responses, replay them offline, and
> related debug aids. **Debug builds only** (`BuildConfig.DEBUG`); zero behaviour in release.
> Read `ARCHITECTURE.md` + `DATASOURCES.md` first. Last updated: 2026-06-24.
> **Status: IMPLEMENTED.** All phases landed; `assembleDebug` + `detekt` + unit tests + `lintDebug` green.
> Capture pipeline (`pullMocks` → `NewsApp/mock` → `seedMockAssets`) requires a device + `adb`.

## 0. Goals & decisions

| Topic | Decision |
|-------|----------|
| Capture location | Device external files dir (`Android/data/<pkg>/files/mock`) + a Gradle **`pullMocks`** task that copies into `NewsApp/mock` (version-controllable). |
| Offline source | **Device captures first, then bundled `assets/mock` fallback** (fresh install / CI still works). |
| Extra tools | **All:** force API error, latency simulation, in-app request log/inspector, reset app state + clear captured mocks. |
| Gating | Debug builds only. Code lives in `main` but every effect is guarded by `BuildConfig.DEBUG`; the Settings entry + screen render only in debug. |
| Layer | A single **OkHttp `MockInterceptor`** (transparent to Retrofit services, sources, repository). Fits the existing interceptor stack in `NetworkModule`. |

**Why an interceptor:** the network layer is already interceptor-based (`UsageInterceptor`,
`RedactingLoggingInterceptor`) and `sourceIdForHost()` already maps host → provider id. Capturing
and replaying at this layer means **no changes to services, DTOs, sources, paging, or the repository.**

## 1. The constraint (read this)

An app running on a device/emulator **cannot write to `NewsApp/mock`** — that path is on the dev
machine, not the device. So the pipeline is:

```
[app captures] → Android/data/<pkg>/files/mock      (on device, adb-pullable)
   │  ./gradlew pullMocks
   ▼
NewsApp/mock/…                                       (in the repo, git-tracked)
   │  ./gradlew seedMockAssets   (optional)
   ▼
NewsApp/app/src/main/assets/mock/…                   (bundled in APK → offline fallback)
```

## 2. Folder & file layout (capture)

Categorised by **which API call is being made** — `sourceId / endpoint / param-slug`:

```
mock/
  newsapi/
    top-headlines/
      category=business/   001.json  002.json  …
      category=general/    001.json
    everything/
      q=bitcoin/           001.json
  gnews/
    top-headlines/         page=1/   001.json
  newsdata/  …  currents/  …  mediastack/  …
```

- **sourceId** ← `sourceIdForHost(request.url.host)` (already exists; non-provider hosts like image
  CDNs are skipped).
- **endpoint** ← last path segment(s) (`v2/top-headlines` → `top-headlines`). Generic across all 5
  providers — no per-provider code.
- **param-slug** ← request query params **excluding the key** (`apiKey`/`apikey`/`token`/`access_key`/
  `apikey`), sorted and joined (`category=business`), so a replayed file matches the call that made it.
- **filename** ← zero-padded incrementing counter (`001.json`, `002.json`). Multiple captures per key
  build a pool that offline mode rotates through.

## 3. New components

### 3a. Debug settings — `domain/debug/` + `data/debug/`
- `domain/debug/DebugConfig.kt` — `data class DebugConfig(saveResponses, offlineMode, forceError, latencyMs)` (all default off/0).
- `domain/debug/DebugSettingsStore.kt` — contract: `fun config(): Flow<DebugConfig>` + setters.
- `data/debug/DebugSettingsStoreImpl.kt` — DataStore Preferences (store `debug_settings`).

### 3b. Mock IO — `data/debug/MockStore.kt`
- `keyOf(request): String` → relative path `sourceId/endpoint/paramSlug`.
- `save(key, json)` → write next `NNN.json` under the device `mock/` dir.
- `next(key): String?` → **round-robin** selection over available files (in-memory `AtomicInteger`
  per key) so **each refresh returns a different saved response**; falls back to `assets/mock/<key>`
  when the device dir has none; null if neither.
- `clearAll()` → delete the device `mock/` tree.
- Dir = `context.getExternalFilesDir(null)/mock`. Assets read via `context.assets.open("mock/…")`.

### 3c. `data/remote/MockInterceptor.kt`  (inserted **first** in the chain)
Pseudocode:
```
val cfg = configSnapshot()              // see §3d (no DataStore read on the network thread)
val sourceId = sourceIdForHost(req.url.host) ?: return chain.proceed(req)   // only provider calls
if (!BuildConfig.DEBUG) return chain.proceed(req)

if (cfg.latencyMs > 0) Thread.sleep(cfg.latencyMs)
if (cfg.forceError)    throw IOException("Forced debug error")              // exercises LoadResult.Error
if (cfg.offlineMode) {
    val json = mockStore.next(keyOf(req)) ?: return emptyJsonResponse(req)  // replay
    log(req, status = REPLAYED); return jsonResponse(req, json)
}
val resp = chain.proceed(req)                                              // live
if (cfg.saveResponses && resp.isSuccessful) {
    mockStore.save(keyOf(req), resp.peekBody(MAX).string())                // tee, body untouched
}
log(req, status = if (cfg.saveResponses) SAVED else LIVE); return resp
```
- Synthetic responses built with `Response.Builder()` (code 200, `json.toResponseBody("application/json")`).
- Placed **before** `UsageInterceptor` so replayed/offline calls don't burn the usage quota.

### 3d. Config snapshot (avoid DataStore on the network thread)
`DebugModule` launches a `CoroutineScope` that collects `DebugSettingsStore.config()` into an
`AtomicReference<DebugConfig>`; the interceptor reads `.get()` synchronously. Cheap and lock-free.

### 3e. Request log / inspector — `data/debug/RequestLog.kt`
- Ring buffer (last ~100) of `RequestEntry(time, method, urlRedacted, status, durationMs, kind)` where
  `kind ∈ {LIVE, SAVED, REPLAYED, ERROR}`. Exposed as `StateFlow<List<RequestEntry>>`.
- Written by `MockInterceptor`; URL redacted via the same key-param masking as `RedactingLoggingInterceptor`.

### 3f. Reset actions — `data/debug/DebugActions.kt`
Suspend helpers: `clearMocks()` (→ `MockStore.clearAll()`), `resetAppState()` (clear user-settings &
debug & usage DataStores, wipe Room `NewsDatabase` tables, clear `ApiKeyStore`). Each guarded/confirmed in UI.

### 3g. DI — `di/DebugModule.kt`
Provides `DebugSettingsStore`, `MockStore`, `RequestLog`, `DebugActions`, and the config snapshot
holder. `NetworkModule.provideOkHttpClient` gains a `MockInterceptor` param and adds it **first**.

### 3h. UI — `presentation/debug/` (debug-gated)
- Settings → **"Developer options"** row, shown only when `BuildConfig.DEBUG`, → `DebugScreen` (new route).
- `DebugScreen`: `BriefToggle`s for Save responses / Offline mode / Force error; a latency stepper;
  buttons **Clear captured mocks** and **Reset app state** (confirm dialog); a link to the request log.
- `RequestLogScreen`: list of `RequestEntry` (status colour-coded; REPLAYED tagged), newest first.
- Reuses existing Brief components (`SegmentedControl`, `BriefToggle`, `SectionHeader`, `ArticleRow`-style rows).

### 3i. Gradle tasks — `app/build.gradle.kts` (debug helpers)
- `pullMocks` — `adb pull /sdcard/Android/data/<applicationId>/files/mock <root>/NewsApp/mock`.
- `clearDeviceMocks` — `adb shell rm -rf …/files/mock`.
- `seedMockAssets` — copy `NewsApp/mock` → `app/src/main/assets/mock` (bundle as offline fallback).
- All resolve `<applicationId>` from the variant (the Phase 2 rename → `com.skystone1000.briefly`
  changed the device path automatically; the `mockAppId` constant tracks `applicationId`).

## 4. Build order

1. `DebugConfig` + `DebugSettingsStore`(+Impl) + `DebugModule` + config-snapshot holder.
2. `MockStore` (capture + round-robin replay + assets fallback) + key derivation.
3. `MockInterceptor`; wire into `NetworkModule` (first in chain). → **save + offline core works.**
4. `pullMocks` / `clearDeviceMocks` / `seedMockAssets` Gradle tasks.
5. `DebugScreen` + Settings entry + nav route (debug-gated). Toggles for save/offline.
6. Extra tools: force-error + latency in the interceptor + UI; `RequestLog` + `RequestLogScreen`;
   `DebugActions` (reset state / clear mocks) + UI with confirm.
7. Assets fallback verified (fresh install, offline, no captures → reads `assets/mock`).
8. Verify: `assembleDebug` + `detekt` + unit tests + `lintDebug`; manual capture→pull→offline loop.
9. Docs: add the new files to `CODEBASE.md`, note the debug tooling in `ARCHITECTURE.md`, and
   reference the capture pipeline in `DATASOURCES.md`.

## 5. Risks / notes
- **Release safety:** every path guarded by `BuildConfig.DEBUG`; `MockInterceptor` early-returns and
  the Settings entry is hidden in release. Consider isolating in a `src/debug/` source set if stricter
  separation is wanted (heavier setup; not required).
- **External-files dir** needs no runtime permission (app-scoped). Survives app restarts; removed on uninstall.
- **`peekBody`** buffers the JSON in memory to tee it — fine for news payloads; cap the byte limit.
- **Offline + paging:** returning the same/rotated page JSON is fine; Paging 3 de-dupes by URL, so
  rotating files gives fresh-looking refreshes (matches the "new response every refresh" ask).
- **Quota:** mock/replay calls are skipped by `UsageInterceptor` (ordering), so offline testing won't
  consume free-tier limits.
