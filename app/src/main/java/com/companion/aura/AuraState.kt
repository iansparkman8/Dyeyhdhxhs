package com.companion.aura

import androidx.compose.ui.graphics.Color

enum class AuraState(
    val displayName: String,
    val imageRes: Int,
    val coreColor: Color,
    val wingColor: Color
) {
    AMBIENT(
        "Ambient hover",
        R.drawable.aura_ambient_pose,
        Color(0xFFD86BFF),
        Color(0x99E9B8FF)
    ),
    THINKING(
        "Thinking",
        R.drawable.aura_thinking_pose,
        Color(0xFF33E6FF),
        Color(0xAA9FE7FF)
    ),
    FOCUSED(
        "Focused assist",
        R.drawable.aura_focused_pose,
        Color(0xFFFFD166),
        Color(0xAAFFE7A0)
    ),
    LISTENING(
        "Listening",
        R.drawable.aura_sprite_sheet,
        Color(0xFF8BFFB8),
        Color(0xAAAEFFD0)
    )
}
