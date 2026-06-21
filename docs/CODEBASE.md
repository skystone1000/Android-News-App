# CODEBASE.md

> File-by-file map of the project so you don't have to re-scan the tree each session.
> Read this before opening source files. Last updated: 2026-06-22.
> Update this whenever files are added, removed, moved, or substantially changed (see root `CLAUDE.md`).

## 1. Repository layout

```
Android-News-App/                 (git root, also the working directory)
├── README.md
├── CLAUDE.md                     ← LLM rules (read docs before code; keep docs updated)
├── docs/                         ← project documentation (this folder)
│   ├── ARCHITECTURE.md
│   ├── CODEBASE.md
│   └── FEATURES.md
└── NewsApp/                      ← the Android Studio project (open THIS in the IDE)
    ├── build.gradle.kts          ← root build: plugin versions
    ├── settings.gradle.kts       ← module includes + repositories
    ├── gradle.properties
    ├── gradle/wrapper/gradle-wrapper.properties  ← Gradle version
    └── app/
        ├── build.gradle.kts      ← app module: SDK levels + all dependencies
        └── src/main/
            ├── AndroidManifest.xml
            ├── java/com/example/newsapp/...   (see §3)
            └── res/                            (see §4)
```

## 2. Build / toolchain (authoritative versions)

| Item | Version | File |
|------|---------|------|
| Gradle | 8.11.1 | `NewsApp/gradle/wrapper/gradle-wrapper.properties` |
| Android Gradle Plugin | 8.7.3 | `NewsApp/build.gradle.kts` |
| Kotlin | 1.9.24 | `NewsApp/build.gradle.kts` |
| Compose Compiler ext | 1.5.14 | `NewsApp/app/build.gradle.kts` |
| Compose BOM | 2024.09.00 | `NewsApp/app/build.gradle.kts` |
| Hilt | 2.51.1 | both build files |
| compileSdk / targetSdk / minSdk | 35 / 33 / 24 | `NewsApp/app/build.gradle.kts` |
| JDK for builds | 21 (Android Studio JBR) | no system `java`; CLI builds must `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"` |

Build from CLI: `cd NewsApp && ./gradlew :app:assembleDebug`
(set `JAVA_HOME` first as above). Output APK: `NewsApp/app/build/outputs/apk/debug/app-debug.apk`.

**Dependency versions are centralized** in `NewsApp/gradle/libs.versions.toml` (Gradle
version catalog). Add/bump dependencies there, then reference via `libs.*` in the build files.

**Quality tooling (Phase 0):**
- Static analysis: **detekt** (`NewsApp/config/detekt/detekt.yml`, pre-existing issues in `baseline.xml`). Run `./gradlew detekt`.
- Unit tests live in `NewsApp/app/src/test/`; stack = JUnit4 + MockK + Turbine + Truth + coroutines-test. Run `./gradlew :app:testDebugUnitTest`.
- CI: `.github/workflows/ci.yml` runs detekt + lint + unit tests + `assembleDebug` on every PR/push to `main` (JDK 17).

## 3. Source files (`NewsApp/app/src/main/java/com/example/newsapp/`)

