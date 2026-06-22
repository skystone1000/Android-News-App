# Android News App

A configurable, multi-source news reader for Android — **Jetpack Compose**, single-activity,
**Clean Architecture + MVVM**, Hilt DI.

## Features

- **Multi-source news** — 5 providers (NewsAPI.org, NewsData.io, GNews, Currents, Mediastack)
  behind one pluggable `NewsSource`; pick the active one in Settings.
- **In-app API keys** — enter each provider's key in the app (encrypted at rest); no keys baked
  into the build. Per-provider **free-tier usage meters**.
- **Reading** — infinite-scroll headlines feed with category chips, keyword search, article detail,
  bookmarks (Room).
- **Personalization** — followed categories, theme (Light/Dark/System), all opt-in via Settings.
- **AI (optional)** — Claude-powered summary / sentiment / topic tags on the detail screen.
- **Engagement** — share, listen (TTS), reading history, daily digest notification (WorkManager).
- **Hardened release** — R8 minify + resource shrinking.

## Build & run

Open the **`NewsApp/`** folder in Android Studio (not the repo root).

CLI builds need the Android Studio JBR (there is no system `java`):

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
cd NewsApp
./gradlew :app:assembleDebug
```

API keys are entered **in-app** (Settings → Data sources). For development you may instead put them
in `NewsApp/local.properties` (`NEWS_API_KEY`, `GNEWS_API_KEY`, optional `CLAUDE_API_KEY`); they are
seeded into the encrypted store on first launch. `local.properties` is git-ignored.

## Documentation

The `docs/` directory is the source of truth for project context:

- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) — layers, design, conventions, known gaps
- [docs/CODEBASE.md](docs/CODEBASE.md) — file-by-file map + toolchain versions
- [docs/FEATURES.md](docs/FEATURES.md) — feature status
- [docs/DATASOURCES.md](docs/DATASOURCES.md) — news providers, API-key handling, usage tracking
- [docs/ROADMAP.md](docs/ROADMAP.md) — phased plan (Path A→D) + progress
- [docs/MULTI_SOURCE_PLAN.md](docs/MULTI_SOURCE_PLAN.md) — multi-provider + in-app keys plan

## Tooling

Quality gates run in CI (`.github/workflows/ci.yml`, JDK 17): **detekt**, Android **lint**,
**unit tests**, and `assembleDebug`. Run locally with:

```bash
./gradlew detekt :app:lintDebug :app:testDebugUnitTest :app:assembleDebug
```
