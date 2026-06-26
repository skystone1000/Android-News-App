# CLAUDE.md — Project rules for AI assistants

This file is loaded automatically each session. Follow it before doing project work.

## 0. The golden rule: docs first, code second

To save tokens and time, **the `NewsApp/docs/` directory is the source of truth for project
context**. Do NOT re-scan the whole codebase to orient yourself each session.

**At the start of any task on this project, read these first (in order):**
1. `NewsApp/docs/ARCHITECTURE.md` — layers, design, conventions, known gaps
2. `NewsApp/docs/CODEBASE.md` — file-by-file map, build/toolchain versions
3. `NewsApp/docs/FEATURES.md` — what works, what's planned, build order
4. `NewsApp/docs/plan/plan_1_roadmap.md` — phased implementation plan (Path A→D) + progress tracker; consult before starting feature work and tick boxes as phases land
5. `NewsApp/docs/DATASOURCES.md` — news providers, in-app API-key handling, free-tier usage tracking (read for any data-source/provider/key work)

The phased build plans live in `NewsApp/docs/plan/` (`plan_1_roadmap` … `plan_5_remaining_work`,
sequential by creation date). `plan_5_remaining_work.md` tracks the open backlog (deferred infra,
content placeholders, and known bugs) — consult it before picking up new feature work.

Only after reading the relevant docs should you open source files — and then open
**only the specific files** the task needs (the docs tell you which). Do not bulk-read
the tree if a doc already answers the question.

If a doc and the code disagree, the **code wins** — then immediately fix the doc
(see §1). Treat doc/code drift as a bug.

## 1. Keep the docs in sync (mandatory)

Whenever you change the project, update `NewsApp/docs/` in the **same change** so the next
session stays accurate. Specifically:

| If you change... | Update... |
|------------------|-----------|
| Add/remove/move/rename a source file | `NewsApp/docs/CODEBASE.md` (file map) |
| Change architecture, a layer, or a convention | `NewsApp/docs/ARCHITECTURE.md` |
| Add/finish/alter a user-facing feature | `NewsApp/docs/FEATURES.md` (status table) |
| Bump Gradle/AGP/Kotlin/SDK/dependency versions | `NewsApp/docs/CODEBASE.md` (toolchain table) |
| Add/change a news provider, API-key handling, or usage tracking | `NewsApp/docs/DATASOURCES.md` |
| Close one of the "Known gaps" | remove it from `NewsApp/docs/ARCHITECTURE.md` §6 |

Also bump the `Last updated:` date at the top of any doc you edit. A code change is
**not complete** until its docs are updated. Keep docs concise — they are a map, not
a copy of the code; never paste large code blocks into them.

## 2. Build & run

- Open the **`NewsApp/`** folder in Android Studio (not the repo root).
- There is **no system `java`**. For CLI builds, first:
  `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`
  then `cd NewsApp && ./gradlew :app:assembleDebug`.
- Authoritative toolchain versions live in `NewsApp/docs/CODEBASE.md` — keep them there, not here.

## 3. Code conventions

- 100% Jetpack Compose; single-activity. Follow **Clean Architecture** layering
  (`presentation → domain ← data`); domain stays pure Kotlin (no Android imports).
- Spacing/sizes go in `presentation/Dimens.kt`; colors in `ui/theme/Color.kt`.
- Reusable composables in `presentation/common/`; feature-private ones in
  `presentation/<feature>/components/`.
- Use Hilt for DI once wired; prefer constructor injection.
- Match the style of surrounding code.

## 4. Workflow

- Before claiming a build/fix works, actually run the build and report real output.
- Don't commit or push unless explicitly asked.
- Prefer the smallest change that satisfies the task; flag out-of-scope issues
  rather than bundling them in.
