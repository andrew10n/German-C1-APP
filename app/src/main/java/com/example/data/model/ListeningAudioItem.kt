package com.example.data.model

data class ListeningLaboratoryItem(
    val id: String,
    val title: String,
    val environment: String, // Alltag, Beruf, Technik
    val accentOrDialect: String, // z.B. Schwäbisch, Berlinerisch, Schnelles Umgangsdeutsch
    val nativeSpeechSpeed: Float, // 1.0f to 1.35f
    val durationSeconds: Int,
    val audioSpeakerText: String, // Text spoken by TTS or played
    val blindComprehensionQuestions: List<InferenceQuestion>,
    val transcriptWithReductions: String,
    val reductionNotes: List<ReductionAnnotation>,
    val contextAndSubtextAnalysis: String
)

data class InferenceQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val rationaleWhyOthersAreWrong: String
)

data class ReductionAnnotation(
    val spokenForm: String, // e.g. "hamma"
    val standardWrittenForm: String, // "haben wir"
    val registerExplanation: String // "Vollständige Schmelzung im informellen Sprechfluss"
)
