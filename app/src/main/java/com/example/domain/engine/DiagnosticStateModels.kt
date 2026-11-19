package com.example.domain.engine

import com.example.data.local.MistakeLedgerEntity
import com.example.data.local.PrunedContentEntity
import com.example.data.local.SrsCardStateEntity
import com.example.domain.model.C1Pillar
import com.example.domain.model.C1QuizItem
import com.example.domain.model.C1VocabCard
import com.example.domain.model.CognitiveLoadLevel
import com.example.domain.model.ContextTrack
import com.example.domain.model.ControllerStrategyMode
import com.example.domain.model.ConversationScenarioItem
import com.example.domain.model.ErrorArchetype
import com.example.domain.model.FootballTacticalScenario
import com.example.domain.model.MediaImmersionMission
import com.example.domain.model.MediationScenarioItem
import com.example.domain.model.PersonalizedExercise
import com.example.domain.model.PersonalizedLibraryBundle
import com.example.domain.model.PrioritizedPersonalizedExercise
import com.example.domain.model.PrioritizedRepoUnit
import com.example.domain.model.RoadmapStageGroup
import com.example.domain.model.VisualLessonModule
import com.example.domain.model.YearReleaseAuditReport

enum class PillarStatusTag(val label: String) {
    VITAL_80_20("80/20 Vital ROI"),
    CRITICAL_GAP("Repair Priority"),
    CALIBRATING("Calibrating"),
    DEVELOPING("Developing"),
    MASTERED("C1 Mastered")
}

data class PillarDiagnosticSnapshot(
    val pillar: C1Pillar,
    val mastery: Float,
    val confidenceSigma: Float,
    val targetDifficultyTier: Int,
    val roiScore: Float,
    val roiSharePercent: Int,
    val isParetoVital20: Boolean,
    val urgencyScore: Float,
    val statusTag: PillarStatusTag,
    val attemptsCount: Int,
    val accuracyPercent: Int,
    val visualSlidesCompleted: Int,
    val unresolvedMistakesCount: Int,
    val aiSlidesAddedCount: Int
)

data class ErrorArchetypeSnapshot(
    val archetype: ErrorArchetype,
    val errorCount: Int,
    val remediationCount: Int,
    val severityScore: Float
)

enum class DirectiveActionType {
    RUN_CALIBRATION_SWEEP,
    FIX_CRITICAL_MISTAKES,
    LAUNCH_PERSONALIZED_LAB,
    LAUNCH_REPO_UNIT,
    LAUNCH_SITUATIONS_SIMULATOR,
    LAUNCH_VISUAL_REPAIR,
    LAUNCH_TARGETED_DRILL,
    LAUNCH_SRS_CONSOLIDATION,
    LAUNCH_AI_WRITING_LAB
}

data class ControllerDirective(
    val actionType: DirectiveActionType,
    val headline: String,
    val rationale: String,
    val badgeText: String,
    val roiBadgeText: String,
    val targetPillar: C1Pillar,
    val targetModuleId: String?,
    val targetRepoUnitId: String? = null,
    val targetPersonalizedId: String? = null,
    val estimatedMinutes: Int
)

data class PrioritizedModule(
    val module: VisualLessonModule,
    val masteryPercent: Int,
    val targetDifficultyTier: Int,
    val roiScore: Float,
    val isParetoVital20: Boolean,
    val priorityRank: Int,
    val badgeLabel: String,
    val controllerReason: String,
    val slidesCompleted: Int,
    val aiSlidesCount: Int
)

data class PrioritizedSrsCard(
    val card: C1VocabCard,
    val state: SrsCardStateEntity?,
    val isDue: Boolean,
    val retrievabilityPercent: Int = 90,
    val roiScore: Float,
    val priorityScore: Float,
    val controllerTag: String
)

data class ParetoSummary(
    val vitalPillars: List<C1Pillar>,
    val vitalGainSharePercent: Int,
    val activeMistakesCount: Int,
    val resolvedMistakesCount: Int,
    val prunedItemsCount: Int,
    val minutesSavedByPruning: Int,
    val aiAddedContentCount: Int,
    val headlineSummary: String
)

enum class DailyBeatType(val shortSectorLabel: String) {
    ADAPTIVE_PROBE_BURST("Präzision"),
    BLIND_LISTENING_SCENE("Hören"),
    SPONTANEOUS_SPEAKING("Spontan"),
    NATIVE_MEDIA_IMMERSION("Echt-Podcast"),
    SRS_VOCAB_BURST("Satzrahmen"),
    TEXTBOOK_VISUAL_BLUEPRINT("Satzbau"),
    LIVE_TACTICAL_TRANSFER("Transfer")
}

