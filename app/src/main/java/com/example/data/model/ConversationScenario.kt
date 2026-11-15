package com.example.data.model

data class ConversationScenario(
    val id: String,
    val title: String,
    val rolePartner: String, // e.g. "Dr. Weber (Leiter Entwicklung)" or "Sarah (Kollegin beim Mittagessen)"
    val environment: String, // Alltag (40%), Beruf (35%), Technik (25%)
    val openingStatement: String,
    val turns: List<ConversationTurn>,
    val unexpectedInterruption: UnexpectedInterruption,
    val evaluationFocus: String,
    val variationLabel: String = "Basis"
)

enum class SpeakerProfile(val pitch: Float, val rate: Float) {
    PARTNER(1.0f, 1.0f),
    INTERRUPTER(1.12f, 1.08f),
    FOLLOW_UP(0.9f, 1.04f)
}

data class ConversationTurn(
    val partnerSpeech: String,
    val expectedUserRolePrompt: String,
    val suggestedC1Connectors: List<String>,
    val targetRegister: String,
    val speakerProfile: SpeakerProfile = SpeakerProfile.PARTNER
)

data class UnexpectedInterruption(
    val triggerAfterSecond: Int,
    val partnerInterruptSpeech: String, // "Ja, aber findest du nicht, dass das ein bisschen zu kurz gedacht ist?"
    val reactionObjective: String, // "Sofortige Relativierung & Verteidigung der Prämisse ohne Zögern"
    val speakerProfile: SpeakerProfile = SpeakerProfile.INTERRUPTER
)
