package com.companion.aura

data class AuraReaction(val state: AuraState, val message: String)

object AuraLocalBrain {
    fun reactTo(context: String): AuraReaction {
        val lower = context.lowercase()
        return when {
            listOf("calendar", "schedule", "appointment", "meeting", "pickup").any { it in lower } ->
                AuraReaction(AuraState.FOCUSED, "I see schedule context. Want me to help you turn this into a clean reminder or plan?")

            listOf("message", "reply", "text", "email", "inbox").any { it in lower } ->
                AuraReaction(AuraState.THINKING, "This looks like communication context. I can help draft a careful response.")

            listOf("error", "failed", "exception", "bug", "crash").any { it in lower } ->
                AuraReaction(AuraState.FOCUSED, "I noticed a build or error signal. Paste the exact log into the app or chat and we can fix the next file.")

            listOf("code", "github", "gradle", "kotlin", "android").any { it in lower } ->
                AuraReaction(AuraState.THINKING, "Development mode detected. Focus on one failing file, one build log, one clean patch.")

            else -> AuraReaction(AuraState.AMBIENT, "I caught local screen context and filtered it. I’ll stay light unless you need help.")
        }
    }

    fun nextState(current: AuraState): AuraState = when (current) {
        AuraState.AMBIENT -> AuraState.LISTENING
        AuraState.LISTENING -> AuraState.THINKING
        AuraState.THINKING -> AuraState.FOCUSED
        AuraState.FOCUSED -> AuraState.AMBIENT
    }

    fun idleLine(state: AuraState): String = when (state) {
        AuraState.AMBIENT -> "Ambient mode. I’ll hover out of the way."
        AuraState.LISTENING -> "Listening mode. Give me a task or paste a code note."
        AuraState.THINKING -> "Thinking mode. I’m sorting the next clean step."
        AuraState.FOCUSED -> "Focused assist mode. Let’s fix one blocker at a time."
    }
}
