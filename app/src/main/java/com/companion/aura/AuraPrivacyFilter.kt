package com.companion.aura

object AuraPrivacyFilter {
    private val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
    private val phoneRegex = Regex("(?<!\\d)(?:\\+?1[ .-]?)?(?:\\(?\\d{3}\\)?[ .-]?)\\d{3}[ .-]?\\d{4}(?!\\d)")
    private val cardRegex = Regex("(?<!\\d)(?:\\d[ -]*?){13,19}(?!\\d)")
    private val ssnRegex = Regex("(?<!\\d)\\d{3}-\\d{2}-\\d{4}(?!\\d)")
    private val otpRegex = Regex("(?i)\\b(?:code|otp|pin|verification)[:\\s-]*\\d{4,8}\\b")
    private val longNumberRegex = Regex("(?<!\\d)\\d{9,}(?!\\d)")

    fun redact(input: String): String {
        return input
            .replace(emailRegex, "[email redacted]")
            .replace(phoneRegex, "[phone redacted]")
            .replace(cardRegex, "[number redacted]")
            .replace(ssnRegex, "[ssn redacted]")
            .replace(otpRegex, "[code redacted]")
            .replace(longNumberRegex, "[number redacted]")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
