# Click Track - Copilot Instructions

## Overview
Kotlin Multiplatform metronome app (Android + iOS). ~275 Kotlin files. Tech: Jetpack Compose, Decompose, SQLDelight, kotlin-inject. Targets: Android SDK 23-36, iOS 13.5+.

## CRITICAL: Java 21 Required
**ALWAYS use Java 21.** JVM target errors mean wrong Java version.
```bash
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```
Include before ALL Gradle commands. Gradle 8.13 (use `./gradlew`), Kotlin 2.2.21.

## Essential Build Commands (requires Java 21)

**Check Android** (lint, tests, ktlint) - ~2-4 min clean, ~30s incremental:
```bash
./gradlew :checkAndroid
```
Runs Android tests, ktlint (common/Android), DB migration verification. Use before committing.

**Build Android release** - ~2-3 min (may fail uploading to Firebase locally - expected):
```bash
./gradlew :android-app:bundleRelease
```

**Unit tests only**:
```bash
./gradlew :android-app:testDebugUnitTest
./gradlew :multiplatform:testDebugUnitTest
```

**Lint** - ktlint 1.3.0, config: `.editorconfig`:
```bash
./gradlew ktlintFormat  # Auto-fix
```
Note: `ktlintCheck` fails on Linux (tries iOS compilation). Use `:checkAndroid`.

**Clean** - ~2s:
```bash
./gradlew clean
```

**iOS** (macOS only):
```bash
./gradlew :checkIos
```
Fails on Linux with "Unsupported Operating System" - expected.

## CI - GitHub Actions

**build-check.yml** (on push/PR to master):
- Android: Java 21 (Zulu) → `./gradlew :checkAndroid` → `./gradlew :android-app:bundleRelease`
- iOS: Java 21, XCode 16.3, macOS 15 → `./gradlew :checkIos` → Pod install → XCode build

**coverage.yml** (on push to master): Kover report → GitHub Pages

**deploy.yml** (manual): Deploys to Google Play

Your changes must pass build-check jobs.

## Project Structure

**Root:**
- `build.gradle.kts` - Multi-project setup, ktlint, kover, custom tasks (`checkAndroid`, `checkIos`)
- `settings.gradle.kts` - Includes `:android-app`, `:multiplatform`
- `gradle/libs.versions.toml` - Version catalog
- `.editorconfig` - 4-space indent, 140 char limit
- `build-src/` - Convention plugins (`clicktrack.*`)

**android-app/** - Android app
- `build.gradle.kts` - Firebase, Compose, CMake native build
- `version-code` - Single-line version code (auto-bumped on deploy)
- `src/main/cpp/` - Native C++ audio
- `src/main/kotlin/` - Android app layer
- `src/test/` - JUnit + MockK tests

**multiplatform/** - Kotlin Multiplatform
- `build.gradle.kts` - KMM, Compose, SQLDelight, CocoaPods
- `src/commonMain/kotlin/com/vsevolodganin/clicktrack/` - Shared code
  - `di/` - kotlin-inject DI
  - `model/` - Domain models
  - `player/` - Audio engine
  - `storage/` - Persistence
  - `ui/` - Compose UI
- `src/commonMain/sqldelight/` - SQLDelight schemas & migrations
- `src/androidMain/`, `src/iosMain/` - Platform implementations

**ios-app/** - iOS app (Swift, CocoaPods)
- `Podfile` - Points to `../multiplatform`

## Database - SQLDelight
Schema: `multiplatform/src/commonMain/sqldelight/`
Tables: `SqlClickTrack.sq`, `SqlClickSounds.sq`
Migrations: `migrations/1.sqm`, `2.sqm`

Verify migrations: `./gradlew :multiplatform:verifyCommonMainDatabaseMigration` (part of `:checkAndroid`)

## Common Issues

**JVM target mismatch** - Not using Java 21. Fix: Export `JAVA_HOME` to Java 21 path.

**iOS fails on Linux** - Expected. "Unsupported Operating System" warning is normal.

**Firebase upload fails** - Expected locally (release builds). `UnknownHostException: firebasecrashlyticssymbols.googleapis.com` means no credentials/network. App builds fine.

**ktlint fails on iOS** - Use `./gradlew :checkAndroid` not `ktlintCheck` on Linux.

## Code Style
- **Line length:** 140 chars (strict)
- **Indent:** 4 spaces
- **Comments:** `FIXME`/`TODO` markers are intentional - don't remove unless fixing issue
- **Composables:** Exempt from function naming rules

## Development Workflow
1. Ensure Java 21 configured
2. Baseline: `./gradlew :checkAndroid` (should pass)
3. Make changes
4. Test: `./gradlew :android-app:testDebugUnitTest`
5. Format: `./gradlew ktlintFormat`
6. Verify: `./gradlew :checkAndroid`
7. If DB changed: verify migrations
8. Commit

## Tips
- **Incremental builds:** 30s after first build (don't `clean` unless needed)
- **First build:** ~2min (downloads Gradle, NDK)
- **Parallel builds:** Enabled by default

**Trust these instructions** - all commands tested and validated.