/**
 * Real-time content modification overlay synthesized by DiagnosticController
 * to adapt difficulty, acoustic speed, production constraints, and scaffolding on the fly.
 */
data class BeatAdaptationSpec(
    val adaptationBadge: String,
    val modifiedConstraintDe: String,
    val targetLatencySeconds: Int = 7,
    val recommendedAudioRate: Float = 1.15f,
    val scaffoldingLevel: Int = 1 // 0 = Zero-Hint C1+ Pressure, 1 = Precision Contrast, 2 = Fatigue-Adjusted Guided
)

data class EnginePrecisionTelemetry(
    val diagnosticPrecisionPercent: Int = 74,
    val automaticityIndexPercent: Int = 68,
    val meanRetrievabilityPercent: Int = 86,
    val medianResponseSeconds: Float = 6.0f,
    val translationRiskDetected: Boolean = false,
    val reorderedSectorsReason: String = "",
    val activeContentModificationsCount: Int = 0
)

/**
 * Commander Mode Overrides allowing the learner to take manual control over
 * focus pillar, active daily sector mix, audio tempo, and difficulty pressure.
 */
data class CommanderOverrideConfig(
    val commanderEnabled: Boolean = false,
    val pinnedFocusPillar: C1Pillar? = null,
    val enabledBeatTypes: Set<DailyBeatType> = DailyBeatType.entries.toSet(),
    val audioSpeedBias: Float = 1.0f,
    val difficultyOffset: Int = 0
)

/**
 * Visible diagnostic decision entry rendered live in the Engine Cockpit.
 */
data class ControllerDecisionEvent(
    val titleDe: String,
    val detailDe: String,
    val metricBadge: String,
    val isOverride: Boolean = false
)

data class DailySessionBeat(
    val id: String,
    val sectorNumber: Int,
    val beatType: DailyBeatType,
    val title: String,
    val controllerReason: String,
    val estimatedMinutes: Int,
    val targetPillar: C1Pillar,
    val evPerMinute: Float = 0f,
    val adaptation: BeatAdaptationSpec? = null,
    val personalizedExercise: PersonalizedExercise? = null,
    val mediaMission: MediaImmersionMission? = null,
    val repoUnit: PrioritizedRepoUnit? = null,
    val srsCards: List<PrioritizedSrsCard> = emptyList(),
    val quizProbes: List<C1QuizItem> = emptyList(),
    val footballScenario: FootballTacticalScenario? = null,
    val mediationScenario: MediationScenarioItem? = null
)

data class MasterControlState(
    val overallC1Index: Float,
    val cefrLevel: String,
    val functionalC1ReadinessPercent: Int,
    val examReadinessPercent: Int,
    val precisionTelemetry: EnginePrecisionTelemetry = EnginePrecisionTelemetry(),
    val selectedContextTrack: ContextTrack,
    val strategyMode: ControllerStrategyMode,
    val cognitiveLoad: CognitiveLoadLevel,
    val calibrated: Boolean,
    val paretoSummary: ParetoSummary,
    val pillarSnapshots: List<PillarDiagnosticSnapshot>,
    val stagesRoadmap: List<RoadmapStageGroup>,
    val prioritizedRepoUnits: List<PrioritizedRepoUnit>,
    val personalizedLibrary: PersonalizedLibraryBundle,
    val prioritizedPersonalizedExercises: List<PrioritizedPersonalizedExercise>,
    val mediaMissions: List<MediaImmersionMission> = emptyList(),
    val yearAuditReport: YearReleaseAuditReport? = null,
    val conversations: List<ConversationScenarioItem>,
    val mediations: List<MediationScenarioItem>,
    val footballScenarios: List<FootballTacticalScenario>,
    val errorStats: List<ErrorArchetypeSnapshot>,
    val dominantErrorArchetype: ErrorArchetype?,
    val unresolvedMistakes: List<MistakeLedgerEntity>,
    val prunedItems: List<PrunedContentEntity>,
    val directive: ControllerDirective,
    val dailySessionQueue: List<DailySessionBeat> = emptyList(),
    val orderedModules: List<PrioritizedModule>,
    val orderedSrsCards: List<PrioritizedSrsCard>,
    val dueSrsCount: Int,
    val dailyGoalMinutes: Int,
    val minutesToday: Int,
    val streakDays: Int,
    val totalXp: Int,
    val aiDiagnosticBriefing: String,
    val commanderConfig: CommanderOverrideConfig = CommanderOverrideConfig(),
    val decisionFeed: List<ControllerDecisionEvent> = emptyList()
)
