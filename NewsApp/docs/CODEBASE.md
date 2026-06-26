# CODEBASE.md

> File-by-file map of the project so you don't have to re-scan the tree each session.
> Read this before opening source files. Last updated: 2026-06-25.
> Update this whenever files are added, removed, moved, or substantially changed (see root `CLAUDE.md`).

## 1. Repository layout

```
Android-News-App/                 (git root, also the working directory)
├── README.md
├── CLAUDE.md                     ← LLM rules (read docs before code; keep docs updated)
└── NewsApp/                      ← the Android Studio project (open THIS in the IDE)
    ├── docs/                     ← project documentation (this folder)
    │   ├── ARCHITECTURE.md
    │   ├── CODEBASE.md
    │   ├── FEATURES.md
    │   ├── DATASOURCES.md        ← news providers + API-key/usage handling
    │   └── plan/                 ← phased build plans (sequential by creation date)
    │       ├── plan_1_roadmap.md         ← master phased Path A→D plan + status
    │       ├── plan_2_multi_source.md    ← multi-provider + in-app keys plan
    │       ├── plan_3_ui_refactor.md     ← Brief reskin + rename to Briefly
    │       ├── plan_4_debug_mode.md      ← in-app debug/mock tooling
    │       └── plan_5_remaining_work.md  ← remaining work, gaps & known issues
    ├── build.gradle.kts          ← root build: plugin versions
    ├── settings.gradle.kts       ← module includes + repositories
    ├── gradle.properties
    ├── gradle/wrapper/gradle-wrapper.properties  ← Gradle version
    └── app/
        ├── build.gradle.kts      ← app module: SDK levels + all dependencies
        └── src/main/
            ├── AndroidManifest.xml
            ├── java/com/skystone1000/briefly/...   (see §3)
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

Key libraries (all via the catalog): Hilt, Retrofit + Gson + OkHttp, Coil, Paging 3, Room 2.6.1
(kapt), DataStore Preferences, Navigation-Compose, `material-icons-core`,
`androidx.work:work-runtime-ktx` (daily digest), `androidx.security:security-crypto` (encrypted
API keys). Release builds use **R8** (`isMinifyEnabled` + `isShrinkResources`) with
`app/proguard-rules.pro`.

**Quality tooling (Phase 0):**
- Static analysis: **detekt** (`NewsApp/config/detekt/detekt.yml`, pre-existing issues in `baseline.xml`). Run `./gradlew detekt`.
- Unit tests live in `NewsApp/app/src/test/`; stack = JUnit4 + MockK + Turbine + Truth + coroutines-test. Run `./gradlew :app:testDebugUnitTest`.
- CI: `.github/workflows/ci.yml` runs detekt + lint + unit tests + `assembleDebug` on every PR/push to `main` (JDK 17).

## 3. Source files (`NewsApp/app/src/main/java/com/skystone1000/briefly/`)

| File | Purpose |
|------|---------|
| `NewsApplication.kt` | `@HiltAndroidApp` Application — DI entry point (registered in manifest). |
| `MainActivity.kt` | `@AndroidEntryPoint` single activity. Splash held via `MainViewModel.splashCondition`; computes dark theme from `MainViewModel.themeMode`; requests `POST_NOTIFICATIONS` (API 33+); renders `NavGraph(startDestination)`. |
| `util/Constants.kt` | App-wide constant keys (`USER_SETTINGS`, `APP_ENTRY`). |
| `di/AppModule.kt` | Hilt `@Module` (SingletonComponent): provides `LocalUserManager`, `AppEntryUseCases`, `SettingsManager`. (Other modules: `NetworkModule`, `SourceModule`, `DatabaseModule`, `RepositoryModule`, `UseCaseModule`, `AiModule`, `SecurityModule`, `UsageModule`.) |
| `domain/manager/LocalUserManager.kt` | Pure-Kotlin contract for local user state (app-entry flag). |
| `data/manager/LocalUserManagerImpl.kt` | DataStore Preferences impl of `LocalUserManager`. |
| `domain/usecases/app_entry/` | `ReadAppEntry`, `SaveAppEntry`, `AppEntryUseCases` holder. |
| `presentation/MainViewModel.kt` | `@HiltViewModel` — resolves start destination + splash hold from the app-entry flag. |
| `presentation/navgraph/Route.kt` | Sealed route/destination definitions. |
| `presentation/navgraph/NavGraph.kt` | Top-level `NavHost`: app-start (onboarding) + news nested graphs. |
| `presentation/onboarding/OnBoardingViewModel.kt` + `OnBoardingEvent.kt` | Onboarding `@HiltViewModel` + event(s) (`SaveAppEntry`). |
| `presentation/news_navigator/NewsNavigator.kt` | Bottom-nav `Scaffold` + nested `NavHost` (Home/Search/Bookmark/Settings tabs + Details/History routes); article passed via `savedStateHandle`. |
| `presentation/news_navigator/components/` | `NewsBottomNavigation` bar + `BottomNavigationItem`. |
| `presentation/Dimens.kt` | `object Dimens` — all spacing/size/radii constants (incl. Brief design-system tokens: card/chip/button radii, screen padding, thumbnail/hero sizes, toggle dims). |
| `presentation/common/NewsButton.kt` | `BriefButton` (filled emerald), `BriefSecondaryButton`, `BriefTextButton` + legacy `NewsButton`/`NewsTextButton` aliases (restyled, used by onboarding). |
| `presentation/common/BriefComponents.kt` | Small Brief primitives: `BriefChip`, `BriefToggle`, `SegmentedControl`, `SectionHeader`, `BriefScreenTitle`. |
| `presentation/common/ArticleRow.kt` | Compact list item (kicker/time overline, Schibsted title, source, 66dp thumbnail; optional leading thumb + trailing slot). |
| `presentation/common/FeaturedCard.kt` | Hero card for the top article (image + badge + headline + source row). |
| `presentation/common/CategoryTabRow.kt` | Scrollable underline category tabs (Home). |
| `presentation/common/BriefLogo.kt` | `BriefMark` (emerald three-bar mark, drawn in Compose) + `BriefWordmark` ("Briefly"). The launcher icon (`mipmap`/`ic_launcher_*`) mirrors this mark. |
| `presentation/onboarding/OnBoardingScreen.kt` | Onboarding screen: `HorizontalPager` over `pages`, page indicator, Back/Next/Get-Started (last page fires `SaveAppEntry` → enters the main graph). Restyled to Brief. |
| `presentation/onboarding/Page.kt` | `data class Page(title, description, @DrawableRes image)` + the `pages` list (3 pages using `onboarding1/2/3.png`; copy still placeholder). |
| `presentation/onboarding/components/OnBoardingPage.kt` | Single onboarding page UI (image + title + description); Brief theme colors. |
| `presentation/onboarding/components/PageIndicator.kt` | Row of circular dots; highlights the selected page (emerald). |
| `ui/theme/Color.kt` | Intentionally minimal — all colors live in `BriefColors.kt` and are read via `BriefTheme.colors`. |
| `ui/theme/BriefColors.kt` | **Brief design-system token set**: `BriefColors` data class + `Light/DarkBriefColors` (warm-stone neutrals, emerald accent), `LocalBriefColors`, `BriefTheme.colors` accessor. |
| `ui/theme/Theme.kt` | `BriefTheme` composable: provides `LocalBriefColors` + a minimal Material3 colorScheme + status-bar handling. (Was `NewsAppTheme`.) |
| `ui/theme/Type.kt` | `BriefTypography` — Schibsted Grotesk (display/headline/title) + Hanken Grotesk (body/label/overline), loaded per-weight from bundled variable fonts via `FontVariation`. |

### Data layer (Phase 2)
| File / package | Purpose |
|------|---------|
| `domain/model/{Article,Source}.kt` | Pure-Kotlin domain models. |
| `domain/repository/NewsRepository.kt` | Single repository contract (paged news/search + bookmark CRUD). |
| `data/remote/dto/*` | Per-provider wire DTOs + `toArticleOrNull()` mappers (NewsAPI, GNews originally; NewsData/Currents/Mediastack added later — see "Multi-source" below). |
| `data/remote/api/*` | Retrofit service bindings (+ `BASE_URL`) for all 5 providers. |
| `data/remote/source/NewsSource.kt` | Provider-agnostic source contract (now returns `NewsPage`; string cursor). |
| `data/remote/source/*Source.kt` | Concrete sources; read the key per call from `ApiKeyStore`. |
| `data/remote/source/NewsSourceProvider.kt` | Resolves the active source from the Hilt source map. |
| `data/remote/NewsPagingSource.kt` | Paging 3 source (headlines or search), string-cursor keyed, de-dupes by URL. |
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
| `presentation/bookmark/` | `BookmarkScreen` ("Saved": title + count, All/Unread filter, `ArticleRow`)/`BookmarkViewModel`/`BookmarkState`. Room-backed list; **Unread** filters out saved articles whose URL appears in reading history (`readUrls`), so the filter is functional (not inert). |
| `presentation/common/` | `ArticleRow`, `FeaturedCard`, `ArticlesList` (+ paging-state handling), `ShimmerEffect`, `EmptyScreen`. (`ArticleCard` removed.) |

Domain models implement `java.io.Serializable` so an `Article` can pass through Compose
navigation via `savedStateHandle` (pure JVM, keeps the domain Android-free).

### Settings & personalization (Phase 4)
| File / package | Purpose |
|------|---------|
| `domain/model/UserSettings.kt` | `UserSettings`, `ThemeMode`, `NewsCategories`, source-id constants. |
| `domain/manager/SettingsManager.kt` | Contract for reading/persisting user settings. |
| `data/manager/SettingsManagerImpl.kt` | DataStore impl (separate store: `news_user_settings`). |
| `presentation/settings/` | `SettingsScreen` (segmented theme control, data-sources entry row, preferences card, followed chips) + `DataSourcesScreen` (own screen: per-provider key/usage cards) / `SettingsViewModel`/`SettingsEvent`. |
| Home category selector | Now the `CategoryTabRow` underline tabs (was `home/components/CategoryChips.kt`, removed). |
| `NewsApplication.kt` | Now also syncs `NewsSourceProvider.activeSourceId` from settings. |
| `MainViewModel.kt` | Now also exposes `themeMode`; `MainActivity` applies it to `NewsAppTheme`. |

### AI layer (Phase 5)
| File / package | Purpose |
|------|---------|
| `domain/ai/AiGateway.kt` | `AiGateway` contract + `ArticleInsight` (summary/sentiment/tags). |
| `data/ai/ClaudeService.kt` + `dto/ClaudeDto.kt` | Retrofit binding for Anthropic Messages API. |
| `data/ai/ClaudeAiGateway.kt` | Builds prompt, parses JSON, handles `refusal`, caches by URL. Model `claude-haiku-4-5`. |
| `data/local/AiInsightEntity.kt` + `AiInsightDao.kt` | Room AI-insight cache (DB now v3; see Phase 6). |
| `di/AiModule.kt` | Provides `ClaudeService` (base URL = `CLAUDE_PROXY_URL` or Anthropic) + `AiGateway`. |
| `presentation/details/AiInsightState.kt` + `components/AiInsightCard.kt` | AI card UI; `DetailsViewModel.loadInsightIfEnabled` gates on the Settings toggle. |

Extra API keys in `local.properties`: `CLAUDE_API_KEY` (dev), optional `CLAUDE_PROXY_URL` (prod base URL).

### Engagement (Phase 6)
| File / package | Purpose |
|------|---------|
| `presentation/details/ArticleSpeaker.kt` | `TextToSpeech` wrapper (play/stop toggle, lifecycle-safe shutdown) behind `rememberArticleSpeaker()`. |
| `presentation/details/components/DetailsTopBar.kt` | Listen + Share actions added (icons via `material-icons-core`). |
| `data/local/ReadingHistoryEntity.kt` + `ReadingHistoryDao.kt` | Room `reading_history` store + entity↔domain mapping (DB bumped to v3). |
| `domain/usecases/news/` (`RecordHistory`/`GetHistory`/`ClearHistory`) | History use cases on `NewsRepository`. |
| `presentation/history/` | `HistoryScreen` + `HistoryViewModel`; reached from Settings → Activity, navigates to detail, clear-all. |
| `data/notifications/NewsNotifier.kt` | Notification channel + digest notification helper (`NotificationManagerCompat`). |
| `work/DailyDigestWorker.kt` + `DigestScheduler.kt` | `CoroutineWorker` (deps via Hilt `EntryPoint`) fetching headlines → digest; scheduled once/day from `NewsApplication`. |
| `MainActivity.kt` | Requests `POST_NOTIFICATIONS` (API 33+) on launch. |

FCM push is **deferred** — it needs a Firebase project (`google-services.json`) + a server
trigger that can't be provisioned in this environment (see `plan/plan_1_roadmap.md` Phase 6). The local
engagement surface (channel, runtime permission, WorkManager digest) is built.

### Production hardening (Phase 7)
| File / package | Purpose |
|------|---------|
| `app/build.gradle.kts` (release) | `isMinifyEnabled` + `isShrinkResources` = true (R8). |
| `app/proguard-rules.pro` | Keep rules for Gson DTOs, `Serializable` domain models, Retrofit services. |
| `NewsApplication.enableStrictMode()` | Debug-only thread + VM `StrictMode` policies. |

Release builds are currently **unsigned** (`app-release-unsigned.apk`). Signing/AAB,
Crashlytics, modularization, baseline profiles, and Play publishing are deferred — they
need a keystore / Firebase / Play account / devices (see `plan/plan_1_roadmap.md` Phase 7).

### Multi-source + in-app API keys (in progress — see `plan/plan_2_multi_source.md`)
| File / package | Purpose |
|------|---------|
| `domain/model/SourceQuota.kt` | `SourceQuota(limit, period)` + `QuotaPeriod` (drives the usage meter). |
| `domain/model/SourceCatalog.kt` | `SourceMetadata` + `SourceCatalog.ALL_SOURCES` (5 providers: newsapi/newsdata/gnews/currents/mediastack). |
| `domain/security/ApiKeyStore.kt` | Contract for per-provider, user-entered keys (read at call time). |
| `data/security/EncryptedApiKeyStore.kt` | `EncryptedSharedPreferences` (Keystore master key) impl; `keys()` via `MutableStateFlow`. |
| `di/SecurityModule.kt` | Provides `ApiKeyStore`. |
| `data/remote/source/NewsSource.kt` | Contract now returns `NewsPage(articles, nextCursor)` and takes a `cursor: String?` (numeric/offset/token-agnostic). |
| `data/remote/source/PageCursor.kt` | `pageOf`/`nextPageCursor` helpers for numeric-page providers. |
| `data/remote/source/MissingApiKeyException.kt` | Thrown when a source has no configured key → surfaces as a paging `LoadResult.Error`. |
| `data/remote/NewsPagingSource.kt` | Keyed on `String?` cursor (forward-only). `NewsApiSource`/`GNewsSource` read keys per call from `ApiKeyStore`. |
| `data/remote/{api,dto,source}` NewsData/Currents/Mediastack | 3 providers: NewsData.io (token cursor), Currents (numeric), Mediastack (offset, **HTTP-only**). Each = service + DTO/mapper + source + `NetworkModule` Retrofit + `SourceModule` binding. |
| `res/xml/network_security_config.xml` | Cleartext allowed **only** for `api.mediastack.com` (free tier has no TLS); referenced from the manifest. |
| `domain/usage/ApiUsageStore.kt` + `QuotaWindow.kt` | Usage contract + `UsageSnapshot`; daily/monthly window math (pure). |
| `data/usage/ApiUsageStoreImpl.kt` | Per-provider request counts in a DataStore (`api_usage`), rolling the window on day/month boundaries. |
| `data/remote/UsageInterceptor.kt` | OkHttp interceptor: host→sourceId, fire-and-forget count; reconciles `x-ratelimit-remaining`. Added to the client in `NetworkModule`. |
| `di/UsageModule.kt` | Provides `ApiUsageStore`. |
| `presentation/settings/SettingsScreen.kt` | "Data sources & API keys" section driven by `SourceCatalog`; `SettingsViewModel` exposes `configuredSourceIds` + `usage`; new `SetApiKey`/`ClearApiKey` events (clearing the active key falls back). |
| `presentation/settings/components/DataSourceCard.kt` + `UsageMeter.kt` | Per-provider card (masked key field, get-a-key link, active selector) + usage bar. |
| `data/remote/RedactingLoggingInterceptor.kt` | Debug-only request logger that **masks key query params** (OkHttp 4.x can't redact those). Replaces `HttpLoggingInterceptor` in `NetworkModule`. |
| `presentation/common/EmptyScreen.kt` | Adds a `MissingApiKeyException` message ("Add one in Settings → Data sources"). |
| `NewsApplication.seedDevKeysFromBuildConfig()` | Dev convenience: seeds `ApiKeyStore` from `BuildConfig` keys if unset (no-op in prod). |
| `app/proguard-rules.pro` | `-dontwarn` for Tink's optional deps (ErrorProne/Google-API-client/Joda) + keep `com.google.crypto.tink.**`. Release (R8) verified green. |

### Debug-mode tooling (debug builds only — see `plan/plan_4_debug_mode.md`)
| File / package | Purpose |
|------|---------|
| `domain/debug/{DebugConfig,DebugSettingsStore}.kt` | Debug flags model + contract (save / offline / force-error / latency). |
| `data/debug/DebugSettingsStoreImpl.kt` | DataStore (`debug_settings`) impl. |
| `data/debug/DebugConfigHolder.kt` | Mirrors latest config into an `AtomicReference` for synchronous reads on the network thread. |
| `data/debug/MockStore.kt` | Capture (`getExternalFilesDir/mock/<key>/NNN.json`) + round-robin replay + assets fallback; pure `mockKey()`. |
| `data/remote/MockInterceptor.kt` | First OkHttp interceptor: capture/offline-replay/force-error/latency (debug + provider hosts only); before `UsageInterceptor`. |
| `data/debug/RequestLog.kt` | In-memory ring buffer (last 100) for the request inspector. |
| `data/debug/DebugActions.kt` | `clearMocks()` + `resetAppState()` (wipes Room, keys, settings/usage/debug stores). |
| `di/DebugModule.kt` | Provides `DebugSettingsStore`. |
| `presentation/debug/` | `DebugScreen`/`DebugViewModel` (toggles + actions), `RequestLogScreen`/`RequestLogViewModel`. Reached from Settings → "Developer options" (debug only). |
| `app/build.gradle.kts` (tasks) | `pullMocks` / `clearDeviceMocks` / `seedMockAssets` adb/copy helpers. |

## 4. Resources (`NewsApp/app/src/main/res/`)

| Group | Contents |
|-------|----------|
| `drawable/` | App icons + UI icons: `ic_back_arrow, ic_bookmark, ic_close, ic_home, ic_logo (unused legacy art), ic_network, ic_network_error, ic_preferences, ic_search, ic_search_document, ic_splash, ic_time`; onboarding images `onboarding1/2/3.png`; launcher `ic_launcher_background` (emerald) + `ic_launcher_foreground` (Briefly three-bar mark). |
| `values/strings.xml` | Only `app_name = Briefly`. (Most UI strings are hardcoded in composables — candidate for extraction.) |
| `font/` | `schibsted_grotesk_variable.ttf`, `hanken_grotesk_variable.ttf` (bundled variable fonts; Poppins removed). |
| `values/colors.xml` | Legacy semantic color resources (`display_small`, `text_medium`) — now unused by UI (colors come from `BriefColors.kt`); candidates for removal. |
| `values/themes.xml`, `values/splash.xml`, `values-night/splash.xml` | App theme + splash (light/dark). |
| `xml/` | `backup_rules.xml`, `data_extraction_rules.xml`. |
| `mipmap-anydpi-v26/` | Adaptive launcher icons. |

## 5. Manifest notes (`AndroidManifest.xml`)

- Single `<activity .MainActivity>` as launcher; theme `@style/App.Starting.Theme`.
- **No** `android:name` on `<application>` → Hilt Application not registered yet.
- **No** `<uses-permission android:name="android.permission.INTERNET"/>` → add before networking.
