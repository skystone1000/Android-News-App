# FEATURES.md

> What the app does today and what's planned. Read this to understand product scope.
> Last updated: 2026-06-21. Update the status table whenever a feature lands or changes.

## Status legend
✅ Done · 🟡 Partial / stubbed · ⛔ Not started (dependency may exist)

## 1. Feature status

| # | Feature | Status | Notes |
|---|---------|--------|-------|
| 1 | App launch / Splash screen | ✅ | `installSplashScreen()` in `MainActivity`; light + dark splash resources. |
| 2 | Onboarding carousel | 🟡 | 3-page `HorizontalPager` with indicator and Back/Next buttons. Content is placeholder Lorem Ipsum; "Get Started" does not navigate; page description not shown (renders title twice). |
| 3 | First-launch detection (show onboarding once) | ⛔ | Needs DataStore flag + start-destination logic. |
| 4 | Navigation graph (single-activity) | ⛔ | Navigation-Compose present; no NavHost yet. |
| 5 | Home feed — breaking/top news, infinite scroll | ⛔ | Needs Retrofit API + Paging 3 + Home screen/ViewModel. |
| 6 | Article detail view | ⛔ | Open full article (in-app WebView or formatted detail). |
| 7 | Search articles | ⛔ | Query the news API; debounced search + paged results. |
| 8 | Bookmark / save articles | ⛔ | Room-backed; bookmarks list screen. |
| 9 | Image loading | ⛔ | Coil dependency present; used once article UI exists. |
| 10 | Dependency injection wiring | ⛔ | Hilt present but no `@HiltAndroidApp` / modules. |
| 11 | Theming (light/dark, Material3) | ✅ | `NewsAppTheme`, brand palette, edge-to-edge system bars. |
| 12 | Reusable UI kit | 🟡 | `NewsButton`/`NewsTextButton`, `PageIndicator`, `Dimens`. Grows per feature. |

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
