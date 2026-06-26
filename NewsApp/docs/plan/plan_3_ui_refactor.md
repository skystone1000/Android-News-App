# plan_3_ui_refactor.md (UI_REFACTOR_PLAN) — "Brief" redesign

> Plan to reskin the app to the **Brief** design system (see `Brief Figma/`) and then
> rename the app + package. Read `ARCHITECTURE.md` and `CODEBASE.md` first.
> Last updated: 2026-06-25.
>
> **Status: Phase 1 (UI reskin) — DONE.** Foundation (BriefColors/BriefTheme/Type/Dimens),
> shared components, all screens, the new Data Sources screen, and onboarding are reskinned;
> `assembleDebug` + `detekt` + unit tests + `lintDebug` all green.
> **Phase 2 (rename) — DONE.** App renamed to **Briefly**, package/applicationId
> `com.skystone1000.briefly`, launcher icon swapped to the emerald three-bar mark;
> `assembleDebug` + `detekt` + unit tests green. (Final brand name = "Briefly", not the
> working title "Brief" used in the plan below.)

## 0. Summary & decisions

A **full visual reskin** plus a later **rename**. Business logic (ViewModels, use cases,
data layer, nav graph) is unchanged; only the theme layer, `Dimens`, and composable
styling change — plus a few **new structural UI elements** the mockups introduce.

Decisions locked in:

| Topic | Decision |
|-------|----------|
| Phase 1 fidelity | **Pixel-match all mockups** (incl. trending searches + browse-topics + counts) |
| Data Sources UI | **Split into its own screen** (Settings row → new route) |
| Onboarding | **Restyle to Brief** (emerald + new fonts + wordmark) |
| Brief logo / launcher icon | **Defer to Phase 2** (ships with rename) |
| Fonts | **Bundle `.ttf`** from Google Fonts in `res/font/` (offline, no runtime fetch) |
| Saved "Unread" filter | Originally planned **inert**; **superseded** — now functional, deriving read-state from reading history (`readUrls`). |

Two honest gaps from the "pixel-match" choice, implemented as **curated/static** content
rather than inventing a backend (called out where they occur below):
1. **Trending searches** and **browse-topics counts** have no data source.
2. ~~**Saved "Unread"** has no read-state — filter is inert.~~ **Resolved:** the Saved screen now
   treats articles already in reading history as "read", so the Unread filter is functional.

---

## Phase 1 — UI reskin

### 1. Design-system foundation (the token layer)

The Figma palette is richer than Material3's `colorScheme` (3 text tiers, accent-soft /
accent-line, border, divider, chipBg, placeholder). Use a **custom token layer over
Material3** rather than bending everything into `colorScheme`.

#### 1a. `ui/theme/BriefColors.kt` (new)

```kotlin
data class BriefColors(
    val bg: Color, val surface: Color, val card: Color,
    val border: Color, val divider: Color,
    val text: Color, val textSec: Color, val textTer: Color,
    val accent: Color, val accentSoft: Color, val accentLine: Color, val onAccent: Color,
    val chipBg: Color, val placeholder: Color, val placeholderText: Color,
    val good: Color, val warn: Color, val toggleOff: Color, val navBg: Color,
)

val LightBriefColors = BriefColors(
    bg = Color(0xFFFAFAF9), surface = Color(0xFFFFFFFF), card = Color(0xFFFFFFFF),
    border = Color(0xFFE7E5E4), divider = Color(0xFFF1EFEC),
    text = Color(0xFF1C1917), textSec = Color(0xFF57534E), textTer = Color(0xFFA8A29E),
    accent = Color(0xFF047857), accentSoft = Color(0xFFECFDF5),
    accentLine = Color(0xFFC7EBD9), onAccent = Color(0xFFFFFFFF),
    chipBg = Color(0xFFF4F4F2), placeholder = Color(0xFFEEEDE9),
    placeholderText = Color(0xFFB6B0A8),
    good = Color(0xFF047857), warn = Color(0xFFB45309),
    toggleOff = Color(0xFFD9D6D1), navBg = Color(0xFFFFFFFF),
)

val DarkBriefColors = BriefColors(
    bg = Color(0xFF0B0F0E), surface = Color(0xFF14181A), card = Color(0xFF161B1D),
    border = Color(0xFF272D2F), divider = Color(0xFF1E2426),
    text = Color(0xFFF4F4F3), textSec = Color(0xFFA6AFAB), textTer = Color(0xFF6B746F),
    accent = Color(0xFF10B981), accentSoft = Color(0x2210B981), // ~13% alpha
    accentLine = Color(0x4710B981), onAccent = Color(0xFF04231A),
    chipBg = Color(0xFF1C2123), placeholder = Color(0xFF1E2426),
    placeholderText = Color(0xFF48524C),
    good = Color(0xFF34D399), warn = Color(0xFFF59E0B),
    toggleOff = Color(0xFF2B3235), navBg = Color(0xFF0F1413),
)

val LocalBriefColors = staticCompositionLocalOf { LightBriefColors }
```

