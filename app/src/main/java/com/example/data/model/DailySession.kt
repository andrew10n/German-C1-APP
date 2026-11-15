package com.example.data.model

enum class SessionPhase(val index: Int, val titleDe: String, val durationMin: Int, val description: String) {
    WARM_UP(1, "Diagnostisches Warm-Up", 2, "Kurzer Hörimpuls zur Bestimmung der Tagesform ohne vorzeitige Auflösung"),
    MAIN_CHALLENGE(2, "Haupt-Herausforderung", 7, "Gezielte Schwachstellen-Intervention mit ansteigender Progressionsstufe"),
    ACTIVE_PRODUCTION(3, "Aktive Produktion", 5, "Freies Sprechen oder Schreiben unter Zeitdruck mit Fokus auf Kohärenz"),
    PRECISION_CORRECTION(4, "Präzisions-Korrektur", 2, "Analyse der 2-3 gravierendsten Fehler mit muttersprachlichen Alternativen"),
    TRANSFER_TEST(5, "Transfer-Test", 2, "Autonome Anwendung der Zielstrukturen in neuem Kontext ohne Vorlage")
}

data class DailyTrainingSession(
    val id: String,
    val title: String,
    val targetCategory: SkillCategory,
    val totalMinutes: Int = 18,
    val environment: String, // Alltag, Beruf, Technik
    val warmUpPrompt: WarmUpTask,
    val mainChallenge: MainChallengeTask,
    val productionTask: ProductionTask,
    val correctionTask: CorrectionTask,
    val transferTask: TransferTask
)

data class WarmUpTask(
    val audioSpeakerText: String,
    val accentInfo: String,
    val questionPrompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val diagnosticExplanation: String
)

data class MainChallengeTask(
    val title: String,
    val scenarioContext: String,
    val partnerStatement: String,
    val stage1Prompt: String,
    val stage2Deepening: String,
    val stage3StressInference: String,
    val keyPointsExpected: List<String>
)

data class ProductionTask(
    val isSpeaking: Boolean, // true = Voice, false = Writing
    val situationBrief: String,
    val timeLimitSeconds: Int,
    val keyEvaluationCriteria: List<String>, // Coherence, register, accuracy
    val simulatedAudioPrompt: String
)

data class CorrectionTask(
    val errorItems: List<ErrorIntelligenceItem>
)

data class TransferTask(
    val transferDomain: String, // Completely new context
    val scenarioPrompt: String,
    val benchmarkExpectation: String
)
