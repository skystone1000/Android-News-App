# CODEBASE.md

> File-by-file map of the project so you don't have to re-scan the tree each session.
> Read this before opening source files. Last updated: 2026-06-21.
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

## 3. Source files (`NewsApp/app/src/main/java/com/example/newsapp/`)

| File | Purpose |
|------|---------|
| `MainActivity.kt` | Single activity. Edge-to-edge + `installSplashScreen()`, sets `NewsAppTheme { OnBoardingScreen() }`. Still contains template `Greeting`/`GreetingPreview`. |
| `presentation/Dimens.kt` | `object Dimens` — all spacing/size constants (paddings, indicator size, icon sizes, article card/image sizes). |
| `presentation/common/NewsButton.kt` | Reusable `NewsButton` (filled) and `NewsTextButton` (text) composables. |
| `presentation/onboarding/OnBoardingScreen.kt` | Onboarding screen: `HorizontalPager` over `pages`, page indicator, Back/Next/Get-Started buttons driven by `derivedStateOf`. Navigation onClick is a stub. |
| `presentation/onboarding/Page.kt` | `data class Page(title, description, @DrawableRes image)` + the `pages` list (3 placeholder Lorem-Ipsum pages using `onboarding1/2/3.png`). |
| `presentation/onboarding/components/OnBoardingPage.kt` | Single onboarding page UI (image + title + text). NOTE: currently shows `title` twice (description bug). |
| `presentation/onboarding/components/PageIndicator.kt` | Row of circular dots; highlights the selected page. |
| `ui/theme/Color.kt` | Color palette. Brand: `Blue 0xFF1877F2` (primary), `Black 0xFF1C1E21`, plus error/surface/gray tones (`BlueGray`, `WhiteGray`). |
| `ui/theme/Theme.kt` | `NewsAppTheme` Material3 theme + status bar handling. |
| `ui/theme/Type.kt` | Typography definitions. |

### Where new layers will go (planned, not yet created)
- `domain/model/`, `domain/repository/`, `domain/usecases/`
- `data/remote/` (Retrofit API + Paging source), `data/local/` (Room DB/DAO), `data/repository/`, `data/manager/` (DataStore)
- `di/` (Hilt modules), `presentation/navgraph/`, `presentation/home/`, `presentation/search/`, `presentation/details/`, `presentation/bookmark/`
- `NewsApplication.kt` (`@HiltAndroidApp`)

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
