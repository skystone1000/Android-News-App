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
| 4 | Navigation graph (single-activity) | ✅ | `NavGraph` with app-start + news nested graphs; bottom-nav `NewsNavigator` (Home/Search/Bookmark/Settings + Details/History routes). |
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
| 16 | Runtime data-source selection | ✅ | Pick from 5 providers (NewsAPI.org, NewsData.io, GNews, Currents, Mediastack) in Settings; picker gated to sources with a saved key; feed rebuilds via `NewsSourceProvider`. Mediastack free tier is HTTP-only (scoped cleartext config). |
| 25 | In-app API keys (encrypted) | ✅ | Per-provider masked key entry in Settings, stored via `EncryptedSharedPreferences`; "Get a key" deep-links to each provider's signup. Clearing the active source's key falls back to another configured source. |
| 26 | Free-tier usage meter | ✅ | Per-provider `used / limit` + colour bar + reset time in Settings (`UsageMeter`); on-device estimate, reconciled from rate-limit headers when present. |
| 17 | AI summaries / sentiment / tags | ✅ | Claude-powered `AiGateway`; card on article detail, gated by the AI toggle; cached in Room. (Dev key in BuildConfig; proxy for prod.) |
| 12 | Reusable UI kit | ✅ | `NewsButton`, `PageIndicator`, `ArticleCard`, `ArticlesList`, `ShimmerEffect`, `EmptyScreen`, `SearchBar`, `Dimens`. |

## 2. Current user-facing behavior

First launch shows the splash → **Onboarding** (3-page pager); "Get Started" saves the app-entry
flag and enters the main app. Returning users go straight in. The main app is a bottom-nav
single-activity with four tabs:

- **Home** — infinite-scroll headline feed with category chips; tap an article to open it.
- **Search** — paged keyword search.
- **Bookmark** — saved articles (Room).
- **Settings** — data source + API keys (5 providers, with usage meters), theme (Light/Dark/System),
  followed categories + personalization, AI summaries toggle, and reading history.

The **article detail** screen shows the image/title/content with bookmark, open-in-browser, share,
listen (TTS), and — when enabled — a Claude-generated summary/sentiment/tags card. A daily
**WorkManager digest** notification surfaces top headlines. To fetch news the user must add at least
one provider's API key in Settings.

## 3. Product status

The intended product is **built**: a configurable, multi-source news reader (Paths A–D) with
in-app encrypted API keys, optional AI features, engagement features (share/TTS/history/digest), and
release hardening (R8). Remaining items are externally gated (FCM push, Crashlytics, release
signing/Play publishing, modularization) — see [ROADMAP.md](ROADMAP.md).

## 4. Where to read more

- Data sources & API-key handling → [DATASOURCES.md](DATASOURCES.md)
- Layer design & conventions → [ARCHITECTURE.md](ARCHITECTURE.md)
- File-by-file map & toolchain → [CODEBASE.md](CODEBASE.md)
- Phased plans & status → [ROADMAP.md](ROADMAP.md), [MULTI_SOURCE_PLAN.md](MULTI_SOURCE_PLAN.md)

See [ARCHITECTURE.md](ARCHITECTURE.md) for layer design and [CODEBASE.md](CODEBASE.md) for where each piece goes.