| File | Purpose |
|------|---------|
| `NewsApplication.kt` | `@HiltAndroidApp` Application — DI entry point (registered in manifest). |
| `MainActivity.kt` | `@AndroidEntryPoint` single activity. Splash held via `MainViewModel.splashCondition`; renders `NavGraph(startDestination)`. |
| `util/Constants.kt` | App-wide constant keys (`USER_SETTINGS`, `APP_ENTRY`). |
| `di/AppModule.kt` | Hilt `@Module` (SingletonComponent): provides `LocalUserManager` + `AppEntryUseCases`. |
| `domain/manager/LocalUserManager.kt` | Pure-Kotlin contract for local user state (app-entry flag). |
| `data/manager/LocalUserManagerImpl.kt` | DataStore Preferences impl of `LocalUserManager`. |
| `domain/usecases/app_entry/` | `ReadAppEntry`, `SaveAppEntry`, `AppEntryUseCases` holder. |
| `presentation/MainViewModel.kt` | `@HiltViewModel` — resolves start destination + splash hold from the app-entry flag. |
| `presentation/navgraph/Route.kt` | Sealed route/destination definitions. |
| `presentation/navgraph/NavGraph.kt` | Top-level `NavHost`: app-start (onboarding) + news nested graphs. |
| `presentation/onboarding/OnBoardingViewModel.kt` + `OnBoardingEvent.kt` | Onboarding `@HiltViewModel` + event(s) (`SaveAppEntry`). |
| `presentation/news_navigator/NewsNavigator.kt` | Bottom-nav `Scaffold` + nested `NavHost` (Home/Search/Bookmark placeholder tabs). |
| `presentation/news_navigator/components/` | `NewsBottomNavigation` bar + `BottomNavigationItem`. |
| `presentation/common/PlaceholderScreen.kt` | Temporary stand-in for tab screens (replaced in Phase 3). |
| `presentation/Dimens.kt` | `object Dimens` — all spacing/size constants (paddings, indicator size, icon sizes, article card/image sizes). |
| `presentation/common/NewsButton.kt` | Reusable `NewsButton` (filled) and `NewsTextButton` (text) composables. |
| `presentation/onboarding/OnBoardingScreen.kt` | Onboarding screen: `HorizontalPager` over `pages`, page indicator, Back/Next/Get-Started buttons driven by `derivedStateOf`. Navigation onClick is a stub. |
| `presentation/onboarding/Page.kt` | `data class Page(title, description, @DrawableRes image)` + the `pages` list (3 placeholder Lorem-Ipsum pages using `onboarding1/2/3.png`). |
| `presentation/onboarding/components/OnBoardingPage.kt` | Single onboarding page UI (image + title + text). NOTE: currently shows `title` twice (description bug). |
| `presentation/onboarding/components/PageIndicator.kt` | Row of circular dots; highlights the selected page. |
| `ui/theme/Color.kt` | Color palette. Brand: `Blue 0xFF1877F2` (primary), `Black 0xFF1C1E21`, plus error/surface/gray tones (`BlueGray`, `WhiteGray`). |
| `ui/theme/Theme.kt` | `NewsAppTheme` Material3 theme + status bar handling. |
| `ui/theme/Type.kt` | Typography definitions. |

### Data layer (Phase 2)
| File / package | Purpose |
|------|---------|
| `domain/model/{Article,Source}.kt` | Pure-Kotlin domain models. |
| `domain/repository/NewsRepository.kt` | Single repository contract (paged news/search + bookmark CRUD). |
| `data/remote/dto/{NewsApiDto,GNewsDto}.kt` | Per-provider wire DTOs + `toArticleOrNull()` mappers. |
| `data/remote/api/{NewsApiService,GNewsService}.kt` | Retrofit service bindings (+ `BASE_URL`). |
| `data/remote/source/NewsSource.kt` | Provider-agnostic source contract. |
| `data/remote/source/{NewsApiSource,GNewsSource}.kt` | Concrete sources (service + API key → domain). |
| `data/remote/source/NewsSourceProvider.kt` | Resolves the active source from the Hilt source map. |
| `data/remote/NewsPagingSource.kt` | Paging 3 source (headlines or search), de-dupes by URL. |
| `data/local/{ArticleEntity,NewsDao,NewsDatabase,ArticleMapper}.kt` | Room bookmark store + entity↔domain mapping. |
| `data/repository/NewsRepositoryImpl.kt` | Pager over the active source + Room-backed bookmarks. |
| `di/{NetworkModule,SourceModule,DatabaseModule,RepositoryModule}.kt` | Hilt wiring; `SourceModule` uses `@IntoMap @StringKey` multibinding. |

**Required API keys** (in `NewsApp/local.properties`, git-ignored): `NEWS_API_KEY`, `GNEWS_API_KEY`.
Exposed to code as `BuildConfig.NEWS_API_KEY` / `BuildConfig.GNEWS_API_KEY` (default `""`).

