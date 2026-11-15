package com.example.data.model

enum class WordPronunciationStatus {
    PERFECT,        // High accuracy, correct phoneme delivery
    ACCEPTABLE,     // Understandable with minor accent or syllable stress variation
    MISPRONOUNCED,  // Phonetic distortion or incorrect vowel/consonant
    MISSING         // Word was skipped or dropped in speech
}

data class PhoneticWordTip(
    val word: String,
    val ipa: String,
    val focusArea: String,
    val tipDe: String
)

data class SpeakingPrompt(
    val id: String,
    val title: String,
    val targetSentence: String,
    val category: String,
    val contextBrief: String,
    val difficultyLevel: String,
    val phoneticFocusWords: List<PhoneticWordTip>,
    val audioPromptSpeed: Float = 1.0f
)

data class PronunciationWordResult(
    val expectedWord: String,
    val spokenWord: String?,
    val status: WordPronunciationStatus,
    val confidenceScore: Float,
    val ipa: String? = null,
    val phoneticTip: String? = null
)

data class PronunciationAnalysisResult(
    val promptId: String,
    val overallScore: Int,
    val accuracyScore: Int,
    val fluencyScore: Int,
    val prosodyScore: Int,
    val transcribedText: String,
    val wordResults: List<PronunciationWordResult>,
    val feedbackHeadlineDe: String,
    val cefrBandDe: String,
    val actionableTipsDe: List<String>,
    val assessedAtTimestamp: Long = System.currentTimeMillis()
)
