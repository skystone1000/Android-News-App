# MULTI_SOURCE_PLAN.md — Multi-provider news + in-app API keys

> Plan to support 5 news providers and let the user enter/manage each provider's API key
> **inside the app** (encrypted at rest), with an **in-app free-tier usage meter** (used / limit +
> reset time) per provider. One source is active at a time.
> Created: 2026-06-22 · Owner: Aditya Mahajan
> Read alongside [ARCHITECTURE.md](ARCHITECTURE.md), [CODEBASE.md](CODEBASE.md), [FEATURES.md](FEATURES.md), [ROADMAP.md](ROADMAP.md).
> Per root `CLAUDE.md`: as each phase lands, update those docs and tick the boxes here.

## Decisions (locked)

| Decision | Choice | Why |
|----------|--------|-----|
| Source usage model | **One active source at a time** | Extends the existing `NewsSourceProvider` single-active design; predictable quota use; clean paging. |
| Key storage | **Encrypted at rest** (Android Keystore-backed) | Matches the "production-shippable" goal; keys never sit in cleartext. |
| Key entry | **In-app**, per provider, in Settings | Replaces compile-time `BuildConfig` keys (which become a dev-only seed). |

## Providers in scope

| Provider | `id` | Base URL | Auth (query param) | Headlines | Search | Paging model | Results root | Notes |
|----------|------|----------|--------------------|-----------|--------|--------------|--------------|-------|
| NewsAPI.org | `newsapi` | `https://newsapi.org/` | `apiKey` | `v2/top-headlines` | `v2/everything` | int `page`/`pageSize` | `articles[]` | Already implemented. Free key is **dev-only** (no prod). |
| NewsData.io | `newsdata` | `https://newsdata.io/` | `apikey` | `api/1/latest` | `api/1/latest?q=` | **cursor** `page` (opaque token), `size` | `results[]` + `nextPage` | Cursor pagination — not numeric. |
| GNews | `gnews` | `https://gnews.io/` | `apikey` | `api/v4/top-headlines` | `api/v4/search` | int `page`/`max` | `articles[]` | Already implemented. |
| Currents | `currents` | `https://api.currentsapi.services/` | `apiKey` | `v1/latest-news` | `v1/search` | int `page_number`/`page_size` | `news[]` | Category vocab differs. |
| Mediastack | `mediastack` | `http://api.mediastack.com/` | `access_key` | `v1/news` | `v1/news?keywords=` | **offset** `offset`/`limit` | `data[]` + `pagination` | Free tier is **HTTP only** (no TLS). |

> ⚠️ Endpoint paths/params above are best-effort and **must be verified against each provider's
> current docs** during implementation — these APIs drift. The architecture below is provider-shaped
> so a path/param tweak stays local to one source.

## Why this is more than "add 3 sources"

The current design injects each source's key **once at DI time** from `BuildConfig`
(`SourceModule` → `NewsApiSource(service, BuildConfig.NEWS_API_KEY)`). Runtime keys mean:
- the key can change at any time and is **unknown when the Hilt graph is built**, so sources must
  **read their key per call** from a store, not hold it as a constructor constant;
- two providers don't fit the current `Int page` model (NewsData = opaque cursor, Mediastack = offset),
  so the `NewsSource` contract needs a small **cursor-capable** result type.

---

# Phase A — Runtime encrypted key store

**Goal:** A secure, observable per-provider key store; provider catalog metadata.

**Tasks**
- [ ] `domain/security/ApiKeyStore` interface:
  - `fun keys(): Flow<Map<String,String>>` (sourceId → non-blank key)
  - `suspend fun getKey(sourceId: String): String`
  - `suspend fun setKey(sourceId: String, key: String)` / `suspend fun clearKey(sourceId: String)`
- [x] `data/security/EncryptedApiKeyStore` impl. **Chosen:** `androidx.security:security-crypto`
  `EncryptedSharedPreferences` with a Keystore-backed `MasterKey` (AES-256-GCM values, AES-256-SIV
  keys); `keys()` mirrored into a `MutableStateFlow`, updated on each write. (Tink AEAD + DataStore
  was the alternative; security-crypto keeps the impl small and stable. The lib is in maintenance but
  fully functional.)
- [ ] `domain/model/SourceCatalog.kt`: `SourceMetadata(id, displayName, signupUrl, keyParamHint, quota)`
  where `quota: SourceQuota(limit, period)` (see Phase D) carries the free-tier limit used by the
  usage meter. `val ALL_SOURCES: List<SourceMetadata>` for the 5 providers. Replace `AVAILABLE_SOURCE_IDS`.
- [ ] Hilt: `di/SecurityModule` provides `ApiKeyStore`.
- [ ] Dependency: `com.google.crypto.tink:tink-android` (or `androidx.security:security-crypto`).

