# ARCHITECTURE.md

> High-level design of the NewsApp. Read this before reading code.
> Last updated: 2026-06-24 · Keep in sync with the codebase (see root `CLAUDE.md`).

## 1. Summary

NewsApp is a single-module Android app built with **Jetpack Compose** following
**Clean Architecture + MVVM**. It is a **functional multi-source news reader**: onboarding,
a bottom-nav main app (Home/Search/Saved/Settings), article detail, configurable settings,
5 pluggable news providers with in-app encrypted API keys + usage meters, optional Claude AI
summaries, engagement features (share/TTS/reading-history/daily digest), and release hardening
(R8). Remaining work is externally gated (FCM, Crashlytics, signing/Play, modularization).

- Single Gradle module: `:app`
- Package root: `com.example.newsapp`
- UI: 100% Jetpack Compose (no XML layouts, no Fragments)
- Min SDK 24 · target SDK 33 · compile SDK 35

## 2. Target architecture (the intended end-state)

Clean Architecture with three layers. Dependencies point **inward**
(`presentation → domain ← data`); the domain layer knows nothing about Android.

```
┌─────────────────────────────────────────────┐
│ presentation/   (Compose UI + ViewModels)    │  ← Android, Compose, Hilt
│   screens, components, navigation, theme     │
└───────────────┬─────────────────────────────┘
                │ calls use cases
┌───────────────▼─────────────────────────────┐
│ domain/         (pure Kotlin)                │  ← no Android deps
│   model/ , repository interfaces , usecases/ │
└───────────────▲─────────────────────────────┘
                │ implemented by
┌───────────────┴─────────────────────────────┐
│ data/           (remote + local + repo impl) │  ← Retrofit, Room, DataStore
│   remote (Retrofit API + Paging),            │
│   local (Room DAO/DB), repository impl,      │
│   manager (DataStore for first-launch flag)  │
└──────────────────────────────────────────────┘
```

### Layer responsibilities

| Layer | Holds | Android-aware? |
|-------|-------|----------------|
| `presentation` | Composable screens, `ViewModel`s, UI state, navigation graph, theme, reusable components | Yes |
| `domain` | Plain data models, repository **interfaces**, use cases (one action each) | No (pure Kotlin) |
| `data` | Repository **implementations**, Retrofit service, Paging sources, Room DB/DAO, DataStore manager, DI mappers | Yes |

## 3. Cross-cutting concerns

| Concern | Approach / Library | Status |
|---------|--------------------|--------|
| Dependency Injection | **Hilt** (`@HiltAndroidApp`, `@Module`, `@HiltViewModel`) | **wired** (`NewsApplication`, `di/AppModule`, `MainViewModel`/`OnBoardingViewModel`) |
| Navigation | **Navigation-Compose** single-activity NavHost | **implemented** (`navgraph/NavGraph` + bottom-nav `NewsNavigator`: Home/Search/Saved/Settings tabs + Details/History/DataSources routes) |
| Networking | **Retrofit + Gson** behind a pluggable `NewsSource` (NewsAPI, NewsData.io, GNews, Currents, Mediastack) | **implemented** (source-agnostic; runtime-selectable via `NewsSourceProvider`; Mediastack is HTTP-only via scoped cleartext config) |
| API keys | User-entered per provider, **encrypted at rest** (`ApiKeyStore` / `EncryptedSharedPreferences`) | **implemented** — read per-call by each source; entered in Settings; dev seed from `BuildConfig`; keys redacted from debug logs |
| Quota usage | On-device per-provider request counting vs free-tier limit (`ApiUsageStore`) | **implemented** — DataStore counters with daily/monthly windows; meter in Settings |
| Paging | **Paging 3** (`paging-compose`) for infinite article lists | **implemented** (`NewsPagingSource` keyed on a provider-agnostic **string cursor**: numeric page / offset / opaque token) |
| Local cache / bookmarks | **Room** 2.6.1 (`ArticleEntity`, `NewsDao`, `NewsDatabase`) | **implemented** (bookmark store; consumed by repository) |
| First-launch / user prefs | **DataStore Preferences** | **implemented** (`LocalUserManager` app-entry + `SettingsManager` user settings: source, category, theme, follows, toggles) |
| Image loading | **Coil** (`coil-compose`) | **implemented** (`AsyncImage` in article cards/detail) |
| System bars | **Accompanist systemuicontroller** + edge-to-edge | partially used (theme) |
| Splash | **Core SplashScreen API** (`installSplashScreen`) | implemented |

## 4. App startup flow (current)

