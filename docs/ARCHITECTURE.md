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
| Dependency Injection | **Hilt** (`@HiltAndroidApp`, `@Module`, `@HiltViewModel`) | dependency present, **not wired yet** (no Application class, no modules) |
| Navigation | **Navigation-Compose** single-activity NavHost | planned (placeholders in onboarding) |
| Networking | **Retrofit + Gson** against a news API (e.g. newsapi.org) | dependency present, not implemented |
| Paging | **Paging 3** (`paging-compose`) for infinite article lists | dependency present, not implemented |
| Local cache / bookmarks | **Room** | dependency present, not implemented |
| First-launch / user prefs | **DataStore Preferences** | dependency present, not implemented |
| Image loading | **Coil** (`coil-compose`) | dependency present, not used yet |
| System bars | **Accompanist systemuicontroller** + edge-to-edge | partially used (theme) |
| Splash | **Core SplashScreen API** (`installSplashScreen`) | implemented |

## 4. App startup flow (current)

`MainActivity.onCreate` → edge-to-edge (`setDecorFitsSystemWindows(false)`) →
`installSplashScreen()` → `setContent { NewsAppTheme { OnBoardingScreen() } }`.

There is currently **no navigation graph**: `MainActivity` shows `OnBoardingScreen`
directly. Intended flow once built:
`Splash → (first launch?) → Onboarding → Home (paged articles) → Detail → Bookmarks/Search`.

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

- Hilt is not initialized (no `@HiltAndroidApp` Application, not in manifest).
- No `INTERNET` permission in the manifest (required before networking works).
- No domain or data layer exists yet.
- Navigation is stubbed (`// Navigate to home` placeholder in `OnBoardingScreen`).
- `OnBoardingScreen` button logic checks `currentPage == 3` but there are only 3 pages (indices 0–2), so "Get Started" currently does nothing.
- `OnBoardingPage` renders `page.title` twice instead of `page.title` + `page.description`.
- `MainActivity` still has the template `Greeting`/`GreetingPreview` boilerplate.

See [CODEBASE.md](CODEBASE.md) for the file-by-file map and [FEATURES.md](FEATURES.md) for feature status.
