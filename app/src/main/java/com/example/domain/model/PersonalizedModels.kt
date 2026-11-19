package com.example.domain.model

enum class VisualSceneType {
    WAVEFORM_RADAR,
    RECEIPT_REGISTER_MORPH,
    POWERTRAIN_SIGNAL_FLOW,
    FUNINO_PITCH_SPLIT,
    PARTICLE_TONE_DIAL,
    MULTI_SPEAKER_TABLE;

    companion object {
        fun fromRaw(raw: String?): VisualSceneType =
            entries.find { it.name.equals(raw, ignoreCase = true) } ?: WAVEFORM_RADAR
    }
}

enum class PersonalizationErrorCategory11(val id: String, val labelDe: String, val targetFixLabel: String) {
    LISTENING_RECOGNITION("LISTENING_RECOGNITION", "Hörerkennung im Schnellsprech-Strom", "Blind-Hören mit Tempo-Fokus"),
    CONNECTED_SPEECH_DECODING("CONNECTED_SPEECH_DECODING", "Verschleifungen & Kurzformen", "Klang-zu-Klartext-Decoder"),
    VOCABULARY("VOCABULARY", "Kontext-Wortschatz & Kollokation", "Satzrahmen statt Einzelwort"),
    INTERPRETATION("INTERPRETATION", "Sprecherabsicht, Ironie & Zwischentöne", "Subtext- & Partikel-Check"),
    SITUATIONAL_UNDERSTANDING("SITUATIONAL_UNDERSTANDING", "Situationsanforderung & Handlungsimpuls", "Situations-Zielbild"),
    ACTIVE_RETRIEVAL("ACTIVE_RETRIEVAL", "Spontaner Ausdrucksabruf ohne Übersetzen", "3-Sekunden-Anker-Abruf"),
    SENTENCE_FORMULATION("SENTENCE_FORMULATION", "Natürlicher Satzbau im Echtzeit-Sprechen", "Gesprochener Satzbaukasten"),
    GRAMMAR_UNDER_PRESSURE("GRAMMAR_UNDER_PRESSURE", "Grammatikstabilität unter Gesprächsdruck", "Klammer- & Modus-Stabilisierung"),
    NATURALNESS("NATURALNESS", "Idiomatik & Modalpartikel-Feingefühl", "Verständlich -> Idiomatisch Upgrade"),
    REGISTER("REGISTER", "Registertreffsicherheit (Alltag / Technik / Platz)", "Doppel-Register-Switchboard"),
    INTERACTION("INTERACTION", "Unterbrechungen, Themenwechsel & Reparatur", "Gegenwind- & Brücken-Reaktion");

    companion object {
        fun fromId(raw: String?): PersonalizationErrorCategory11 =
            entries.find { it.id.equals(raw, ignoreCase = true) } ?: NATURALNESS
    }
}

data class ContentAuditReport(
    val learnerSummary: String,
    val provisionalNote: String,
    val coveredWell: List<String>,
    val coveredSuperficially: List<String>,
    val missingCriticalGaps: List<String>
)

data class BatchValidationReport(
    val batchName: String,
    val totalExercises: Int,
    val scenarioFamiliesCount: Int,
    val skillsCovered: Map<String, Int>,
    val contextDistribution: Map<String, Int>,
    val difficultyDistribution: Map<String, Int>,
    val duplicatesDetected: Int,
    val qualityChecksCompleted: List<String>,
    val missingAudioAssets: List<String>,
    val integrationStatus: String
)

data class ScenarioFamily(
    val id: String,
    val title: String,
    val priorityRank: Int,
    val targetWeakness: String,
    val environment: String,
    val visualTheme: VisualSceneType,
    val description: String
)

data class AudioDeliverySpec(
    val deliveryStyle: String,
    val speakerCount: Int,
    val regionalVariety: String,
    val ttsDisclosure: String,
    val nativeAudioAsset: String
)

