#!/usr/bin/env bash
set -euo pipefail

echo "AURA preflight"
test -f settings.gradle.kts
test -f build.gradle.kts
test -f app/build.gradle.kts
test -f app/src/main/AndroidManifest.xml
test -f .github/workflows/android-debug.yml
find app/src/main/java -name '*.kt' -print | sort
find app/src/main/res -type f -print | sort

echo "OK: project layout is GitHub Actions ready."
echo "Build command on a machine with Android SDK + Gradle: gradle clean :app:assembleDebug --stacktrace"
