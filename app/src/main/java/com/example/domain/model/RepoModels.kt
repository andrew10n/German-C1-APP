package com.example.domain.model

import androidx.annotation.DrawableRes
import com.example.R

/**
 * The 4 authentic real-life C1 tracks from andrew10n/German-C1-APP,
 * paired with their original artwork in res/drawable.
 */
enum class ContextTrack(
    val id: String,
    val shortLabel: String,
    val titleDe: String,
    val subtitleDe: String,
    @DrawableRes val artworkRes: Int
) {
    GENERAL(
        id = "GENERAL",
        shortLabel = "Alltag & Diskurs",
        titleDe = "Souveräner Alltag & Gesellschaft",
        subtitleDe = "Supermarkt, Verträge, spontane Rückfragen & Debatten",
        artworkRes = R.drawable.img_visual_kasse_alltag
    ),
    SOCIAL(
        id = "SOCIAL",
        shortLabel = "Esslingen & Team",
        titleDe = "Kollegenrunde Esslingen & Schwäbisch",
        subtitleDe = "Kaffeeküche, Werksalltag, Zwischentöne & regionale Färbung",
        artworkRes = R.drawable.img_visual_swabia_roundtable
    ),
    ENGINEERING(
        id = "ENGINEERING",
        shortLabel = "AMG & 911 Prüfstand",
        titleDe = "Stuttgart Powertrain, AMG & 911 Dynamik",
        subtitleDe = "ECU/TCU-Applikation, Prüfstand, FMEA & System-Reviews",
        artworkRes = R.drawable.img_amg_911_cockpit
    ),
    COACHING(
        id = "COACHING",
        shortLabel = "BVB 09 & Trainer",
        titleDe = "Schwarzgelb Pressing & Jugend-Coaching",
        subtitleDe = "Gegenpressing im Halbraum, SV 1845 Training & Taktik",
        artworkRes = R.drawable.img_bvb_yellow_pitch
    );

    companion object {
        fun fromId(id: String?): ContextTrack =
            entries.find { it.id.equals(id, ignoreCase = true) } ?: GENERAL
    }
}

enum class MentalDiagramArchetype {
    SPECTRUM,
    BRACKET,
    MATRIX_2X2,
    PIPELINE,
    SCALE,
    BRIDGE,
    FUNNEL,
    LOOP;

    companion object {
        fun fromName(raw: String?): MentalDiagramArchetype =
            entries.find { it.name.equals(raw, ignoreCase = true) } ?: PIPELINE
    }
}

data class VisualStepCard(
    val tag: String,
    val title: String,
    val caption: String,
    val nodes: List<String>,
    val highlightIndex: Int
)

data class ContextTransformPair(
    val sceneLabel: String,
    val avoidLabel: String,
    val avoidPhrase: String,
    val c1Phrase: String,
    val grammarHint: String
)

data class EmbeddedMicroCheck(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class ClauseSnapPuzzle(
    val instruction: String,
    val tiles: List<String>,
    val targetOrder: List<String>,
    val syntaxRule: String
)

data class SentenceSurgeryItem(
    val label: String,
    val before: String,
    val diagnosis: String,
    val after: String,
    val steps: List<String>
)

data class UnitVisualDeck(
    val unitId: String,
    val modelTitle: String,
    val diagramType: MentalDiagramArchetype,
    val visualHook: String,
    val steps: List<VisualStepCard>,
    val contextTransforms: Map<ContextTrack, ContextTransformPair>,
    val microCheck: EmbeddedMicroCheck,
    val clausePuzzle: ClauseSnapPuzzle,
    val surgery: SentenceSurgeryItem
)

data class BookBridgeInfo(
    val unitId: String,
    val bookReference: String,
    val audioFilenameHint: String,
    val originalPrompt: String
) {
    val shortBookBadge: String
        get() = when {
            bookReference.contains("Aspekte neu", ignoreCase = true) -> "Aspekte neu C1"
            bookReference.contains("Sicher!", ignoreCase = true) -> "Sicher! C1"
            bookReference.contains("Goethe", ignoreCase = true) -> "Goethe / Telc C1"
            else -> "Lehrwerk C1"
        }
}

data class ListeningEpisodeInfo(
    val unitId: String,
    val title: String,
    val url: String,
    val task: String
)

data class RepoUnitContent(
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

data class RepoCurriculumUnit(
    val id: String,
    val stage: Int,
    val stageTitle: String,
    val title: String,
    val objectives: List<String>,
    val contexts: Map<ContextTrack, String>,
    val prerequisites: List<String>,
    val mappedPillar: C1Pillar,
    val content: RepoUnitContent,
    val visualDeck: UnitVisualDeck?,
    val bookBridge: BookBridgeInfo?,
    val listeningEpisode: ListeningEpisodeInfo?
)

data class ConversationTurnItem(
    val partnerSpeech: String,
    val expectedUserRolePrompt: String,
    val suggestedC1Connectors: List<String>,
    val targetRegister: String
)

data class UnexpectedInterruptionItem(
    val triggerAfterSecond: Int,
    val partnerInterruptSpeech: String,
    val reactionObjective: String
)

data class ConversationScenarioItem(
    val id: String,
    val title: String,
    val rolePartner: String,
    val environment: String,
    val openingStatement: String,
    val turns: List<ConversationTurnItem>,
    val unexpectedInterruption: UnexpectedInterruptionItem?,
    val evaluationFocus: String
)

data class MediationScenarioItem(
    val id: String,
    val title: String,
    val mediationType: String,
    val sourceMaterial: String,
    val recipientContext: String,
    val criticalPitfallsToAvoid: List<String>,
    val targetReformulationModel: String
)

data class FootballTacticalScenario(
    val id: String,
    val title: String,
    val matchPhase: String,
    val situationDescription: String,
    val options: List<String>,
    val correctIndex: Int,
    val coachingExplanation: String,
    val keyCommandToShout: String
)

data class SituationAiFeedback(
    val scorePercent: Int,
    val registerVerdict: String,
    val strengthsDe: String,
    val upgradedResponseDe: String,
    val keyTakeawayDe: String
)

data class PrioritizedRepoUnit(
    val unit: RepoCurriculumUnit,
    val masteryPercent: Int,
    val roiScore: Float,
    val isParetoVital20: Boolean,
    val targetDifficultyTier: Int,
    val visualDone: Boolean,
    val puzzleDone: Boolean,
    val bookDone: Boolean,
    val situationDone: Boolean,
    val completedStepsCount: Int,
    val statusBadge: String
)

data class RoadmapStageGroup(
    val stageIndex: Int,
    val stageTitle: String,
    val averageMasteryPercent: Int,
    val hasVital20Unit: Boolean,
    val units: List<PrioritizedRepoUnit>
)
