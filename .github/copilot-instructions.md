# Click Track - Copilot Agent Instructions

## Project Overview

**Click Track** is a free metronome Android app with iOS support in development. The app allows users to create click tracks (sequences of sections/cues) with custom tempo, time signatures, and subdivisions. Built with Kotlin Multiplatform Mobile (KMM), it shares business logic between Android and iOS while maintaining platform-specific UI implementations.

**Repository Stats:**
- ~275 Kotlin source files
- Languages: Kotlin (primary), Swift (iOS), C++ (native audio), Gradle (build)
- Frameworks: Jetpack Compose (UI), Decompose (navigation), SQLDelight (database), kotlin-inject (DI)
- Target platforms: Android (min SDK 23, target SDK 36), iOS (13.5+)

## Critical Build Requirements

### Java Version - REQUIRED
**YOU MUST USE JAVA 21 FOR ALL BUILDS.** The project requires Java 21 (not Java 17). If you encounter JVM target compatibility errors between Java and Kotlin tasks, it means you're not using Java 21.

**Setup commands:**
```bash
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64  # On Linux
export PATH=$JAVA_HOME/bin:$PATH
java -version  # Should show version 21
```

Always include these exports before ANY Gradle command, or builds will fail with JVM target mismatch errors.

### Gradle Version
- Gradle 8.13 (via wrapper - always use `./gradlew`, never `gradle`)
- Kotlin 2.2.21
- Android Gradle Plugin 8.13.0

## Build Commands

### Essential Commands (Android Development)

**ALWAYS run these commands with Java 21 configured.**

1. **Check Android code** (lint, tests, ktlint):
   ```bash
   ./gradlew :checkAndroid
   ```
   - Duration: ~2-4 minutes (clean build), ~30s (incremental)
   - Runs: Android unit tests, multiplatform debug unit tests, ktlint on common/Android code, database migration verification
   - Use this before committing Android changes

2. **Build Android release bundle**:
   ```bash
   ./gradlew :android-app:bundleRelease
   ```
   - Duration: ~2-3 minutes
   - Note: May fail with `UnknownHostException` for `firebasecrashlyticssymbols.googleapis.com` when building release locally (requires network access to Firebase). This is expected and doesn't indicate a code problem.

3. **Run Android unit tests only**:
   ```bash
   ./gradlew :android-app:testDebugUnitTest
   ./gradlew :multiplatform:testDebugUnitTest
   ```

4. **Lint Kotlin code** (ktlint):
   ```bash
   ./gradlew ktlintCheck          # Check all code (will fail on iOS files if not on macOS)
   ./gradlew ktlintFormat         # Auto-fix formatting issues
   ```
   - ktlint version: 1.3.0
   - Configuration: `.editorconfig`, `build-src/src/main/kotlin/clicktrack.ktlint.gradle.kts`
   - Note: `ktlintCheck` on Linux will fail trying to compile iOS code. Use `:checkAndroid` instead which only checks Android sources.

5. **Clean build**:
   ```bash
   ./gradlew clean
   ```
   - Duration: ~2 seconds
   - Deletes build directories

### iOS-Specific Commands (macOS only)

The project includes iOS support via Kotlin CocoaPods. **These commands only work on macOS:**

```bash
./gradlew :checkIos                           # iOS checks
./gradlew :multiplatform:generateDummyFramework
cd ios-app && pod install --repo-update
xcodebuild -workspace ios-app/ClickTrack.xcworkspace -scheme ClickTrack -configuration Debug -sdk iphonesimulator -arch arm64
```

On Linux, iOS compilation will fail with "Unsupported Operating System" warning - this is expected.

### Coverage and Verification

```bash
./gradlew :koverHtmlReport          # Generate HTML coverage report
./gradlew :printLineCoverage        # Print coverage percentage
```

## Continuous Integration

The project has three GitHub Actions workflows:

### 1. Build Check (`.github/workflows/build-check.yml`)
Runs on: Push to master, PRs to master

**Android Job:**
- Setup: Java 21 (Zulu distribution)
- Steps: `./gradlew :checkAndroid` → `./gradlew :android-app:bundleRelease`

