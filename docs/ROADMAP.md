# ROADMAP.md — Sequential Implementation Plan (Path A → D)

> Detailed, phased plan to take NewsApp from its current state (Splash + Onboarding only)
> to a production-shippable, fully-configurable news app.
> Created: 2026-06-21 · Owner: Aditya Mahajan
> Read alongside [ARCHITECTURE.md](ARCHITECTURE.md), [CODEBASE.md](CODEBASE.md), [FEATURES.md](FEATURES.md).
> **Per the root `CLAUDE.md` rule: when a phase lands, update those docs and tick the boxes here.**

## Product decisions driving this plan

| Decision | Choice |
|----------|--------|
| Product angle | **All angles, user-configurable.** AI summaries, personalization, and niche filtering all ship as features the user enables/disables in **Settings**. Everything off = a clean generic reader. |
| Data source | **Source-agnostic.** A `NewsRepository` interface sits over swappable `NewsSource` implementations (NewsAPI, GNews, …), selectable at runtime. Adding a source = adding one class. |
| Quality bar | **Production-shippable.** Full testing, CI/CD, static analysis, crash reporting, signed releases. |
| Sequencing | **Approach 2** — A→B→C→D spine, but Path D *foundations* (test harness, CI, quality gates) are front-loaded into Phase 0 so every later phase ships tested. |

## How to read each phase

Every phase lists: **Goal · Prerequisites · Tasks · New files/layers · Dependencies to add · Testing · Exit criteria · Docs to update.**
A phase is **Done** only when its exit criteria pass *and* the relevant `docs/` files are updated.

## Cross-cutting standards (apply to every phase)

- **Definition of Done:** code + tests + green CI + docs updated + no new lint/detekt violations.
- **Testing strategy:** pure-Kotlin domain (use cases, mappers) → fast unit tests; ViewModels → unit tests with fakes + Turbine for `Flow`/state; Room/Paging → instrumented tests; critical screens → Compose UI tests.
- **Architecture:** keep `domain` pure Kotlin; dependencies point inward; one public reason-to-exist per class; constructor injection via Hilt.
- **Secrets:** never commit API keys. Inject via `local.properties` → `BuildConfig` (local) and CI secrets (pipeline).
- **Branching:** one feature branch per phase (or per task for large phases); PR → CI must be green to merge.

---

# Phase 0 — Production foundation (front-loaded Path D)

**Goal:** Put quality infrastructure in place *before* feature code, so nothing is retrofitted later.

**Prerequisites:** none (build already works on Gradle 8.11.1 / AGP 8.7.3 / JDK 21).

