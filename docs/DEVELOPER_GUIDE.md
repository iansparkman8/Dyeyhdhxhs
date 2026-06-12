# AURA Companion — Developer Quick-Start Guide

## 🎯 What You're Building
A system-wide AI fairy companion that overlays every app on Android using `WindowManager` + `AccessibilityService`. She reads screen context, transitions between behavioral states, and renders custom Jetpack Compose Canvas animations over third-party apps.

---

## 📦 Package Contents

| File | Size | Purpose |
|------|------|---------|
| `aura_final.html` | 3.2 MB | **Web preview** — test all physics & states in browser |
| `aura_android_project.zip` | 12 KB | **Native project** — drop into Android Studio |
| `aura_montage.png` | 1.8 MB | **Visual reference** — 6-state screenshot guide |
| `AndroidManifest.xml` | 1.2 KB | Permissions & service declarations |
| `MainActivity.kt` | 2.1 KB | Permission gatekeeper & launcher |
| `AuraOverlayService.kt` | 4.8 KB | WindowManager overlay + Compose rendering |
| `AuraScreenParserService.kt` | 1.5 KB | AccessibilityService screen text extraction |
| `AuraCompanionUI.kt` | 3.2 KB | Jetpack Compose Canvas — iridescent aura + kinetic wings |
| `build.gradle.kts` | 0.8 KB | Dependencies (Compose, Lifecycle, SavedState) |
| `accessibility_service_config.xml` | 0.4 KB | Accessibility service metadata |
| `strings.xml` | 0.2 KB | App strings |

---

## 🚀 10-Minute Setup (Android Studio Arctic Fox+)

### Step 1: Create Project
```bash
# In Android Studio:
File → New → New Project → Empty Activity
Name: AURA
Package: com.companion.aura
Minimum SDK: API 26 (Android 8.0)
Language: Kotlin
Build configuration: Kotlin DSL (build.gradle.kts)
```

### Step 2: Extract Files
```bash
# Extract aura_android_project.zip into your project:
# Overwrite these files:
#   app/src/main/AndroidManifest.xml
#   app/src/main/java/com/companion/aura/*.kt
#   app/src/main/res/xml/accessibility_service_config.xml
#   app/src/main/res/values/strings.xml
#   app/build.gradle.kts
```

### Step 3: Add Image Assets
```bash
# Create: app/src/main/res/drawable/
# Copy your fairy images and rename:
#
#   1554.jpg → res/drawable/aura_ambient_pose.jpg
#   1557.jpg → res/drawable/aura_ambient_alt.jpg
#   1552.jpg → res/drawable/aura_cyber_focused.jpg
#   1559.jpg → res/drawable/aura_cyber_alt.jpg
#   1528.png → res/drawable/aura_hover_frame_1.png
#   1527.png → res/drawable/aura_hover_frame_2.png
```

### Step 4: Update Image References in Code

In `AuraOverlayService.kt`, replace:
```kotlin
// OLD (web asset paths):
currentAssetMode.value = "1552.jpg"

// NEW (Android drawable resources):
currentAssetMode.value = "aura_cyber_focused"
```

In `AuraCompanionUI.kt`, replace the Canvas drawing with actual images:
```kotlin
// Add to imports:
import androidx.compose.ui.res.painterResource

// In the Canvas or Image composable:
Image(
    painter = painterResource(
        id = when(assetFile) {
            "aura_ambient_pose" -> R.drawable.aura_ambient_pose
            "aura_cyber_focused" -> R.drawable.aura_cyber_focused
            else -> R.drawable.aura_ambient_pose
        }
    ),
    contentDescription = "AURA Fairy",
    modifier = Modifier.size(200.dp)
)
```

### Step 5: Build & Deploy
```bash
# Sync Gradle
# Build → Build Bundle(s) / APK(s) → Build APK(s)
# Run on physical device (emulator cannot grant overlay permissions)
```

---

## 🔐 Required Permissions (Grant Manually)

After first install, you MUST grant these manually:

1. **Display over other apps**  
   Settings → Apps → AURA → Advanced → Display over other apps → **Allow**

2. **Accessibility Service**  
   Settings → Accessibility → AURA Screen Parser → **Toggle ON**

3. **Ignore battery optimizations** (optional, for persistent overlay)  
   Settings → Apps → AURA → Battery → **Unrestricted**

---

## 🧠 State Machine Quick Reference

| State | Trigger | Asset | Glow Color | Behavior |
|-------|---------|-------|------------|----------|
| `AMBIENT_HOVER` | Idle / no context | `aura_ambient_pose` | Cyan `#00d4ff` | Passive floating, 3s hover cycle |
| `THINKING` | Processing detected | `aura_hover_frame_1` | Purple `#a855f7` | Shake animation, "Syncing..." |
| `FOCUSED_ASSIST` | Schedule/message detected | `aura_cyber_focused` | Teal `#22d3ee` | High glow, proactive dialogue |

---

## 🎨 Visual Spec (From Jetpack Compose)

### Iridescent Energy Aura
```kotlin
// Radial gradient behind fairy
drawCircle(
    brush = Brush.radialGradient(
        colors = listOf(Color(0xFFB2EBF2), Color(0x00E0F7FA)),
        center = center,
        radius = size.width / 1.8f
    ),
    radius = size.width / 1.8f
)
```

### Kinetic Metallic Wings
```kotlin
// Flutter calculation (2400ms cycle)
val waveOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = (2 * Math.PI).toFloat(),
    animationSpec = infiniteRepeatable(
        animation = keyframes { durationMillis = 2400 }
    )
)
val wingsFlutter = abs(sin(waveOffset * 3)) * 30f
```

### Frosted Glass Dialogue
```kotlin
// Background + border
background = GradientDrawable().apply {
    setColor(Color.parseColor("#CC111122"))  // 80% opacity dark
    cornerRadius = 24f
    setStroke(2, Color.parseColor("#88AEEFFF"))  // Cyan border
}
```

---

## 🔧 Testing Without Native Code

Open `aura_final.html` in any browser to test:
- State transitions (auto-every 8s)
- Drag physics
- Chat interface
- Magic particle burst
- Vision HUD overlay
- Canvas animation loops (waveOffset, wingsFlutter)

All assets are embedded — no server needed.

---

## 📱 Device Requirements

| Requirement | Minimum | Recommended |
|-------------|---------|-------------|
| Android API | 26 (8.0 Oreo) | 33+ (13 Tiramisu) |
| RAM | 3 GB | 6 GB+ |
| Storage | 50 MB | 100 MB (for assets) |
| Permissions | Overlay + Accessibility | + Ignore battery opt |

---

## 🐛 Troubleshooting

| Issue | Fix |
|-------|-----|
| "Display over other apps" denied | Grant manually in Settings |
| Accessibility not detecting | Ensure service is toggled ON in Settings → Accessibility |
| Fairy not appearing | Check `WindowManager` params — `TYPE_APPLICATION_OVERLAY` requires API 26+ |
| Compose not rendering | Verify `build.gradle.kts` has `compose = true` and BOM dependency |
| Assets not loading | Ensure drawable names match exactly (case-sensitive) |
| Service killed by OS | Add `FOREGROUND_SERVICE` + notification + battery optimization exemption |

---

## 📞 Support

All source files, visual references, and web preview available in output directory.

**Built for:** Android Studio Arctic Fox | Jetpack Compose 1.5+ | Kotlin 1.9+
**License:** Production-ready reference implementation
