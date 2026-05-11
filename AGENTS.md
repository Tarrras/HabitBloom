# AGENTS.md

## Cursor Cloud specific instructions

### Project Overview

HabitBloom is a Kotlin Multiplatform (KMP) habit tracking app targeting Android and iOS, built with Compose Multiplatform. Only the **Android target** can be built on Linux VMs (iOS requires macOS/Xcode).

### Environment

- **JDK**: 21 (set via `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64`)
- **Android SDK**: Installed at `/opt/android-sdk` (API 36, build-tools 36.0.0)
- **Gradle**: Wrapper at `./gradlew` (Gradle 8.13)
- Environment variables `ANDROID_HOME` and `JAVA_HOME` are set in `~/.bashrc`.

### Key Commands

| Task | Command |
|---|---|
| Build Android debug APK | `./gradlew composeApp:assembleDebug` |
| Run unit tests | `./gradlew composeApp:testDebugUnitTest` |
| Lint/format check (Spotless) | `./gradlew spotlessCheck` |
| Apply formatting fixes | `./gradlew spotlessApply` |

### Known Issues

- One pre-existing test failure: `GrowthAndHealthTest.flowerHealth_missPenalties_andRecovery_andRegressionRule` — assertion mismatch in health penalty calculation. This is a codebase issue, not an environment issue.
- Build produces deprecation warnings about KMP + `com.android.application` plugin compatibility with AGP 9.0. These are informational and do not affect the build.

### Cloud Services (no local setup needed)

The app uses Firebase Auth, Firebase Firestore, and Supabase Storage — all cloud-hosted with credentials already committed in the repo (`google-services.json`, `SupabaseConfig.kt`). No local backend services or Docker containers are required.
