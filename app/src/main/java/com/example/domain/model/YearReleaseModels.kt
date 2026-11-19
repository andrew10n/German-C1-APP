package com.example.domain.model

data class AuditGapResolutionItem(
    val dimension: String,
    val previousStatus: String,
    val finalReleaseSolution: String,
    val itemCountDelivered: Int
)

data class GoetheExamModuleAudit(
    val moduleCode: String,
    val officialFormatDe: String,
    val targetedSubskillsDe: String,
    val embeddedPracticeCount: Int
)

data class YearReleaseAuditReport(
    val releaseVersion: String,
    val totalAnnualCapacityDays: Int,
    val totalMultiSpeakerScenarios: Int,
    val totalContextualVocabChunks: Int,
    val totalAdaptiveIrtProbes: Int,
    val totalTextbookUnits: Int,
    val totalLiveMediaMissions: Int,
    val audioArchitectureSummaryDe: String,
    val gapResolutions: List<AuditGapResolutionItem>,
    val goetheExamMatrix: List<GoetheExamModuleAudit>
)

data class MediaImmersionMission(
    val id: String,
    val title: String,
    val sourceName: String,
    val sourceType: String,
    val podcastFeedId: String,
    val externalUrl: String,
    val contextTrack: ContextTrack,
    val mappedPillar: C1Pillar,
    val difficulty: String,
    val goetheExamPartTag: String,
    val listeningMissionDe: String,
    val keyCollocationsToCatch: List<String>,
    val spokenFeaturesNoteDe: String,
    val checkQuestionDe: String,
    val checkOptions: List<String>,
    val correctOptionIndex: Int,
    val checkExplanationDe: String,
    val activeReactionPromptDe: String,
    val sampleSovereignReactionDe: String
)

data class PostSessionAdaptationSummary(
    val completedSectorsCount: Int,
    val sessionAccuracyPercent: Int,
    val medianLatencySeconds: Float,
    val strategyModeAfter: ControllerStrategyMode,
    val strategyChanged: Boolean,
    val adjustedPillarsSummary: List<String>,
    val prunedLowRoiCount: Int,
    val primaryAdaptationReasonDe: String,
    val nextStintPreviewTitles: List<String>,
    val aiOptimizationTriggered: Boolean
)