#### 1b. `ui/theme/Theme.kt` → `BriefTheme`

Replace `NewsAppTheme` with `BriefTheme`. It provides `LocalBriefColors`, sets a minimal
Material3 `colorScheme` (so ripples/selection defaults stay sane), keeps the existing
status-bar handling, and keeps `darkTheme` driven by the current `ThemeMode` setting from
`MainViewModel`.

```kotlin
object BriefTheme {
    val colors: BriefColors @Composable get() = LocalBriefColors.current
}

@Composable
fun BriefTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkBriefColors else LightBriefColors
    val scheme = if (darkTheme)
        darkColorScheme(background = colors.bg, surface = colors.card, primary = colors.accent)
    else
        lightColorScheme(background = colors.bg, surface = colors.card, primary = colors.accent)
    // status bar -> colors.bg, light/dark icons per theme
    CompositionLocalProvider(LocalBriefColors provides colors) {
        MaterialTheme(colorScheme = scheme, typography = BriefTypography, content = content)
    }
}
```

Update call site in `MainActivity.kt` (`NewsAppTheme(...)` → `BriefTheme(...)`).

#### 1c. `ui/theme/Type.kt` — Schibsted Grotesk + Hanken Grotesk

- Add `.ttf` files to `res/font/`: `schibsted_grotesk_{medium,semibold,bold,extrabold}.ttf`
  and `hanken_grotesk_{regular,medium,semibold,bold}.ttf` (from Google Fonts).
- Remove Poppins (`poppins_*.ttf`) once unreferenced.
- Define families + the 6-step scale (from the Figma type card):

| Style | Family / weight | Size | Tracking / line-height |
|-------|-----------------|------|------------------------|
| Display | Schibsted 800 | 34 | −0.035em |
| Headline | Schibsted 700 | 22 | −0.025em / 1.2 |
| Title | Schibsted 600 | 16 | −0.015em |
| Body | Hanken 400 | 14 | / 1.6 |
| Label | Hanken 600 | 12 | — |
| Overline | Hanken 700 | 10 | +0.09em |

Map onto Material3 `Typography` (e.g. `displaySmall`, `headlineMedium`, `titleMedium`,
`bodyMedium`, `labelLarge`, `labelSmall`) and expose as `BriefTypography`.

#### 1d. `presentation/Dimens.kt` — extend

Add: card radii (14 / 16 / 18 dp), chip radius 11dp, button radius 12dp, screen padding
18–20dp, section gaps, thumbnail 66dp, featured image 152dp, hero image 196dp, avatar
30dp, toggle 38×22dp. Keep existing names that are still used.

#### 1e. `ui/theme/Color.kt`

Remove the old `Blue / Black / BlueGray / WhiteGray / DarkRed / LightRed` constants once no
file references them (do this last, after screens migrate).

---

### 2. Shared component library (`presentation/common/`)

Build these first so screens compose cleanly. Each reads `BriefTheme.colors` / `BriefTypography`.

