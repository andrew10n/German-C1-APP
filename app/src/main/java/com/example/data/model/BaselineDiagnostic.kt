package com.example.data.model

data class DiagnosticQuestion(
    val id: String,
    val dimension: SkillCategory,
    val promptText: String,
    val spokenAudioText: String? = null,
    val options: List<String>,
    val correctIndex: Int,
    val analysisRationale: String
)

data class LearningPathMilestone(
    val stage: Int,
    val title: String,
    val targetArea: String,
    val focusDescription: String,
    val estimatedDurationDays: Int,
    val isPrimaryFocus: Boolean = false
)

data class BaselineDiagnosticReport(
    val isCompleted: Boolean,
    val overallC1Score: Int,
    val functionalReadinessPct: Int,
    val primaryLimitingFactor: String,
    val recommendedStartingPath: String,
    val skillRatings: Map<SkillCategory, Int>,
    val grammarScore: Int = skillRatings[SkillCategory.GRAMMATIK] ?: 0,
    val vocabularyScore: Int = skillRatings[SkillCategory.WORTSCHATZ] ?: 0,
    val grammarTopicMastery: Map<String, Int> = emptyMap(),
    val vocabTopicMastery: Map<String, Int> = emptyMap(),
    val personalizedRoadmap: List<LearningPathMilestone> = emptyList()
)