**iOS Job:**
- Setup: Java 21, XCode 16.3, macOS 15
- Steps: `./gradlew :checkIos` → Generate framework → Pod install → XCode build

**Your changes must pass both jobs.** If you're only changing Android code, iOS job should still pass unchanged.

### 2. Coverage (`.github/workflows/coverage.yml`)
Runs on: Push to master
- Generates Kover HTML coverage report
- Deploys to GitHub Pages
- Updates coverage badge

### 3. Deploy (`.github/workflows/deploy.yml`)
Manual workflow dispatch only - deploys to Google Play

## Project Structure

### Root Directory Files
```
.editorconfig           # Code style rules (4-space indent, 140 char line length)
.github/                # Workflows, logo
.gitignore              # Excludes build/, .gradle/, .idea/, etc.
LICENSE                 # Apache 2.0
README.md               # Project overview, features, badges
build.gradle.kts        # Root build file (multi-project setup, ktlint, kover, custom tasks)
settings.gradle.kts     # Project includes (:android-app, :multiplatform)
gradle.properties       # Gradle config (2GB heap, AndroidX, Kotlin native cache)
gradlew / gradlew.bat   # Gradle wrapper scripts
gradle/                 # Wrapper JARs, libs.versions.toml
build-src/              # Convention plugins (clicktrack.*)
android-app/            # Android application module
multiplatform/          # Shared Kotlin Multiplatform code
ios-app/                # iOS application (Swift)
```

### Module Structure

#### `android-app/` - Android Application
```
build.gradle.kts        # Android app config, Firebase, Compose, native build (CMake)
google-services.json    # Firebase configuration
lint.xml                # Android Lint suppressions (NewApi false positive)
proguard-rules.pro      # R8/ProGuard rules for release builds
version-code            # Single-line file with version code (bumped on deploy)
src/main/               # Android-specific code
  cpp/                  # Native C++ audio code (compiled via CMake)
  kotlin/               # Android app layer
  res/                  # Android resources
src/debug/              # Debug build type sources
src/test/               # Android unit tests
```

#### `multiplatform/` - Kotlin Multiplatform Module
```
build.gradle.kts                          # KMM setup, Compose, SQLDelight, CocoaPods
ClickTrackMultiplatform.podspec           # iOS framework pod spec
src/
  commonMain/                             # Shared code for all platforms
    kotlin/com/vsevolodganin/clicktrack/
      di/                                 # Dependency injection (kotlin-inject)
      export/                             # Click track export functionality
      language/                           # Localization
      model/                              # Domain models (ClickTrack, Cue, BPM, etc.)
      player/                             # Audio playback engine
      polyrhythm/                         # Polyrhythm logic
      soundlibrary/                       # Sound management
      storage/                            # Data persistence layer
      theme/                              # Theme/appearance models
      training/                           # Training click track generation
      ui/                                 # Compose UI components
        piece/                            # Reusable UI components
        preview/                          # Compose preview providers
        theme/                            # UI theming
      utils/                              # Utilities (compose, time, math, etc.)
    sqldelight/                           # SQLDelight database schemas
      com/vsevolodganin/clicktrack/storage/  # Table definitions (.sq files)
      migrations/                         # Database migrations (1.sqm, 2.sqm)
      schema/                             # Generated schema outputs
    composeResources/                     # Compose Multiplatform resources
  androidMain/kotlin/                     # Android-specific implementations
  iosMain/kotlin/                         # iOS-specific implementations
  iosArm64Main/kotlin/                    # iOS ARM64-specific code
  iosSimulatorArm64Main/kotlin/           # iOS Simulator ARM64-specific code
  iosX64Main/kotlin/                      # iOS x64-specific code
  androidUnitTest/kotlin/                 # Android unit tests
```

#### `build-src/` - Convention Plugins
Contains reusable Gradle configuration:
- `clicktrack.android.application.gradle.kts` - Android app plugin
- `clicktrack.android.library.gradle.kts` - Android library plugin
- `clicktrack.android.common.kt` - Shared Android config (SDK 36, Java 21 target)
- `clicktrack.ktlint.gradle.kts` - ktlint setup
- `clicktrack.multiplatform.gradle.kts` - KMM setup
- `clicktrack.include-in-coverage.gradle.kts` - Kover coverage config