data class SpeakerPersona(
    val name: String,
    val role: String,
    val personality: String,
    val variety: String
)

data class TranscriptTurn(
    val speaker: String,
    val line: String,
    val cue: String
)

data class SpeechReductionPair(
    val spoken: String,
    val standard: String,
    val note: String
)

data class ComprehensionProbe(
    val type: String, // COMPREHENSION, INTENTION, IMPLICIT_MEANING
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val acceptableInterpretations: List<String>
)

data class UnderstandableBaseline(
    val userPhrase: String,
    val verdict: String
)

data class RegisterAlternative(
    val register: String,
    val text: String
)

data class PersonalizedExercise(
    val id: String,
    val scenarioFamilyId: String,
    val title: String,
    val skill: String,
    val subskill: String,
    val context: String,
    val subRegister: String,
    val difficulty: String,
    val visualSceneType: VisualSceneType,
    val learningObjective: String,
    val germanInput: String,
    val audioRequirements: AudioDeliverySpec,
    val speakers: List<SpeakerPersona>,
    val transcriptTurns: List<TranscriptTurn>,
    val transcript: String,
    val connectedSpeechReductions: List<SpeechReductionPair>,
    val vocabulary: List<String>,
    val usefulExpressions: List<String>,
    val grammarPatterns: List<String>,
    val comprehensionTasks: List<ComprehensionProbe>,
    val speakingTask: String,
    val interactionTask: String,
    val expectedResponseCharacteristics: List<String>,
    val understandableBaseline: UnderstandableBaseline,
    val acceptableAlternatives: List<RegisterAlternative>,
    val errorClassifications: List<String>,
    val correctionGuidance: String,
    val transferTask: String,
    val delayedRetrievalTarget: String,
    val reviewSchedule: String
) {
    val mappedContextTrack: ContextTrack
        get() = when (context) {
            "AUTOMOTIVE_ENGINEERING" -> ContextTrack.ENGINEERING
            "FOOTBALL_COACHING" -> ContextTrack.COACHING
            else -> if (subRegister.contains("Schwäbisch", ignoreCase = true) ||
                title.contains("Esslingen", ignoreCase = true) && skill == "NATURAL_LISTENING"
            ) ContextTrack.SOCIAL else ContextTrack.GENERAL
        }

    val mappedPillar: C1Pillar
        get() = when (skill) {
            "ENGINEERING_COMMUNICATION" -> C1Pillar.NOMINALISIERUNG
            "NATURAL_LISTENING" -> C1Pillar.SATZGEFUEGE
            "COLLOCATIONS_PARTICLES" -> C1Pillar.NOMEN_VERB
            "SITUATIONAL_PRAGMATICS", "SPONTANEOUS_SPEAKING" -> C1Pillar.MODALPARTIKELN
            else -> C1Pillar.RHETORIK
        }
}

data class PersonalizedLibraryBundle(
    val contentAudit: ContentAuditReport,
    val validationReport: BatchValidationReport,
    val scenarioFamilies: List<ScenarioFamily>,
    val exercises: List<PersonalizedExercise>
)

data class PrioritizedPersonalizedExercise(
    val exercise: PersonalizedExercise,
    val familyTitle: String,
    val priorityScore: Float,
    val blindListeningDone: Boolean,
    val spontaneousSpeakingDone: Boolean,
    val transferDone: Boolean
) {
    val completedBeatsCount: Int
        get() = listOf(blindListeningDone, spontaneousSpeakingDone, transferDone).count { it }
}

data class SpontaneousCoachingEvaluation(
    val meaningCommunicatedWell: Boolean,
    val meaningFeedbackDe: String,
    val naturalnessPercent: Int,
    val primaryErrorCategory: PersonalizationErrorCategory11,
    val detectedTranslationArtifactDe: String,
    val registerAnalysisDe: String,
    val naturalAlternatives: List<String>,
    val targetedRepetitionPromptDe: String,
    val liveTransferChallengeDe: String
)