### Presentation — reading features (Phase 3)
| File / package | Purpose |
|------|---------|
| `domain/usecases/news/NewsUseCases.kt` | `GetNews`, `SearchNews`, `UpsertArticle`, `DeleteArticle`, `SelectArticles`, `SelectArticle` + holder. |
| `di/UseCaseModule.kt` | Provides `NewsUseCases`. |
| `presentation/home/{HomeScreen,HomeViewModel}.kt` | Paged headlines feed. |
| `presentation/search/` | `SearchScreen`/`SearchViewModel`/`SearchState`/`SearchEvent` + `components/SearchBar`. |
| `presentation/details/` | `DetailsScreen`/`DetailsViewModel`/`DetailsEvent` + `components/DetailsTopBar` (listen/share/bookmark/open-in-browser) + `ArticleSpeaker.kt` (TTS wrapper). |
| `presentation/bookmark/` | `BookmarkScreen`/`BookmarkViewModel`/`BookmarkState` (Room-backed list). |
| `presentation/common/` | `ArticleCard`, `ArticlesList` (+ paging-state handling), `ShimmerEffect`, `EmptyScreen`. |

Domain models implement `java.io.Serializable` so an `Article` can pass through Compose
navigation via `savedStateHandle` (pure JVM, keeps the domain Android-free).

### Settings & personalization (Phase 4)
| File / package | Purpose |
|------|---------|
| `domain/model/UserSettings.kt` | `UserSettings`, `ThemeMode`, `NewsCategories`, source-id constants. |
| `domain/manager/SettingsManager.kt` | Contract for reading/persisting user settings. |
| `data/manager/SettingsManagerImpl.kt` | DataStore impl (separate store: `news_user_settings`). |
| `presentation/settings/` | `SettingsScreen`/`SettingsViewModel`/`SettingsEvent` (theme, source, follows, toggles). |
| `presentation/home/components/CategoryChips.kt` | Horizontal category selector on Home. |
| `NewsApplication.kt` | Now also syncs `NewsSourceProvider.activeSourceId` from settings. |
| `MainViewModel.kt` | Now also exposes `themeMode`; `MainActivity` applies it to `NewsAppTheme`. |

### AI layer (Phase 5)
| File / package | Purpose |
|------|---------|
| `domain/ai/AiGateway.kt` | `AiGateway` contract + `ArticleInsight` (summary/sentiment/tags). |
| `data/ai/ClaudeService.kt` + `dto/ClaudeDto.kt` | Retrofit binding for Anthropic Messages API. |
| `data/ai/ClaudeAiGateway.kt` | Builds prompt, parses JSON, handles `refusal`, caches by URL. Model `claude-haiku-4-5`. |
| `data/local/AiInsightEntity.kt` + `AiInsightDao.kt` | Room cache (DB bumped to v2). |
| `di/AiModule.kt` | Provides `ClaudeService` (base URL = `CLAUDE_PROXY_URL` or Anthropic) + `AiGateway`. |
| `presentation/details/AiInsightState.kt` + `components/AiInsightCard.kt` | AI card UI; `DetailsViewModel.loadInsightIfEnabled` gates on the Settings toggle. |

Extra API keys in `local.properties`: `CLAUDE_API_KEY` (dev), optional `CLAUDE_PROXY_URL` (prod base URL).

### Where remaining layers will go (planned, not yet created)
- `work/`, `data/notifications/` (Phase 6)

## 4. Resources (`NewsApp/app/src/main/res/`)

| Group | Contents |
|-------|----------|
| `drawable/` | App icons + UI icons: `ic_back_arrow, ic_bookmark, ic_close, ic_home, ic_logo, ic_network, ic_network_error, ic_preferences, ic_search, ic_search_document, ic_splash, ic_time`; onboarding images `onboarding1/2/3.png`; launcher background/foreground. |
| `values/strings.xml` | Only `app_name = NewsApp`. (Most UI strings are hardcoded in composables — candidate for extraction.) |
| `values/colors.xml` | Semantic color resources incl. `display_small`, `text_medium`. |
| `values/themes.xml`, `values/splash.xml`, `values-night/splash.xml` | App theme + splash (light/dark). |
| `xml/` | `backup_rules.xml`, `data_extraction_rules.xml`. |
| `mipmap-anydpi-v26/` | Adaptive launcher icons. |

## 5. Manifest notes (`AndroidManifest.xml`)

- Single `<activity .MainActivity>` as launcher; theme `@style/App.Starting.Theme`.
- **No** `android:name` on `<application>` → Hilt Application not registered yet.
- **No** `<uses-permission android:name="android.permission.INTERNET"/>` → add before networking.
