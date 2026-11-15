package com.example.programme

enum class ContextTrack { GENERAL, SOCIAL, ENGINEERING, COACHING }

enum class Dimension {
    ACCURACY, FLUENCY, RANGE, NATURALNESS, COHERENCE, INTERACTION,
    PRAGMATICS, REGISTER, COMPREHENSION, MEDIATION
}

enum class EvidenceSource { SELF_REFLECTION, AUTOMATIC_QUIZ, HUMAN_REVIEW }

/** Scores use a 0..4 rubric. Missing or out-of-range scores cannot establish mastery. */
data class Attempt(
    val id: String,
    val unitId: String,
    val taskId: String,
    val timestamp: Long,
    val response: String,
    val source: EvidenceSource,
    val scores: Map<Dimension, Int> = emptyMap(),
    val assisted: Boolean = false,
    val unfamiliar: Boolean = false,
    val independentProduction: Boolean = false,
    val context: ContextTrack = ContextTrack.GENERAL,
    val errors: List<String> = emptyList(),
    val taskVersion: Int = 1,
    val rubricVersion: Int = 1
)

/** Null content denotes an unmapped catalogue entry, never a playable or mastered competency. */
data class CurriculumUnit(
    val id: String,
    val title: String,
    val stage: Int,
    val prerequisites: List<String>,
    val objectives: List<String>,
    val contexts: Map<ContextTrack, String>,
    val content: UnitContent? = null
)

/** Listening is a transcript for read-aloud/TTS, not a claim that audio is bundled. */
data class UnitContent(
    val reading: String,
    val listening: String,
    val expressions: List<String>,
    val grammar: String,
    val question: String,
    val options: List<String>,
    val correctOption: Int,
    val explanation: String,
    val controlledPrompt: String,
    val controlledModel: String,
    val speaking: String,
    val interaction: String,
    val writing: String,
    val transfer: String,
    val feedback: String
)