`MainActivity` (`@AndroidEntryPoint`) → `installSplashScreen()` kept on screen while
`MainViewModel` reads the DataStore app-entry flag → `MainViewModel.startDestination`
selects `AppStartNavigation` (Onboarding) or `NewsNavigation` (main) → `NavGraph` renders.
Pressing **Get Started** saves the flag and navigates to the main graph (clearing onboarding
from the back stack). The main graph hosts `NewsNavigator` (bottom nav: Home/Search/Saved/Settings,
plus full-screen Details and History routes). On startup `NewsApplication` also seeds dev API keys
from `BuildConfig`, ensures the notification channel, and schedules the daily digest worker;
`MainActivity` requests `POST_NOTIFICATIONS` on API 33+.

## 5. Key conventions

- **Design system: "Brief"** (emerald accent, warm-stone neutrals, Schibsted + Hanken
  Grotesk). Tokens live in `ui/theme/BriefColors.kt` (`BriefColors` data class +
  `Light/DarkBriefColors`), provided by the `BriefTheme` composable via `LocalBriefColors`
  and read through `BriefTheme.colors` — **not** Material3's `colorScheme` (which is kept
  minimal for ripple/default coherence). Type scale is `BriefTypography` in `ui/theme/Type.kt`.
- **Dimensions** live in `presentation/Dimens.kt` (no magic numbers in composables).
- **Reusable composables** go in `presentation/common/` (`ArticleRow`, `FeaturedCard`,
  `BriefChip`, `BriefToggle`, `SegmentedControl`, `CategoryTabRow`, `BriefButton`s, `BriefLogo`…).
- **Per-feature folders** under `presentation/<feature>/` with a `components/` subfolder for that feature's private composables.
- The **Brief brand mark** is drawn in Compose (`BriefLogo.kt`); the launcher icon / drawable
  swap and the app rename (`com.skystone1000.brief`) are **Phase 2** (see `UI_REFACTOR_PLAN.md`).

## 6. Known gaps / tech debt (as of 2026-06-22)

- **Claude AI key is dev-only**: `CLAUDE_API_KEY` ships in `BuildConfig`. Production must move it off-device behind a proxy (`CLAUDE_PROXY_URL` is supported as the base URL) — see ROADMAP Phase 5.
- **Externally gated, deferred:** FCM push, Crashlytics/Analytics, release signing + AAB + Play publishing, modularization, baseline profiles — need a Firebase project / keystore / Play account / devices (ROADMAP Phase 6–7). Release builds are currently **unsigned**.
- Mediastack's free tier is **HTTP-only** (cleartext scoped to its host); upgrade for TLS.
- Onboarding copy is still placeholder Lorem Ipsum.

_News-provider API keys are entered in-app and encrypted (`ApiKeyStore`); `local.properties`/`BuildConfig` keys are only an optional dev seed._
_Fixed in Phase 0: template boilerplate removed; `OnBoardingPage` shows `description`; corrupted `Page.kt` repaired; page logic corrected._
_Done in Phase 1: Hilt wired; permissions added; DataStore app-entry flag; navigation graph + bottom-nav scaffold._
_Done in Phase 2: source-agnostic data layer — domain models, `NewsRepository`, pluggable `NewsSource` (NewsAPI/GNews) via Hilt map-multibinding, Paging 3, Room bookmarks, DI modules, BuildConfig API keys._
_Done in Phase 3: core reading — `NewsUseCases`; Home feed, Search, Article detail (bookmark toggle + open-in-browser), Bookmarks; shared `ArticleCard`/`ArticlesList`/`ShimmerEffect`/`EmptyScreen`; Coil images; article passed to detail via `Serializable` + savedStateHandle._
_Done in Phase 4: `SettingsManager` (DataStore) + Settings tab — data-source selection (wired to `NewsSourceProvider`), theme mode (Light/Dark/System), category chips on Home, follow-categories + personalization toggle, AI-summaries toggle. Home feed reacts to source/category changes._
_Done in Phase 5: AI layer — `AiGateway` contract + `ClaudeAiGateway` (Anthropic Messages API, model `claude-haiku-4-5`, refusal-aware), Room cache (`AiInsightEntity`), AI summary/sentiment/tags card on the detail screen gated by the Settings toggle. Key via `BuildConfig` (dev) or `CLAUDE_PROXY_URL` (prod)._
_Done in Phase 6: engagement — share + TTS "listen" on detail; reading history (Room `reading_history` + History screen); WorkManager daily digest + notification channel + `POST_NOTIFICATIONS`. (FCM push deferred.)_
_Done in Phase 7: hardening — R8 minify + resource shrinking + ProGuard rules; StrictMode in debug._
_Done (multi-source): 5 providers behind a cursor-based `NewsSource`; in-app encrypted API keys (`ApiKeyStore`); on-device free-tier usage meters (`ApiUsageStore`); key-redacting HTTP logger. See [DATASOURCES.md](DATASOURCES.md)._

See [CODEBASE.md](CODEBASE.md) for the file-by-file map, [DATASOURCES.md](DATASOURCES.md) for providers/keys, and [FEATURES.md](FEATURES.md) for feature status.