**Tasks**
- [x] Introduce a **version catalog** `gradle/libs.versions.toml`; migrate `build.gradle.kts` and `app/build.gradle.kts` to reference it (single source of truth for versions).
- [x] Add **test stack**: JUnit4, MockK, `kotlinx-coroutines-test`, Turbine, Truth, `androidx.test` (ext-junit, espresso), Compose UI test, Hilt testing (`hilt-android-testing`).
- [x] Add **static analysis**: detekt with baseline + Android `lint`. (ktlint/Spotless deferred — detekt's formatting rules cover the basics for now.)
- [x] Add **GitHub Actions CI** (`.github/workflows/ci.yml`): checkout → set up JDK 17 → cache Gradle → `detekt` + `lintDebug` + `testDebugUnitTest` + `assembleDebug`. Runs on PR + push to `main`.
- [x] **Hygiene fixes** (clear the known gaps that touch every later phase):
  - Removed template `Greeting`/`GreetingPreview` from `MainActivity.kt`.
  - Fixed `OnBoardingPage.kt` to render `page.description`.
  - Fixed `OnBoardingScreen.kt` last-page logic (`currentPage == pages.size - 1`).
  - Repaired corrupted `Page.kt` field (`val title:String,l;wes₹₹₹₹`).
- [x] Add one trivial unit test (`SmokeTest`) to prove the harness + CI are wired.

**New files/layers:** `gradle/libs.versions.toml`, `.github/workflows/ci.yml`, `config/detekt/detekt.yml`, `app/src/test/java/.../SmokeTest.kt`.

**Dependencies to add:** junit, mockk, kotlinx-coroutines-test, turbine, truth, hilt-android-testing, detekt-gradle-plugin, ktlint/spotless.

**Testing:** CI green on the smoke test; lint + detekt run clean (with baselines for pre-existing issues).

**Exit criteria:** Opening a PR triggers CI that builds, lints, analyses, and tests; merges blocked on red CI.

**Docs to update:** `CODEBASE.md` (version catalog, CI, test layout), `ARCHITECTURE.md` §7 (remove fixed gaps).

---

# Phase 1 — App foundation (Path A.1–A.3)

**Goal:** Stand up DI, navigation, and the first-launch flow so feature screens have a home.

**Prerequisites:** Phase 0.

**Tasks**
- [x] Create `NewsApplication` annotated `@HiltAndroidApp`; register via `android:name` in the manifest.
- [x] Add `<uses-permission android:name="android.permission.INTERNET"/>` (and `ACCESS_NETWORK_STATE`).
- [x] Base Hilt module(s) under `di/` (`AppModule`).
- [x] **DataStore app-entry flag:** `domain/manager/LocalUserManager` (interface) + `data/manager/LocalUserManagerImpl`; use cases `domain/usecases/app_entry/{ReadAppEntry, SaveAppEntry, AppEntryUseCases}`.
- [x] **Navigation:** `presentation/navgraph/{Route, NavGraph}`; single `NavHost` with app-start + news nested graphs.
- [x] `MainViewModel` + splash logic: keep splash visible until the app-entry flag is read, then choose start destination (Onboarding vs Main).
- [x] **Bottom-nav scaffold** (`presentation/news_navigator/` + `components/NewsBottomNavigation`) with Home / Search / Bookmark placeholders.
- [x] Wire onboarding "Get Started" → `SaveAppEntry` → navigate to Main (clears onboarding from back stack).

**New files/layers:** `NewsApplication.kt`, `di/AppModule.kt`, `domain/manager/`, `data/manager/`, `domain/usecases/app_entry/`, `presentation/navgraph/`, `presentation/main/`.

**Dependencies to add:** none new (hilt, datastore, navigation-compose already present).

**Testing:** unit-test `ReadAppEntry`/`SaveAppEntry` against a fake/in-memory DataStore; route-mapping test; ViewModel start-destination test (Turbine).

**Exit criteria:** First launch shows Onboarding exactly once; later launches go straight to the Home scaffold; bottom nav switches tabs.

**Docs to update:** `CODEBASE.md` (new packages), `ARCHITECTURE.md` (Hilt now wired; startup flow), `FEATURES.md` (#3, #4 → ✅/🟡).

---

# Phase 2 — Source-agnostic data layer (Path A.4 + Path B data)

**Goal:** A pluggable, cached, paged data layer that doesn't care which news provider it talks to.

**Prerequisites:** Phase 1.

**Tasks**
- [x] **Domain models** (pure Kotlin): `Article`, `Source`. No DTO/Android leakage into domain.
- [x] **Repository contract:** `domain/repository/NewsRepository` — `getNews(category)`, `searchNews(query)`, bookmark ops (`upsert`, `delete`, `getArticles`, `getArticle`).
- [x] **Source abstraction:** `data/remote/source/NewsSource` interface with `NewsApiSource` + `GNewsSource` (Retrofit service + DTOs + `toArticleOrNull()` mappers).
- [x] **Runtime source selection:** `NewsSourceProvider` over a Hilt `@IntoMap @StringKey` map of sources (adding a provider = one binding). User-pref wiring is Phase 4.
- [x] **Paging 3:** `NewsPagingSource` parameterized by the active `NewsSource`; `Pager` exposed from the repository.
- [x] **Room cache/bookmarks:** `ArticleEntity`, `NewsDao`, `NewsDatabase` (source flattened — no converter needed). Room bumped to 2.6.1 for Kotlin 1.9 kapt.
- [x] **Repository impl** combining remote paging + Room.
- [x] **DI:** `NetworkModule` (OkHttp + logging + Retrofit per source), `SourceModule` (multibinding), `DatabaseModule`, `RepositoryModule`.
- [x] **Secrets:** per-source keys from `local.properties` → `BuildConfig`; documented in `CODEBASE.md`.

**New files/layers:** `domain/model/`, `domain/repository/`, `data/remote/{dto,api,source}`, `data/local/`, `data/repository/`, expanded `di/`.

**Dependencies to add:** retrofit + converter-gson (present), okhttp logging-interceptor, room (present) + `room-paging`, paging (present).

**Testing:** mapper unit tests; repository tests with fake `NewsSource`s; `NewsPagingSource` load tests; Room DAO instrumented tests; source-switching test.

**Exit criteria:** App fetches and caches a page of articles from the selected source; changing the configured source changes results with no consumer changes.

**Docs to update:** `ARCHITECTURE.md` (data layer realized; source-abstraction design), `CODEBASE.md` (new packages + required API keys), `FEATURES.md` (#5 groundwork).

---

# Phase 3 — Core reading experience (Path A.5)

**Goal:** The full read loop: browse → open → bookmark → search.

**Prerequisites:** Phase 2.

**Tasks**
- [x] **Home feed:** `HomeScreen` + `HomeViewModel` consuming `collectAsLazyPagingItems()`; `ArticleCard` (Coil images); shimmer/loading, empty, and error states. (Pull-to-refresh deferred to Phase 4 polish.)
- [x] **Article detail:** `DetailsScreen` + `DetailsViewModel` with event-based bookmark toggle; open-in-browser. (Share affordance lands in Phase 6.)
- [x] **Search:** `SearchScreen` + `SearchViewModel` with paged results.
- [x] **Bookmarks:** `BookmarkScreen` + `BookmarkViewModel` reading saved articles from Room.
- [x] **Use cases:** `GetNews`, `SearchNews`, `UpsertArticle`, `DeleteArticle`, `SelectArticles`, `SelectArticle` — grouped in a `NewsUseCases` holder.

**New files/layers:** `presentation/home/`, `presentation/details/`, `presentation/search/`, `presentation/bookmark/`, `domain/usecases/news/`, shared `presentation/common/` cards.

**Dependencies to add:** coil-compose (present); `accompanist-swiperefresh` or Material3 pull-refresh.

**Testing:** ViewModel state/event tests (Turbine); bookmark round-trip test; Compose UI tests for Home list + Detail; search-debounce test.

**Exit criteria:** User can scroll an infinite feed, open an article, bookmark/un-bookmark it, see it in Bookmarks, and search.

**Docs to update:** `FEATURES.md` (#5–#9 → ✅), `CODEBASE.md` (feature packages).

---

# Phase 4 — Configurable product features (Path B)

**Goal:** A Settings hub that turns the generic reader into a personalized/niche experience — all opt-in.

**Prerequisites:** Phase 3.

**Tasks**
- [x] **Settings hub:** `SettingsScreen` + `SettingsViewModel` backed by DataStore (`SettingsManager`). (Preferences DataStore used; Proto deferred — typed wrapper is sufficient.)
- [x] **Preference model & flags:** selected data source; personalization on/off; followed categories; category filter; theme (Light/Dark/System); AI-summaries toggle (inert until Phase 5). (Text-size + notification prefs deferred to their phases.)
- [x] **Personalization:** follow-categories UI + toggle; Home chips narrow to followed categories when enabled.
- [x] **Niche/vertical filtering:** category chips on Home; selected category persisted.
- [x] Make `HomeViewModel` reactive to preference changes (feed rebuilds on source/category change).

**New files/layers:** `presentation/settings/`, `domain/usecases/preferences/`, `data/manager/UserPreferences*`, `presentation/foryou/` (or a Home mode).

**Dependencies to add:** `datastore-preferences` (present) and/or `datastore` + protobuf for Proto DataStore.

**Testing:** prefs persistence + migration tests; "feed respects prefs" tests; settings ViewModel tests.

**Exit criteria:** Flipping settings visibly changes the feed/behavior; with everything off the app behaves as a clean generic reader.

**Docs to update:** `FEATURES.md` (#1, #11 + new personalization rows), `ARCHITECTURE.md` (settings-driven feature flags), `CODEBASE.md`.

---

# Phase 5 — AI layer (Path B — AI features)

**Goal:** Optional Claude-powered summaries, "explain simply", and sentiment/topic tags — gated by Settings.

**Prerequisites:** Phase 4 (toggles exist).

> ⚠️ **Production key handling (decide here).** A Claude API key must **not** ship in the APK.
> - **Recommended (production):** a thin **proxy** (serverless, e.g. Cloudflare Worker / small server) holds the key; the app calls the proxy. This edges into Path E — keep it minimal (one endpoint per AI action).
> - **Fallback (portfolio/dev):** **BYO-key** entered in Settings, stored in Android Keystore-encrypted DataStore. Lower effort, not for public release.
> The plan implements the `AiGateway` behind an interface so either backing works without touching the UI.

**Tasks**
- [x] `domain/ai/AiGateway` interface: `summarize(article)` returning `ArticleInsight` (summary + sentiment + tags in one call). (Merged "simplify"/"tag" into one request for cost.)
- [x] `data/ai/ClaudeAiGateway` impl calling Claude (default model `claude-haiku-4-5`; swap to `claude-sonnet-4-6`/`claude-opus-4-8` for quality) via Retrofit; refusal-aware. Implemented per the `claude-api` skill (endpoint, `x-api-key`/`anthropic-version` headers).
- [x] **Result caching** in Room (`AiInsightEntity`) keyed by article URL.
- [x] **UI:** summary card + sentiment + topic chips on article detail, gated by the Phase 4 AI toggle. ("Explain simply" action deferred.)

**New files/layers:** `domain/ai/`, `data/ai/`, Room `AiResultEntity`/DAO, AI UI components.

**Dependencies to add:** Retrofit/OkHttp (present) or Anthropic SDK; (if proxy) none client-side beyond HTTP.

**Testing:** fake `AiGateway`; cache hit/miss tests; toggle-gating tests (no network calls when disabled).

**Exit criteria:** With AI enabled, articles show summaries/tags (served from cache on repeat); disabled = zero AI calls.

**Docs to update:** `FEATURES.md` (AI rows), `ARCHITECTURE.md` (AI layer + key strategy), `CODEBASE.md`.

---

# Phase 6 — Engagement & retention (Path C)

**Goal:** Bring users back: notifications, digest, listen, share, history, accessibility.

**Prerequisites:** Phase 3 (content) + Phase 4 (notification prefs).

**Tasks**
- [ ] **Push (FCM):** breaking-news notifications via topic subscriptions tied to followed categories; runtime `POST_NOTIFICATIONS` permission (API 33+). Sending requires a Firebase project + a trigger (Cloud Function or the same proxy from Phase 5).
- [ ] **Daily digest:** `WorkManager` periodic job → digest notification.
- [ ] **TTS "Listen to article":** Android `TextToSpeech`.
- [ ] **Share** intent; **reading history** (Room); **accessibility** (dynamic font scaling, content descriptions); AMOLED/dark theme polish.

**New files/layers:** `data/notifications/`, `work/` (WorkManager), `presentation/history/`, TTS controller.

**Dependencies to add:** `firebase-bom` + `firebase-messaging`, `androidx.work:work-runtime-ktx`.

**Testing:** WorkManager test harness; notification-permission flow test; history persistence test.

**Exit criteria:** Breaking-news push + daily digest + TTS + share all work; notification permission handled gracefully.

**Docs to update:** `FEATURES.md` (engagement rows), `ARCHITECTURE.md` (background work + messaging), `CODEBASE.md`.

---

# Phase 7 — Production hardening (rest of Path D)

**Goal:** Ship-ready: modular, observable, signed, automated.

**Prerequisites:** Phases 1–6 (or as much as is being released).

**Tasks**
- [ ] **Modularization:** extract `:core` (ui/common, model), `:domain`, `:data`, and `:feature:*` modules; introduce Gradle **convention plugins** for shared config.
- [ ] **Crash reporting & analytics:** Firebase Crashlytics + Analytics (or Sentry).
- [ ] **Release engineering:** signing config via env/CI secrets; R8/ProGuard rules + `isMinifyEnabled = true` + resource shrinking; produce **AAB**; versioning strategy (versionCode automation).
- [ ] **CI/CD extension:** add instrumented tests on an emulator matrix; release workflow builds the AAB and uploads to the Play **internal** track (fastlane or Gradle Play Publisher).
- [ ] **Performance:** Baseline Profiles; verify Compose stability/strong-skipping; StrictMode in debug.
- [ ] **Pre-launch compliance:** privacy policy, Play Data Safety form, store listing assets; bump `targetSdk` to the latest required.

**New files/layers:** `:core`/`:domain`/`:data`/`:feature:*` modules, `build-logic/` convention plugins, `.github/workflows/release.yml`, `app/proguard-rules.pro` (expanded), `baselineprofile/`.

**Dependencies to add:** firebase crashlytics/analytics, `androidx.profileinstaller` + benchmark/baseline-profile, play-publisher/fastlane (CI only).

**Testing:** full unit + instrumented suites green in CI; minified release build smoke-tested; Crashlytics receiving test crash.

**Exit criteria:** CI produces a **signed AAB**; Crashlytics live; modular build green; app passes Play pre-launch report.

**Docs to update:** all of `ARCHITECTURE.md` (module map), `CODEBASE.md` (module layout, release process), `FEATURES.md` (production status).

---

## Dependency additions by phase (summary)

| Phase | Key additions |
|-------|---------------|
| 0 | mockk, coroutines-test, turbine, truth, hilt-testing, detekt, ktlint/spotless |
| 1 | (none new) |
| 2 | okhttp logging-interceptor, room-paging |
| 3 | pull-to-refresh (accompanist or Material3) |
| 4 | proto datastore (optional), protobuf |
| 5 | Anthropic SDK or HTTP-only (+ proxy) |
| 6 | firebase-messaging, work-runtime-ktx |
| 7 | crashlytics, analytics, baseline-profile, profileinstaller, play-publisher (CI) |

## Risks & open decisions

- **AI key hosting (Phase 5):** proxy backend vs BYO-key — proxy required for a real public release; affects Path E scope. *Decide before Phase 5.*
- **Push backend (Phase 6):** sending notifications needs a server/Cloud Function — reuse the Phase 5 proxy if built.
- **Provider limits:** free news API tiers cap requests/day; the source abstraction mitigates lock-in but caching (Phase 2) is essential.
- **Modularize timing (Phase 7):** could be pulled earlier if the team grows; kept late to avoid premature structure.

## Progress tracker

- [x] Phase 0 — Production foundation
- [x] Phase 1 — App foundation
- [x] Phase 2 — Source-agnostic data layer
- [x] Phase 3 — Core reading experience
- [x] Phase 4 — Configurable product features
- [x] Phase 5 — AI layer
- [ ] Phase 6 — Engagement & retention
- [ ] Phase 7 — Production hardening