**Tests:** key round-trip (set → encrypted on disk → get returns plaintext); clear removes it;
`keys()` emits only non-blank entries.

**Exit:** keys can be stored/read/cleared securely and observed as a Flow. No UI yet.

---

# Phase B — Cursor-capable source contract + paging refactor

**Goal:** One uniform source/paging contract that fits numeric, offset, and cursor providers,
with keys read at call time.

**Tasks**
- [x] Evolve `NewsSource`:
  - `suspend fun getNews(category: String?, cursor: String?, pageSize: Int): NewsPage`
  - `suspend fun searchNews(query: String, cursor: String?, pageSize: Int): NewsPage`
  - `data class NewsPage(val articles: List<Article>, val nextCursor: String?)` (null = end).
- [x] Numeric providers (NewsAPI, GNews, Currents) encode the page number in the cursor string
  (`"1"`, `"2"`, …); Mediastack encodes `offset`; NewsData passes the opaque `nextPage` token through.
- [x] Inject `ApiKeyStore` into each source; read `getKey(id)` per call; throw a typed
  `MissingApiKeyException(sourceId)` when blank.
- [x] Refactor `NewsPagingSource` to key on `String?` (cursor) instead of `Int`; update the
  `Pager`/`PagingConfig` in `NewsRepositoryImpl`. Map `MissingApiKeyException` → `LoadResult.Error`.
- [x] Update existing `NewsApiSource` + `GNewsSource` to the new contract.

**Tests:** fake source paging (cursor advance + end); missing-key → error surfaced; numeric-cursor
round-trips.

**Exit:** existing two providers work through the new cursor-based pipeline with runtime keys.

---
 — Add NewsData.io, Currents, Mediastack

**Goal:** Three new providers behind the same contract — each is one self-contained slice.

**Per provider (×3):**
- [x] `data/remote/api/<X>Service.kt` (Retrofit) + `data/remote/dto/<X>Dto.kt` + `toArticleOrNull()` mapper.
- [x] `data/remote/source/<X>Source.kt` implementing `NewsSource`.
- [x] `@Provides @IntoMap @StringKey(<X>Source.ID)` in `SourceModule`; Retrofit instance in `NetworkModule`.
- [x] Per-provider **category mapping** to the shared `NewsCategories` vocabulary (pass-through where equal).

**Provider specifics**
- [x] **NewsData.io:** cursor pagination via `nextPage`; map `results[]`.
- [x] **Currents:** `page_number`/`page_size`; map `news[]`; reconcile category names.
- [x] **Mediastack:** offset paging (`offset`/`limit`); map `data[]`; **HTTP-only** → add a
  `network_security_config.xml` allowing cleartext **only** for `api.mediastack.com`, referenced
  from the manifest. Document the free-tier-TLS limitation in FEATURES.

**Tests:** one mapper test per provider (sample JSON → `Article`, including null/missing fields).

**Exit:** all 5 providers selectable end-to-end (given a key).

---

# Phase D — Free-tier usage tracking (data layer)

**Goal:** Count requests per provider against its known free-tier limit, reset on the provider's
window, and expose it as observable state for the UI.

**Why count on-device:** these free tiers mostly **don't return a reliable remaining-quota header**,
so usage is **tracked locally** and shown as an *estimate* — the same key used on another device or
tool won't be counted. When a provider *does* return rate-limit headers, prefer them over the local count.

**Tasks**
- [x] `domain/model/SourceQuota.kt`: `SourceQuota(limit: Int, period: QuotaPeriod)` with
  `enum QuotaPeriod { DAILY, MONTHLY }` (DAILY for NewsAPI/NewsData/GNews/Currents, MONTHLY for Mediastack).
- [x] `domain/usage/ApiUsageStore` interface:
  - `fun usage(): Flow<Map<String, UsageSnapshot>>`
  - `suspend fun recordRequest(sourceId: String)`
  - `suspend fun reset(sourceId: String)`
  - `data class UsageSnapshot(used: Int, limit: Int, period: QuotaPeriod, windowStart: Long, resetAt: Long)`.
- [x] `data/usage/ApiUsageStoreImpl`: persist `{count, windowStart}` per source (DataStore or a small
  Room table). On read/increment, if the current time has crossed the period boundary (new local day /
  new month), roll the window and zero the count.
- [x] **Increment hook:** an OkHttp interceptor maps request **host → `sourceId`** and calls
  `recordRequest`; it also parses `X-RateLimit-Remaining`/`-Limit` (or provider equivalents) when
  present and reconciles (header value overrides the local estimate).
- [x] Hilt: provide `ApiUsageStore`; register the interceptor on the OkHttp client (`NetworkModule`).

