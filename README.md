# AURA Companion — GitHub Upload Ready Android Project

This is the cleaned, direct-upload Android repo version of the AURA master package.

It is structured the way GitHub Actions expects:

```text
.github/workflows/android-debug.yml
settings.gradle.kts
build.gradle.kts
app/build.gradle.kts
app/src/main/AndroidManifest.xml
app/src/main/java/com/companion/aura/*.kt
app/src/main/res/*
```

## What this build includes

- Native Android app written in Kotlin + Jetpack Compose.
- Floating `WindowManager` overlay companion.
- Animated avatar bubble using included visual assets.
- Drag movement and tap mood cycling.
- Optional Accessibility-based **local context parser**.
- Privacy guardrails:
  - no `INTERNET` permission,
  - no screenshots,
  - no password/edit-field parsing,
  - redaction for emails, phone numbers, long numbers, SSNs, OTP-style codes.
- GitHub Actions APK build that does **not** require a local PC build.

## Fast GitHub upload path

1. Create a new GitHub repository.
2. Upload the **contents** of this folder to the repository root.
3. Commit to `main`.
4. Open the **Actions** tab.
5. Run **Build Android APK**.
6. Download artifact: `AURA-debug-apk`.
7. Install `app-debug.apk` on your Android device.

## Local build command

On a machine with Java 17, Android SDK 35, and Gradle installed:

```bash
gradle clean :app:assembleDebug --stacktrace
```

The APK will be here:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Android permissions

AURA asks for:

- Display over other apps — required for the floating overlay.
- Foreground service — required so Android keeps the overlay alive.
- Notifications — required on Android 13+ for foreground service visibility.
- Accessibility service — optional; only needed for local screen-context parsing.

The app intentionally does **not** request internet access.

## Notes for Claude / Codex / GitHub agent

Use the root of this repo. Do not create a second nested Android project. Do not run `./gradlew`; this repo intentionally uses GitHub Actions with installed Gradle. Use:

```bash
gradle clean :app:assembleDebug --stacktrace
```

If you modify Android or Compose versions, keep Java 17 and compile SDK 35 unless you update the workflow too.
