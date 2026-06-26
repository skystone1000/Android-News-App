# plan_5_remaining_work.md — Remaining work, gaps & known issues

> Consolidated backlog created by auditing the codebase against every prior plan
> (`plan_1_roadmap` … `plan_4_debug_mode`) and reading the source. Lists what is **not yet
> implemented**, **intentional placeholders**, and **bugs / hardening items** found during review.
> Created: 2026-06-25 · Owner: Aditya Mahajan
> Read alongside [../ARCHITECTURE.md](../ARCHITECTURE.md), [../CODEBASE.md](../CODEBASE.md),
> [../FEATURES.md](../FEATURES.md), [../DATASOURCES.md](../DATASOURCES.md).

## Audit summary (what's already done)

Verified implemented and working (clean `assembleDebug` + `detekt` + `testDebugUnitTest`):

- **plan_1 (ROADMAP) Phases 0–7** — foundation, data layer, reading loop, settings,
  AI layer, engagement (share/TTS/history/digest), R8 hardening. Only externally-gated items remain (§1).
- **plan_2 (multi-source) Phases A–F** — 5 providers, encrypted in-app keys, on-device usage meters.
- **plan_3 (UI refactor) Phases 1–2** — full Brief reskin + rename to **Briefly** (`com.skystone1000.briefly`).
- **plan_4 (debug mode)** — capture/replay interceptor, request log, reset actions, Gradle mock tasks.

The items below are everything that is **still open**.

---

## 1. Externally-gated / deferred (need accounts, infra, or devices)

These were intentionally deferred in plan_1 because they can't be provisioned in this environment.

| # | Item | Blocked on | Source |
|---|------|-----------|--------|
| 1 | **FCM push notifications** (breaking-news topics tied to followed categories) | Firebase project + `google-services.json` + a server/Cloud Function trigger | ROADMAP Phase 6 |
| 2 | **Crashlytics + Analytics** | Firebase project | ROADMAP Phase 7 |
| 3 | **Release signing + AAB + Play publishing** (release APK is currently **unsigned**) | Keystore + Play account | ROADMAP Phase 7 |
| 4 | **Modularization** (`:core` / `:domain` / `:data` / `:feature:*` + convention plugins) | Large refactor (current single-module is clean) | ROADMAP Phase 7 |
| 5 | **Baseline Profiles / Compose strong-skipping verification** | Benchmark module + a physical device | ROADMAP Phase 7 |
| 6 | **CI extension**: instrumented-test emulator matrix + release workflow (upload AAB) | Emulator/Play credentials in CI | ROADMAP Phase 7 |
| 7 | **Pre-launch compliance**: privacy policy, Play Data Safety form, store-listing assets | Release-time legal/marketing | ROADMAP Phase 7 |

## 2. Product / content gaps (intentional placeholders to finish)

| # | Item | Where | Notes |
|---|------|-------|-------|
| 8 | **Onboarding copy is Lorem Ipsum** | `presentation/onboarding/Page.kt` | Write real 3-page value-prop copy for Briefly. |
| 9 | **Search "Trending now" is a curated static list** | `presentation/search/SearchScreen.kt` (`TrendingTerms`) | No trending backend. Either wire a real trending source or relabel as "Suggested". |
| 10 | **Search "Browse topics" counts are decorative** | `presentation/search/SearchScreen.kt` | Counts are static/illustrative — make real (per-category result counts) or drop the numbers. |

## 3. Bugs & hardening found during review

| # | Severity | Item | Where | Fix sketch |
|---|----------|------|-------|-----------|
| 11 | Medium | **NewsData.io error responses break JSON parsing.** On success `results` is a JSON **array**; on an auth/quota error the API returns `results` as an **object**, so Gson throws instead of surfacing a clean error. | `data/remote/dto/NewsDataDto.kt`, `data/remote/source/NewsDataSource.kt` | Add a tolerant deserializer (or pre-read `status`/`results` type) and map error payloads to `MissingApiKeyException`/a friendly error. Add a unit test with an error-shaped fixture. |
| 12 | Medium | **Claude AI key is dev-only** (`CLAUDE_API_KEY` ships in `BuildConfig`). Not safe for a public release. | `data/ai/*`, `di/AiModule.kt` | Stand up a thin proxy and point the app at `CLAUDE_PROXY_URL`; strip the dev key from release builds. |
| 13 | Low | **Dead resources.** `drawable/ic_logo.xml` (legacy blue art) and `values/colors.xml` semantic colors (`display_small`, `text_medium`) are unreferenced after the Brief reskin. | `res/drawable/ic_logo.xml`, `res/values/colors.xml` | Delete; confirm no `R.` references remain; re-run `lintDebug`. |
| 14 | Low | **Mediastack free tier is HTTP-only** (cleartext scoped to `api.mediastack.com`). | `res/xml/network_security_config.xml` | Upgrade to a paid HTTPS plan and remove the cleartext exception for production. |
| 15 | Low | **Accessibility polish not done** — dynamic font scaling audit, content descriptions on icon buttons, AMOLED/dark-theme polish. | presentation-wide | Deferred polish pass from ROADMAP Phase 6. |

## 4. Test-coverage gap

- **No instrumented / Compose-UI / Room-DAO tests exist** despite the deps being wired
  (`androidx.compose.ui.test.junit4`, `hilt-android-testing`, `espresso`). `app/src/androidTest`
  contains only the template `ExampleInstrumentedTest`. Unit coverage is solid (25 unit test files).
- **Action:** add the instrumented tests called for across plan_1 (Room DAO round-trip, `NewsPagingSource`
  load tests on device, Home/Detail Compose UI tests) and run them in the CI emulator matrix (§1 #6).

## 5. Suggested order

`13 → 11 → 8` (quick cleanups + the one real parser bug + finish onboarding copy) before the
infra-gated work (§1), which should be tackled as a single "release readiness" push once a Firebase
project, keystore, and Play account are available. §4 (instrumented tests) pairs naturally with §1 #6.