**Tests:** increment raises `used`; crossing the day/month boundary resets; header value overrides
local count; host→sourceId mapping.

**Exit:** every provider request is counted; `usage()` emits live per-source used/limit + reset time.

---

# Phase E — Settings UI: keys, source selection + usage meters

**Goal:** Users add/remove a key per provider, see how much of each free tier is used, and pick the
active source — gated on configured keys.

**Tasks**
- [x] `SettingsEvent.SetApiKey(sourceId, key)` + `ClearApiKey(sourceId)`; `SettingsViewModel`
  reads `ApiKeyStore.keys()` + `ApiUsageStore.usage()` + settings and exposes a "configured source ids" set.
- [x] Settings "Data sources & API keys" section: one row per `SourceMetadata` with
  display name, a **masked** key field (save/clear), a "Get a key" link (opens `signupUrl`),
  and a configured/active badge.
- [x] **Usage meter** on each configured row: `used / limit` for the period, a progress bar
  (green → amber → red as it nears the limit), and reset time ("resets in 4h" / "resets Jul 1"),
  labeled as an **on-device estimate**. A source at/over its limit shows a warning that further
  calls may fail (HTTP 429). Unconfigured rows show no meter.
- [x] Active-source picker enables **only** configured sources; choosing an unconfigured one
  routes the user to add a key.
- [x] If the active source's key is cleared, fall back to another configured source (else default),
  so the feed never points at an unusable source.

**Tests:** gating (unconfigured source not selectable); clearing the active key triggers fallback;
set/clear key reflected in `keys()`; usage meter renders used/limit and an over-limit warning.

**Exit:** a user with zero keys is guided to add one; each configured source shows its live free-tier
usage and reset time; switching works.

---

# Phase F — Hardening, security, docs

**Tasks**
- [x] **Log redaction:** OkHttp `BODY` logging in debug prints the full URL — which contains the
  key as a query param. Add a `redactQueryParam`/header-redaction interceptor (or move keys to
  headers where supported) so keys don't leak into Logcat.
- [x] **Error UX:** missing/invalid-key and rate-limit (HTTP 401/403/429) → friendly empty/error
  state with an "Add API key in Settings" CTA (extend `EmptyScreen`/paging error handling).
- [x] **ProGuard:** ensure new DTO packages are covered by keep rules (existing rule globs
  `data.remote.dto.**`; verify the new DTOs live there or extend the rule).
- [x] **Usage accuracy caveat:** keep the meter labeled an on-device estimate — failed/cached
  requests and usage from other devices/tools won't match the provider's own count exactly.
- [x] **Dev seed (optional):** on first launch, if `ApiKeyStore` is empty and `BuildConfig.*_API_KEY`
  is present, seed it — keeps the existing `local.properties` dev flow working.
- [x] **Docs:** `ARCHITECTURE.md` (runtime encrypted-key layer + cursor paging + usage tracking),
  `CODEBASE.md` (new files), `FEATURES.md` (#16 reworded + per-provider + key-management +
  usage-meter rows), update `AVAILABLE_SOURCE_IDS` → `SourceCatalog` references.

**Exit:** keys never logged or shipped; clear failure UX; all 5 providers documented.

---

## Free-tier reality check (verify before relying on these)

These limits are the source of the `SourceQuota` values driving the in-app **usage meter** (Phase D/E).
Verify each against current provider docs and update the catalog when they change.

| Provider | Approx. free quota | Meter period | Prod use? |
|----------|--------------------|--------------|-----------|
| NewsAPI.org | ~100 req/day | DAILY | **Dev only** (TOS) |
| NewsData.io | ~200 credits/day | DAILY | Yes (paid tiers) |
| GNews | ~100 req/day | DAILY | Yes (paid tiers) |
| Currents | free dev tier | DAILY | Yes (paid tiers) |
| Mediastack | ~500 req/month, **HTTP only** on free | MONTHLY | Paid for HTTPS |

## Security summary
- Keys encrypted via a Keystore-backed key (Tink AEAD); decrypted only in memory on use.
- Keys removed from `local.properties`/`BuildConfig` as the source of truth (kept only as an
  optional dev seed). Nothing key-related enters VCS.
- Query-param keys are **redacted** from debug network logs (Phase F).
- Mediastack cleartext is scoped to its single domain via network-security-config and flagged to the user.

## Suggested build order
A → B → C → D → E → F. After each phase: sanity `assembleDebug` + `detekt`, write tests (run the full
suite at the end per the project workflow), update docs, commit with a `Feature:`/`Bug:` prefix.
This can be appended to `ROADMAP.md` as **Phase 8** if you want it tracked in the master plan.