### Configuration Files

**`.editorconfig`:**
- Charset: UTF-8, LF line endings
- Indent: 4 spaces (2 for JSON/YAML)
- Max line length: 140 (200 for strings.xml)
- Kotlin: Android Studio code style, trailing commas enabled

**`gradle/libs.versions.toml`:**
Version catalog defining all dependencies. Key versions:
- Kotlin: 2.2.21
- Compose: 1.9.3  
- Coroutines: 1.10.2
- SQLDelight: 2.1.0
- Decompose: 3.4.0

## Database Migrations

The project uses SQLDelight for database management:
- Schema location: `multiplatform/src/commonMain/sqldelight/`
- Tables: `SqlClickTrack.sq`, `SqlClickSounds.sq`
- Migrations: `migrations/1.sqm`, `migrations/2.sqm`

**Verification task:**
```bash
./gradlew :multiplatform:verifyCommonMainDatabaseMigration
```

This runs as part of `:checkAndroid` and `:checkIos`. Always verify migrations after schema changes.

## Common Issues and Workarounds

### JVM Target Mismatch
**Error:** "Inconsistent JVM-target compatibility detected for tasks 'compileDebugJavaWithJavac' (21) and 'compileDebugKotlinAndroid' (17)"

**Cause:** Not using Java 21

**Fix:** Set `JAVA_HOME` to Java 21 before running Gradle:
```bash
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

### iOS Compilation on Linux
**Error:** "Unsupported Operating System - Kotlin CocoaPods Plugin is fully supported on MacOS machines only"

**Expected behavior.** Ignore this warning when working on Android code. iOS tasks are skipped automatically on non-macOS hosts.

### Firebase Crashlytics Upload Failure
**Error:** `UnknownHostException: firebasecrashlyticssymbols.googleapis.com`

**Expected behavior** when building release locally without proper Firebase credentials/network. The app builds successfully; only symbol upload fails. CI has proper credentials.

### Gradle Daemon Slow Start
First build after machine restart downloads Gradle distribution and NDK (~2 minutes). Subsequent builds are faster (30s-2m).

### ktlint Failures on iOS Code
Running `./gradlew ktlintCheck` on Linux attempts to compile iOS code and fails. Use `./gradlew :checkAndroid` instead, which only checks Android/common sources.

## Code Style Notes

- **Comments:** Code contains `FIXME`, `TODO` markers for known issues (see grep results). These are intentional and document temporary workarounds. Don't remove them unless fixing the underlying issue.
- **Line length:** 140 characters (strict limit enforced by ktlint)
- **Imports:** Standard library imports first, then third-party (auto-organized by IDE)
- **Composable naming:** Functions annotated with `@Composable` are exempt from function naming rules

## Making Changes

### Before Starting
1. Ensure Java 21 is configured
2. Run `./gradlew :checkAndroid` to establish baseline (should pass)

### Development Workflow
1. Make code changes
2. Run relevant tests: `./gradlew :android-app:testDebugUnitTest` or `./gradlew :multiplatform:testDebugUnitTest`
3. Format code: `./gradlew ktlintFormat`
4. Run checks: `./gradlew :checkAndroid`
5. If database schema changed: verify migrations pass
6. Commit changes

### Testing Changes
- Unit tests: JUnit + MockK (Android), Kotlin test (multiplatform)
- Test location: `src/test/` (Android), `src/androidUnitTest/` (multiplatform)
- Run specific test class: `./gradlew :android-app:testDebugUnitTest --tests 'ClassName'`

## Performance Tips

- **Incremental builds:** After first build, most tasks are UP-TO-DATE (~30s builds)
- **Parallel builds:** Gradle uses multiple cores by default
- **Build cache:** Enabled via Gradle wrapper (speeds up clean builds)
- **Don't run `clean` unless necessary** - incremental builds are much faster

## Trust These Instructions

These instructions were generated through comprehensive exploration and testing of the repository. Commands have been validated to work correctly. If a command doesn't work as documented, first verify Java 21 is configured, then check for environment-specific issues. Only explore further if the documented approach fails after verification.
