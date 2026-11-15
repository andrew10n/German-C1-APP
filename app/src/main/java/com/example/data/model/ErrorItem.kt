package com.example.data.model

enum class ErrorCategory(val displayName: String, val chipColorHex: Long) {
    PRAEPOSITIONEN_KASUS("Präpositionen & Kasus", 0xFFEF4444),
    KOLLOKATIONEN("Unnatürliche Kollokation", 0xFFF59E0B),
    REGISTER_MISMATCH("Register-Fehlanpassung", 0xFF8B5CF6),
    FALSE_FRIENDS_FEINHEITEN("Falsche Freunde / Nuance", 0xFF06B6D4),
    VERBKLAMMER_SATZSTRUKTUR("Satzbau & Verbklammer", 0xFFEC4899),
    ZOEGERN_LATENZ("Sprechlatenz & Zögern", 0xFF10B981)
}

enum class MasteryStage(val label: String, val stepIndex: Int, val nextReviewDueInDays: Int) {
    NEU("Neu erfasst", 1, 1),
    WIEDERHOLUNG_TAG_1("Aktive Repetition (Tag 1)", 2, 2),
    TRANSFER_TAG_3("Transfer-Drill (Tag 3)", 3, 4),
    GEFESTIGT_TAG_7("Verzögerter Test (Tag 7)", 4, 7),
    GEMEISTERT("Autonom gemeistert", 5, 0)
}

data class ErrorIntelligenceItem(
    val id: String,
    val category: ErrorCategory,
    val userMistake: String,
    val nativeReformulation: String,
    val whyItSoundsForeign: String,
    val ruleOrNuance: String,
    val contextSentence: String,
    val transferChallengePrompt: String,
    val transferTargetAnswer: String,
    val masteryStage: MasteryStage,
    val occurrencesCount: Int,
    val lastOccurredDaysAgo: Int
)
