# Claude Build Instructions — AURA Companion

You are working inside the root of a complete Android project. Your job is to build the debug APK and fix only concrete compile errors.

## Rules

1. Do not create a new nested Android project.
2. Do not rename the package unless absolutely necessary.
3. Do not add `INTERNET` permission.
4. Do not add screenshot capture.
5. Do not enable Accessibility gestures; this app only parses local text context.
6. Do not use `./gradlew`; use installed Gradle.
7. Java must be 17.

## Build command

```bash
gradle clean :app:assembleDebug --stacktrace
```

## Expected output

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Project map

- `MainActivity.kt` — setup screen and permission buttons.
- `AuraOverlayService.kt` — foreground service and floating overlay window.
- `AuraCompanionUI.kt` — animated Compose avatar.
- `AuraScreenParserService.kt` — optional local Accessibility context parser.
- `AuraPrivacyFilter.kt` — redacts sensitive-looking values.
- `AuraLocalBrain.kt` — simple local reaction/state rules.
- `.github/workflows/android-debug.yml` — GitHub Actions build path.

## First fix targets if build fails

1. Dependency version mismatch in `app/build.gradle.kts`.
2. Missing Android SDK platform in GitHub Actions.
3. Compose compiler/Kotlin mismatch.
4. Resource naming issue under `app/src/main/res`.

Always paste the exact failing stacktrace before patching.