| Component | File | Notes |
|-----------|------|-------|
| `BriefButton` / `BriefSecondaryButton` / `BriefTextButton` | replace `NewsButton.kt` | filled emerald / surface+border / emerald text |
| `CategoryTabRow` | `common/CategoryTabRow.kt` (new) | scrollable underline tabs (Home) |
| `BriefChip` | `common/BriefChip.kt` (new) | pill chip, filled vs outlined variants |
| `BriefToggle` | `common/BriefToggle.kt` (new) | 38×22 emerald switch |
| `SegmentedControl` | `common/SegmentedControl.kt` (new) | Light/Dark/System (Settings) |
| `ArticleRow` | `common/ArticleRow.kt` (new) | kicker+time, Schibsted title, source, right 66dp thumb |
| `FeaturedCard` | `common/FeaturedCard.kt` (new) | hero image + badge + headline + source row |
| `SectionHeader` | `common/SectionHeader.kt` (new) | 11px tracked overline label |
| `ShimmerEffect`, `EmptyScreen` | existing | re-theme to stone/emerald tokens |

`ArticleCard.kt` / `ArticlesList.kt` are refactored to use `ArticleRow` + `FeaturedCard`.

---

### 3. Screen-by-screen

All screens keep their existing ViewModel/state wiring; only composables change.

| Screen | File(s) | Changes |
|--------|---------|---------|
| **Home** | `home/HomeScreen.kt`, `home/components/` | Logo+wordmark header + avatar; `CategoryTabRow` (Top/World/Business/Tech/Culture/Sports → `NewsCategories`, "Top" = no filter); first paged item → `FeaturedCard`; rest → `ArticleRow` with top dividers. Replaces `CategoryChips.kt`. |
| **Article** | `details/DetailsScreen.kt`, `details/components/` | Top bar (back + share/bookmark/translate icons); 196dp hero; emerald kicker + read-time; Schibsted headline; source row; restyled `AiInsightCard` (accentSoft bg, accentLine border, "AI" badge); Hanken body. |
| **Search** | `search/SearchScreen.kt`, `search/components/` | "Search" display title; pill search field; **TRENDING NOW** pills (curated static list → runs search); **BROWSE TOPICS** 2-col grid of categories (counts from a static map, decorative). |
| **Saved** | `bookmark/BookmarkScreen.kt` | "Saved" title + article count; All/Unread filter pills (**Unread now functional** via reading history); `ArticleRow` left-thumbnail + filled bookmark icon. |
| **Settings** | `settings/SettingsScreen.kt` | "Settings" title; APPEARANCE `SegmentedControl`; **Data sources row** → navigates to new screen; PREFERENCES card with `BriefToggle`s; FOLLOWED CATEGORIES chips. Data-source cards move out (see below). |
| **Data Sources** (new) | `settings/datasources/DataSourcesScreen.kt` (new) | New `Route` + `NavGraph`/`NewsNavigator` entry; back + title; per-provider cards (status, `UsageMeter`, key input, Save/Clear/Get-a-key, radio select). Move `DataSourceCard.kt` + `UsageMeter.kt` here; reuse existing `SettingsViewModel` events (`SetApiKey`/`ClearApiKey`) or a small dedicated VM. |
| **Onboarding** | `onboarding/` | Re-theme to emerald + Schibsted/Hanken; Brief wordmark; `PageIndicator` emerald. (Logo art still old until Phase 2.) |
| **Bottom nav** | `news_navigator/components/` | 4 line-icon tabs, emerald active / textTer inactive, surface bg + top divider. |

**Static-content details (pixel-match gaps):**
- `Search` trending: `val TrendingTerms = listOf("Nippon Steel", "World Cup", ...)` constant;
  tap → `SearchEvent.UpdateSearchQuery` + search.
- `Search` browse-topics: iterate `NewsCategories`; show name + a count from a static
  `Map<String,Int>` (decorative; add a `// TODO: real counts` note).
- `Saved` Unread: now wired — `BookmarkViewModel` observes reading history and `BookmarkScreen`
  filters out saved articles whose URL is already in `readUrls` (no longer a no-op).

