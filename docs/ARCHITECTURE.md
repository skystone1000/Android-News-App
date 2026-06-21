# ARCHITECTURE.md

> High-level design of the NewsApp. Read this before reading code.
> Last updated: 2026-06-21 · Keep in sync with the codebase (see root `CLAUDE.md`).

## 1. Summary

NewsApp is a single-module Android app built with **Jetpack Compose** following
**Clean Architecture + MVVM**. It is in an **early stage**: the UI shell
(Splash + Onboarding) is implemented; the data/domain layers and feature screens
are scaffolded by dependencies but **not yet written**.

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
| Navigation | **Navigation-Compose** single-activity NavHost | **implemented** (`navgraph/NavGraph` + bottom-nav `NewsNavigator`; tab screens are placeholders until Phase 3) |
| Networking | **Retrofit + Gson** behind a pluggable `NewsSource` (NewsAPI + GNews) | **implemented** (source-agnostic; runtime-selectable via `NewsSourceProvider`) |
| Paging | **Paging 3** (`paging-compose`) for infinite article lists | **implemented** (`NewsPagingSource` + repository `Pager`) |
| Local cache / bookmarks | **Room** 2.6.1 (`ArticleEntity`, `NewsDao`, `NewsDatabase`) | **implemented** (bookmark store; consumed by repository) |
| First-launch / user prefs | **DataStore Preferences** | **implemented** (`LocalUserManager` app-entry flag + `app_entry` use cases) |
| Image loading | **Coil** (`coil-compose`) | **implemented** (`AsyncImage` in article cards/detail) |
| System bars | **Accompanist systemuicontroller** + edge-to-edge | partially used (theme) |
| Splash | **Core SplashScreen API** (`installSplashScreen`) | implemented |

## 4. App startup flow (current)

`MainActivity` (`@AndroidEntryPoint`) → `installSplashScreen()` kept on screen while
`MainViewModel` reads the DataStore app-entry flag → `MainViewModel.startDestination`
selects `AppStartNavigation` (Onboarding) or `NewsNavigation` (main) → `NavGraph` renders.
Pressing **Get Started** saves the flag and navigates to the main graph (clearing onboarding
from the back stack). The main graph hosts `NewsNavigator` (bottom nav: Home/Search/Bookmark —
placeholder screens until Phase 3).

## 5. Intended startup flow (planned)

1. `NewsApplication` (`@HiltAndroidApp`) — register in manifest via `android:name`.
2. Splash decides start destination by reading a DataStore "app_entry" flag.
3. First launch → Onboarding (writes the flag on "Get Started") → Navigator (bottom nav: Home, Search, Bookmark).
4. Otherwise → straight to Navigator.

## 6. Key conventions

- **Dimensions** live in `presentation/Dimens.kt` (no magic numbers in composables).
- **Colors** live in `ui/theme/Color.kt`; semantic color resources in `res/values/colors.xml`.
- **Reusable composables** go in `presentation/common/`.
- **Per-feature folders** under `presentation/<feature>/` with a `components/` subfolder for that feature's private composables.

## 7. Known gaps / tech debt (as of 2026-06-21)

- No **Settings** screen yet — category selection, source selection, personalization, and AI toggles are unimplemented. — *Phase 4*
- `NewsSourceProvider.activeSourceId` is hard-coded to the default; user-driven source selection comes with Settings. — *Phase 4*
- API keys must be supplied in `local.properties` (`NEWS_API_KEY`, `GNEWS_API_KEY`); empty keys make remote calls fail at runtime.

_Fixed in Phase 0: template boilerplate removed; `OnBoardingPage` shows `description`; corrupted `Page.kt` repaired; page logic corrected._
_Done in Phase 1: Hilt wired; permissions added; DataStore app-entry flag; navigation graph + bottom-nav scaffold._
_Done in Phase 2: source-agnostic data layer — domain models, `NewsRepository`, pluggable `NewsSource` (NewsAPI/GNews) via Hilt map-multibinding, Paging 3, Room bookmarks, DI modules, BuildConfig API keys._
_Done in Phase 3: core reading — `NewsUseCases`; Home feed, Search, Article detail (bookmark toggle + open-in-browser), Bookmarks; shared `ArticleCard`/`ArticlesList`/`ShimmerEffect`/`EmptyScreen`; Coil images; article passed to detail via `Serializable` + savedStateHandle._

See [CODEBASE.md](CODEBASE.md) for the file-by-file map and [FEATURES.md](FEATURES.md) for feature status.
