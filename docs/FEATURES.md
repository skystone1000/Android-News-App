# FEATURES.md

> What the app does today and what's planned. Read this to understand product scope.
> Last updated: 2026-06-22. Update the status table whenever a feature lands or changes.

## Status legend
✅ Done · 🟡 Partial / stubbed · ⛔ Not started (dependency may exist)

## 1. Feature status

| # | Feature | Status | Notes |
|---|---------|--------|-------|
| 1 | App launch / Splash screen | ✅ | `installSplashScreen()` in `MainActivity`; light + dark splash resources. |
| 2 | Onboarding carousel | ✅ | 3-page `HorizontalPager`, indicator, Back/Next/Get-Started. "Get Started" saves the app-entry flag and navigates to the main graph. (Copy is still placeholder Lorem Ipsum.) |
| 3 | First-launch detection (show onboarding once) | ✅ | DataStore app-entry flag via `LocalUserManager`; `MainViewModel` picks the start destination. |
| 4 | Navigation graph (single-activity) | ✅ | `NavGraph` with app-start + news nested graphs; bottom-nav `NewsNavigator` (tab screens are placeholders until Phase 3). |
| 5 | Home feed — breaking/top news, infinite scroll | ✅ | `HomeScreen` + `HomeViewModel`; paged via `ArticlesList` with shimmer/empty/error states. |
| 6 | Article detail view | ✅ | `DetailsScreen` (image, title, content) + bookmark toggle + open-in-browser + share + listen (TTS). |
| 18 | Share article | ✅ | `ACTION_SEND` chooser from the detail top bar (title + URL). |
| 19 | Listen to article (TTS) | ✅ | `ArticleSpeaker` wraps Android `TextToSpeech`; play/stop toggle in the detail top bar. |
| 20 | Reading history | ✅ | Opening an article records it (Room `reading_history`); `HistoryScreen` (reached from Settings → Activity) lists it newest-first with a clear-all action. |
| 21 | Daily digest notification | ✅ | `WorkManager` daily job (`DailyDigestWorker`) fetches top headlines → digest notification; channel + `POST_NOTIFICATIONS` runtime request handled. |
| 22 | Push notifications (FCM) | ⛔ | Deferred — needs a Firebase project + `google-services.json` and a server trigger (cannot be provisioned in this environment). |
| 23 | Release optimization (R8) | ✅ | `isMinifyEnabled`/`isShrinkResources` on release + full `proguard-rules.pro`; APK ~13 MB → ~2.8 MB. Release APK is **unsigned** (signing/AAB deferred — needs a keystore). |
| 24 | StrictMode (debug) | ✅ | Thread + VM policies (`penaltyLog`) in debug builds to catch main-thread I/O and leaks. |
| 7 | Search articles | ✅ | `SearchScreen` + `SearchViewModel`; paged search results. |
| 8 | Bookmark / save articles | ✅ | `BookmarkScreen` from Room; toggle from detail. |
| 9 | Image loading | ✅ | Coil `AsyncImage` in cards + detail. |
| 10 | Dependency injection wiring | ✅ | Hilt wired: `NewsApplication`, `di/AppModule`, `@HiltViewModel`s. |
| 11 | Theming (light/dark, Material3) | ✅ | `NewsAppTheme`, brand palette, edge-to-edge; theme mode (Light/Dark/System) user-selectable in Settings. |
| 13 | Settings hub | ✅ | `SettingsScreen` (4th tab); DataStore-backed `SettingsManager`. |
| 14 | Category / niche filtering | ✅ | Category chips on Home; selected category drives the feed. |
| 15 | Personalized feed | ✅ | Follow categories + personalization toggle; chips narrow to followed categories. |
| 16 | Runtime data-source selection | ✅ | Pick NewsAPI / GNews in Settings; feed rebuilds via `NewsSourceProvider`. **Expanding** (see `MULTI_SOURCE_PLAN.md`): data layer now also supports NewsData.io, Currents, Mediastack; in-app per-provider API-key entry + usage meters land in the Settings-UI phase. Mediastack free tier is HTTP-only (scoped cleartext config). |
| 17 | AI summaries / sentiment / tags | ✅ | Claude-powered `AiGateway`; card on article detail, gated by the AI toggle; cached in Room. (Dev key in BuildConfig; proxy for prod.) |
| 12 | Reusable UI kit | ✅ | `NewsButton`, `PageIndicator`, `ArticleCard`, `ArticlesList`, `ShimmerEffect`, `EmptyScreen`, `SearchBar`, `Dimens`. |

## 2. Current user-facing behavior

Launching the app shows the splash, then the **Onboarding** screen. The user can
swipe/press Next through 3 pages. Pressing "Get Started" on the last page currently
does nothing (navigation not wired). There is no home feed, search, or bookmarks yet.

## 3. Intended product (target)

A news reader where a user can: see top headlines in an infinite-scroll feed,
open an article to read it, search for articles by keyword, and bookmark articles
for offline access — built on a public news API (e.g. newsapi.org) with a free API key.

## 4. Build order recommended for remaining features

1. Wire Hilt (`NewsApplication` + manifest + base modules) and add `INTERNET` permission.
2. DataStore "app entry" manager → first-launch logic; finish onboarding navigation.
3. Navigation graph + bottom-nav scaffold (Home / Search / Bookmark).
4. Data layer: Retrofit `NewsApi`, Paging source, repository + use cases.
5. Home feed (paged list) → Article detail.
6. Search (paged) → Bookmarks (Room).

See [ARCHITECTURE.md](ARCHITECTURE.md) for layer design and [CODEBASE.md](CODEBASE.md) for where each piece goes.