---

### 4. Phase 1 build order (each step compiles)

1. Foundation: fonts + `BriefColors` + `BriefTheme` + `Type` + `Dimens`; swap
   `NewsAppTheme`→`BriefTheme` at call sites. App builds, half-migrated look.
2. Shared components (§2).
3. Screens one at a time (§3), each verified to build.
4. New Data Sources route + Search trending/topics.
5. Onboarding reskin.
6. Remove dead old color/font constants.
7. Verify: `./gradlew :app:assembleDebug`, `detekt`, `:app:testDebugUnitTest` green.
8. Docs in sync (per root `CLAUDE.md`): `ARCHITECTURE.md` (theme/token layer + new screen),
   `CODEBASE.md` (file map: new/renamed files), `FEATURES.md`, `DATASOURCES.md` (Data
   Sources moved to its own screen), `plan_1_roadmap.md`.

---

## Phase 2 — Rename to "Briefly" + package `com.skystone1000.briefly` — DONE

Done as its own change after Phase 1 (kept the reskin diff reviewable). Final brand name
landed as **Briefly** (working title in this plan was "Brief").

1. ✅ **App name**: `res/values/strings.xml` → `app_name = Briefly`. Theme style
   `Theme.NewsApp` → `Theme.Briefly` (themes/splash).
2. ✅ **Gradle**: `app/build.gradle.kts` → `namespace`/`applicationId` =
   `com.skystone1000.briefly`; the `mockAppId` device-path constant updated to match. (New
   `applicationId` = effectively a new app install; no data migration needed.)
3. ✅ **Package move**: `java/com/example/newsapp/` → `java/com/skystone1000/briefly/`
   (`git mv` of `main`/`test`/`androidTest` trees + scripted rewrite of `package`/`import`
   lines across 162 files).
4. ✅ **References**: `proguard-rules.pro` keep-rules + `config/detekt/baseline.xml` entries
   rewritten; manifest uses relative `.MainActivity`/`.NewsApplication` (resolve via
   namespace). A stale-kapt `clean` was needed before the first green Hilt build.
5. ✅ **Brand art**: launcher icon (`ic_launcher_background` emerald + `ic_launcher_foreground`
   three-bar mark) now mirrors `BriefMark`; in-app `BriefWordmark` text → "Briefly". The old
   `ic_logo` drawable is unused legacy art (left in place).
6. ✅ **Verify**: clean `assembleDebug` + `detekt` + `testDebugUnitTest` green; APK badging
   confirms `package=com.skystone1000.briefly`, `label=Briefly`.
7. ✅ **Docs**: package-root + app-name references updated in `ARCHITECTURE.md` / `CODEBASE.md`.

> **Note:** domain class names that legitimately describe a news app (`NewsApplication`,
> `NewsRepository`, `NewsDao`, `NewsBottomNavigation`, etc.) were intentionally **not**
> renamed — the app is still a news app, just branded "Briefly". Only app-identity tokens
> (app name, package, theme style) changed.

---

## Token quick-reference (from Figma)

| Token | Light | Dark |
|-------|-------|------|
| bg | `#FAFAF9` | `#0B0F0E` |
| surface / card | `#FFFFFF` | `#14181A` / `#161B1D` |
| border | `#E7E5E4` | `#272D2F` |
| divider | `#F1EFEC` | `#1E2426` |
| text | `#1C1917` | `#F4F4F3` |
| textSec | `#57534E` | `#A6AFAB` |
| textTer | `#A8A29E` | `#6B746F` |
| accent | `#047857` | `#10B981` |
| accentSoft | `#ECFDF5` | `rgba(16,185,129,.13)` |
| accentLine | `#C7EBD9` | `rgba(16,185,129,.28)` |
| onAccent | `#FFFFFF` | `#04231A` |
| chipBg | `#F4F4F2` | `#1C2123` |
| warn | `#B45309` | `#F59E0B` |

Fonts: **Schibsted Grotesk** (display/headline/title) · **Hanken Grotesk** (body/label/overline).
