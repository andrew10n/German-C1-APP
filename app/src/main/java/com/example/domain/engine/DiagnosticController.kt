package com.example.domain.engine

import com.example.ai.GeminiC1Service
import com.example.data.curriculum.C1CurriculumRepository
import com.example.data.local.AiGeneratedQuizEntity
import com.example.data.local.AiGeneratedSlideEntity
import com.example.data.local.AiGeneratedVocabEntity
import com.example.data.local.ControllerConfigEntity
import com.example.data.local.DiagnosticDao
import com.example.data.local.DiagnosticSessionLogEntity
import com.example.data.local.ErrorPatternEntity
import com.example.data.local.MistakeLedgerEntity
import com.example.data.local.PillarMasteryEntity
import com.example.data.local.PrunedContentEntity
import com.example.data.local.RepoUnitProgressEntity
import com.example.data.local.SrsCardStateEntity
import com.example.domain.model.AiMutationReport
import com.example.domain.model.C1Pillar
import com.example.domain.model.C1QuizItem
import com.example.domain.model.C1VocabCard
import com.example.domain.model.CognitiveLoadLevel
import com.example.domain.model.ContextTrack
import com.example.domain.model.ControllerStrategyMode
import com.example.domain.model.ErrorArchetype
import com.example.domain.model.PersonalizationErrorCategory11
import com.example.domain.model.PersonalizedExercise
import com.example.domain.model.PostSessionAdaptationSummary
import com.example.domain.model.PrioritizedPersonalizedExercise
import com.example.domain.model.PrioritizedRepoUnit
import com.example.domain.model.RoadmapStageGroup
import com.example.domain.model.SituationAiFeedback
import com.example.domain.model.SpontaneousCoachingEvaluation
import com.example.domain.model.VisualDiagramType
import com.example.domain.model.VisualLessonModule
import com.example.domain.model.VisualSlide
import com.example.domain.model.WritingDiagnosticResult
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

typealias AdaptiveDiagnosticController = DiagnosticController

/**
 * State-of-the-Art Psychometric & Decision-Theoretic Controller:
 * - Kalman-Bayesian 3PL + Response-Time IRT (detects automaticity vs. mental translation)
 * - FSRS-4.5 Power-Law Forgetting Curve R(t, S) & Desirable-Difficulty Scheduling
 * - Expected-Value-Per-Minute (EVPM) Dynamic Knapsack & Cognitive-Ergonomic Reordering
 * - Real-Time On-the-Fly Content Adaptation + Closed-Loop Firebase AI Curriculum Mutation
 */
class DiagnosticController(
    private val dao: DiagnosticDao,
    private val curriculum: C1CurriculumRepository,
    private val geminiService: GeminiC1Service = GeminiC1Service()
) {
    suspend fun ensureInitialized() {
        val existingPillars = dao.getAllPillarMasteryOnce()
        val now = System.currentTimeMillis()
        if (existingPillars.isEmpty()) {
            // Day-1 Profile Seeding (Zero Initial Assessment): initialized from known B2+->C1 learner priors
            val initialPillars = C1Pillar.entries.map { pillar ->
                val (baseScore, initSigma) = when (pillar) {
                    C1Pillar.NOMINALISIERUNG -> 48f to 20f
                    C1Pillar.SATZGEFUEGE -> 45f to 18f
                    C1Pillar.MODALPARTIKELN -> 46f to 18f
                    C1Pillar.RHETORIK -> 49f to 17f
                    C1Pillar.KONJUNKTIV -> 54f to 16f
                    C1Pillar.NOMEN_VERB -> 56f to 15f
                }
                PillarMasteryEntity(
                    pillarId = pillar.id,
                    masteryScore = baseScore,
                    confidenceSigma = initSigma,
                    targetDifficultyTier = PsychometricMathEngine.computeOptimalDifficultyTier(baseScore, initSigma),
                    attemptsCount = 0,
                    correctCount = 0,
                    recentStreak = 0,
                    visualSlidesCompleted = 0,
                    lastPracticedAt = now
                )
            }
            dao.upsertAllPillarMastery(initialPillars)
        }

        if (dao.getErrorPatternsOnce().isEmpty()) {
            dao.upsertAllErrorPatterns(
                ErrorArchetype.entries.map { ErrorPatternEntity(it.id, 0, 0, 0L) }
            )
        }

        val existingConfig = dao.getControllerConfigOnce()
        if (existingConfig == null) {
            dao.upsertControllerConfig(
                ControllerConfigEntity(
                    id = 1,
                    strategyModeId = ControllerStrategyMode.PARETO_80_20.id,
                    dailyGoalMinutes = 15,
                    minutesToday = 4,
                    streakDays = 1,
                    totalXp = 120,
                    lastActiveEpochDay = currentEpochDay(),
                    diagnosticCalibrated = true,
                    selectedContextTrackId = ContextTrack.ENGINEERING.id
                )
            )
        } else if (!existingConfig.diagnosticCalibrated) {
            dao.upsertControllerConfig(existingConfig.copy(diagnosticCalibrated = true))
        }
    }

    fun buildMasterControlState(
        pillars: List<PillarMasteryEntity>,
        errorPatterns: List<ErrorPatternEntity>,
        srsStates: List<SrsCardStateEntity>,
        recentLogs: List<DiagnosticSessionLogEntity>,
        configEntity: ControllerConfigEntity?,
        mistakes: List<MistakeLedgerEntity> = emptyList(),
        prunedItems: List<PrunedContentEntity> = emptyList(),
        aiSlides: List<AiGeneratedSlideEntity> = emptyList(),
        aiVocab: List<AiGeneratedVocabEntity> = emptyList(),
        aiQuiz: List<AiGeneratedQuizEntity> = emptyList(),
        unitProgress: List<RepoUnitProgressEntity> = emptyList(),
        stintOffset: Int = 0,
        commanderOverride: CommanderOverrideConfig = CommanderOverrideConfig()
    ): MasterControlState {
        val now = System.currentTimeMillis()
        val config = configEntity ?: ControllerConfigEntity(
            1, ControllerStrategyMode.PARETO_80_20.id, 15, 0, 1, 0, currentEpochDay(), false, "GENERAL"
        )
        val selectedTrack = ContextTrack.fromId(config.selectedContextTrackId)
        val strategyMode = ControllerStrategyMode.entries.find { it.id == config.strategyModeId }
            ?: ControllerStrategyMode.PARETO_80_20

        val prunedIdSet = prunedItems.map { it.contentId }.toSet()
        val unresolvedMistakes = mistakes.filter { !it.isResolved }
        val resolvedMistakesCount = mistakes.count { it.isResolved }
        val mistakesByPillar = unresolvedMistakes.groupBy { it.pillarId }

        val errorMap = errorPatterns.associateBy { it.archetypeId }
        val errorSnapshots = ErrorArchetype.entries.map { arch ->
            val entity = errorMap[arch.id]
            val errs = entity?.errorCount ?: 0
            val rems = entity?.remediationCount ?: 0
            val recencyBoost = if (entity != null && entity.lastTriggeredAt > 0L) {
                val hoursAgo = ((now - entity.lastTriggeredAt).coerceAtLeast(0L) / 3_600_000f)
                if (hoursAgo < 24f) 1.8f else 0f
            } else 0f
            val severity = max(0f, errs * 1.65f - rems * 0.65f + recencyBoost)
            ErrorArchetypeSnapshot(arch, errs, rems, severity)
        }.sortedByDescending { it.severityScore }

        val dominantError = errorSnapshots.firstOrNull { it.errorCount > 0 }?.archetype

        val aiSlidesByPillar = aiSlides.groupBy { it.pillarId }
        val pillarMap = pillars.associateBy { it.pillarId }

        // Step 1: Kalman Temporal Projection + Bayesian Pareto 80/20 Marginal ROI per Pillar
        data class RawPillarCalc(
            val pillar: C1Pillar,
            val mastery: Float,
            val sigma: Float,
            val tier: Int,
            val roiRaw: Float,
            val urgency: Float,
            val attempts: Int,
            val accuracy: Int,
            val slidesDone: Int,
            val unresolvedCount: Int,
            val aiSlidesCount: Int
        )

        val rawCalcs = C1Pillar.entries.map { pillar ->
            val entity = pillarMap[pillar.id]
            val rawMastery = entity?.masteryScore ?: 52f
            val rawSigma = entity?.confidenceSigma ?: 22f
            val lastAt = entity?.lastPracticedAt ?: now
            val (mastery, sigma) = PsychometricMathEngine.projectTemporalPrior(rawMastery, rawSigma, lastAt, now)

            val tier = (entity?.targetDifficultyTier ?: PsychometricMathEngine.computeOptimalDifficultyTier(mastery, sigma))
                .coerceIn(1, 4)
            val attempts = entity?.attemptsCount ?: 0
            val correct = entity?.correctCount ?: 0
            val slidesDone = entity?.visualSlidesCompleted ?: 0
            val unresolvedCount = mistakesByPillar[pillar.id]?.size ?: 0
            val aiSlidesCount = aiSlidesByPillar[pillar.id]?.size ?: 0
            val daysSince = ((now - lastAt).coerceAtLeast(0L) / 86_400_000f).coerceIn(0f, 14f)

            val baseModule = curriculum.getModuleForPillar(pillar)
            val estMinutes = baseModule.estimatedMinutes.coerceAtLeast(3).toFloat()

            // Context-Track Relevance Multiplier (aligns pillar ROI with active AMG/911, BVB, or Esslingen focus)
            val trackAffinity = when (selectedTrack) {
                ContextTrack.ENGINEERING -> when (pillar) {
                    C1Pillar.NOMINALISIERUNG, C1Pillar.NOMEN_VERB, C1Pillar.KONJUNKTIV -> 1.18f
                    else -> 1.0f
                }
                ContextTrack.COACHING -> when (pillar) {
                    C1Pillar.MODALPARTIKELN, C1Pillar.SATZGEFUEGE, C1Pillar.RHETORIK -> 1.18f
                    else -> 1.0f
                }
                ContextTrack.SOCIAL -> when (pillar) {
                    C1Pillar.MODALPARTIKELN, C1Pillar.SATZGEFUEGE -> 1.20f
                    else -> 1.0f
                }
                ContextTrack.GENERAL -> 1.0f
            }

            val headroom = (100f - mastery).coerceAtLeast(4f).pow(1.14f)
            // Upper Confidence Bound (UCB) exploration bonus from Bayesian posterior uncertainty sigma
            val infoMultiplier = 1f + (sigma / 34f)
            val mistakeMultiplier = 1f + (unresolvedCount * 0.34f)
            val roiRaw = ((pillar.examWeight * 100f * headroom * infoMultiplier * mistakeMultiplier * trackAffinity) / (estMinutes * 18f))

            val urgency = headroom * strategyMode.remediationWeight +
                sigma * 0.92f +
                pillar.examWeight * 45f +
                unresolvedCount * 19f +
                daysSince * 2.2f

            val accuracy = if (attempts > 0) ((correct.toFloat() / attempts) * 100f).roundToInt() else mastery.roundToInt()

            RawPillarCalc(
                pillar, mastery, sigma, tier, roiRaw, urgency,
                attempts, accuracy, slidesDone, unresolvedCount, aiSlidesCount
            )
        }.sortedByDescending {
            if (strategyMode == ControllerStrategyMode.PARETO_80_20) it.roiRaw else it.urgency
        }

        val totalRoiSum = rawCalcs.sumOf { it.roiRaw.toDouble() }.toFloat().coerceAtLeast(1f)
        val vitalPillarIds = rawCalcs.take(2).map { it.pillar.id }.toSet()

        val snapshots = rawCalcs.map { raw ->
            val sharePct = ((raw.roiRaw / totalRoiSum) * 100f).roundToInt().coerceIn(1, 95)
            val isVital = raw.pillar.id in vitalPillarIds
            val status = when {
                isVital && raw.mastery < 82f -> PillarStatusTag.VITAL_80_20
                raw.sigma > 18.5f && raw.attempts < 3 -> PillarStatusTag.CALIBRATING
                raw.mastery < 60f || raw.unresolvedCount >= 2 -> PillarStatusTag.CRITICAL_GAP
                raw.mastery >= 83f && raw.sigma <= 12f -> PillarStatusTag.MASTERED
                else -> PillarStatusTag.DEVELOPING
            }
            PillarDiagnosticSnapshot(
                pillar = raw.pillar,
                mastery = raw.mastery,
                confidenceSigma = raw.sigma,
                targetDifficultyTier = raw.tier,
                roiScore = raw.roiRaw,
                roiSharePercent = sharePct,
                isParetoVital20 = isVital,
                urgencyScore = raw.urgency,
                statusTag = status,
                attemptsCount = raw.attempts,
                accuracyPercent = raw.accuracy,
                visualSlidesCompleted = raw.slidesDone,
                unresolvedMistakesCount = raw.unresolvedCount,
                aiSlidesAddedCount = raw.aiSlidesCount
            )
        }

        val vitalShareRaw = snapshots.take(2).sumOf { it.roiSharePercent }
        val vitalLeveragePercent = max(vitalShareRaw, snapshots.take(3).sumOf { it.roiSharePercent }.coerceAtMost(86))

        val overallIndex = snapshots.sumOf { (it.mastery * it.pillar.examWeight).toDouble() }.toFloat()
            .coerceIn(0f, 100f)

        val cefrLevel = when {
            overallIndex < 42f -> "B2.1"
            overallIndex < 55f -> "B2.2"
            overallIndex < 68f -> "C1.1"
            overallIndex < 82f -> "C1.2"
            else -> "C1+ Prüfbereit"
        }

        // Step 2: FSRS-4.5 Power-Law Retrievability R(t, S) & Desirable-Difficulty Ranking
        val mergedVocab = buildActiveVocabList(aiVocab, prunedIdSet)
        val srsMap = srsStates.associateBy { it.cardId }
        val pillarSnapMap = snapshots.associateBy { it.pillar }

        val orderedSrs = mergedVocab.map { card ->
            val st = srsMap[card.id]
            val snap = pillarSnapMap[card.pillar]
            val retrievability = PsychometricMathEngine.computeFsrsRetrievability(st, now)
            val retrievabilityPct = (retrievability * 100f).roundToInt().coerceIn(5, 100)
            val fsrsUrgency = PsychometricMathEngine.computeFsrsUrgency(st, retrievability)
            val isDue = st == null || st.nextReviewAt <= now || retrievability <= 0.90f

            val pillarRoi = snap?.roiScore ?: 20f
            val aiBoost = if (card.isAiGenerated) 24f else 0f
            val vitalBoost = if (snap?.isParetoVital20 == true) 22f else 0f
            val priority = fsrsUrgency + pillarRoi * 0.78f + aiBoost + vitalBoost

            val tag = when {
                card.isAiGenerated -> "AI Custom ROI"
                (st?.lapses ?: 0) >= 2 || (st != null && retrievabilityPct < 75) -> "Retention Rescue ($retrievabilityPct% R)"
                snap?.isParetoVital20 == true -> "80/20 Vital ($retrievabilityPct% R)"
                st == null -> "High-Yield C1"
                isDue -> "90%-Cliff Due ($retrievabilityPct% R)"
                else -> "Stabil ($retrievabilityPct% R)"
            }
            PrioritizedSrsCard(
                card = card,
                state = st,
                isDue = isDue,
                retrievabilityPercent = retrievabilityPct,
                roiScore = pillarRoi,
                priorityScore = priority,
                controllerTag = tag
            )
        }.sortedByDescending { it.priorityScore }

        val dueCount = orderedSrs.count { it.isDue }

        // Step 3: Multi-Signal Drift-Diffusion Cognitive Load & Precision Diagnostics
        val trackedRetrievabilities = srsStates.map { PsychometricMathEngine.computeFsrsRetrievability(it, now) }
        val (cognitiveLoad, rawPrecisionTelemetry) = PsychometricMathEngine.analyzeSessionTelemetry(
            logs = recentLogs,
            pillarSigmas = snapshots.map { it.confidenceSigma },
            srsRetrievabilities = trackedRetrievabilities
        )

        // Step 4: Enrich Visual Modules with AI-Synthesized Slides & Pruned Filter
        val orderedModules = snapshots.mapIndexed { index, snap ->
            val enrichedModule = buildEnrichedModule(snap.pillar, aiSlidesByPillar[snap.pillar.id].orEmpty(), prunedIdSet)
            val badge = when {
                snap.isParetoVital20 -> "80/20 VITAL ROI"
                snap.unresolvedMistakesCount > 0 -> "FIX ${snap.unresolvedMistakesCount} MISTAKES"
                snap.aiSlidesAddedCount > 0 -> "AI ADAPTED"
                snap.statusTag == PillarStatusTag.MASTERED -> "MASTERED (LOW ROI)"
                else -> "TIER L${snap.targetDifficultyTier}"
            }
            val reason = buildString {
                append("ROI ${snap.roiScore.roundToInt()} pts · Tier L${snap.targetDifficultyTier}/4 · ")
                if (snap.unresolvedMistakesCount > 0) {
                    append("${snap.unresolvedMistakesCount} active mistake(s) queued.")
                } else if (snap.isParetoVital20) {
                    append("Captures ${snap.roiSharePercent}% of immediate C1 exam upside.")
                } else {
                    append("Mastery ${snap.mastery.roundToInt()}% (±${snap.confidenceSigma.roundToInt()}%).")
                }
            }
            PrioritizedModule(
                module = enrichedModule,
                masteryPercent = snap.mastery.roundToInt(),
                targetDifficultyTier = snap.targetDifficultyTier,
                roiScore = snap.roiScore,
                isParetoVital20 = snap.isParetoVital20,
                priorityRank = index + 1,
                badgeLabel = badge,
                controllerReason = reason,
                slidesCompleted = snap.visualSlidesCompleted,
                aiSlidesCount = snap.aiSlidesAddedCount
            )
        }

        val minutesSaved = prunedItems.sumOf { it.minutesSaved }
        val totalAiAdded = aiSlides.size + aiVocab.size + aiQuiz.size
        val paretoSummary = ParetoSummary(
            vitalPillars = snapshots.take(2).map { it.pillar },
            vitalGainSharePercent = vitalLeveragePercent,
            activeMistakesCount = unresolvedMistakes.size,
            resolvedMistakesCount = resolvedMistakesCount,
            prunedItemsCount = prunedItems.size,
            minutesSavedByPruning = minutesSaved,
            aiAddedContentCount = totalAiAdded,
            headlineSummary = "Focusing on ${snapshots[0].pillar.shortLabel} & ${snapshots[1].pillar.shortLabel} drives $vitalLeveragePercent% of your next C1 score jump."
        )

        // Step 5: Dynamic Reordering of 28 Repo Units by Bayesian Pillar ROI, Mistakes & Track
        val progressMap = unitProgress.associateBy { it.unitId }
        val rawRepoUnits = curriculum.getAllRepoUnits().map { repoUnit ->
            val pSnap = pillarSnapMap[repoUnit.mappedPillar]
            val prog = progressMap[repoUnit.id]
            val stepsCount = listOfNotNull(
                prog?.visualDone?.takeIf { it },
                prog?.puzzleDone?.takeIf { it },
                prog?.bookDone?.takeIf { it },
                prog?.situationDone?.takeIf { it }
            ).size
            val baseMastery = pSnap?.mastery ?: 50f
            val unitMastery = prog?.masteryScore ?: (baseMastery * 0.85f + stepsCount * 5f).coerceIn(18f, 95f)
            val stageWeight = when (repoUnit.stage) {
                1, 2, 3 -> 1.28f
                4, 5 -> 1.16f
                else -> 0.94f
            }
            val incompleteSteps = 4 - stepsCount
            val mistakeBoost = (pSnap?.unresolvedMistakesCount ?: 0) * 14f
            val vitalBoost = if (pSnap?.isParetoVital20 == true) 20f else 0f
            val sigmaExploreBoost = (pSnap?.confidenceSigma ?: 15f) * 0.45f
            val unitRoi = ((100f - unitMastery).coerceAtLeast(5f) * stageWeight * (0.75f + incompleteSteps * 0.16f)) +
                vitalBoost + mistakeBoost + sigmaExploreBoost
            Triple(repoUnit, prog to stepsCount, unitMastery.roundToInt() to unitRoi)
        }

        val vitalUnitIds = rawRepoUnits
            .sortedByDescending { it.third.second }
            .take(6)
            .map { it.first.id }
            .toSet()

        val allPrioritizedRepoUnits = rawRepoUnits.map { (u, progPair, scorePair) ->
            val (prog, stepsDone) = progPair
            val (masteryPct, roi) = scorePair
            val isVital = u.id in vitalUnitIds && masteryPct < 85
            val tier = pillarSnapMap[u.mappedPillar]?.targetDifficultyTier ?: 2
            val badge = when {
                stepsDone >= 4 || masteryPct >= 85 -> "GEMEISTERT"
                isVital -> "80/20 VITAL"
                stepsDone > 0 -> "$stepsDone/4 AKTIV"
                else -> u.bookBridge?.shortBookBadge ?: "STUFE ${u.stage}"
            }
            PrioritizedRepoUnit(
                unit = u,
                masteryPercent = masteryPct,
                roiScore = roi,
                isParetoVital20 = isVital,
                targetDifficultyTier = tier,
                visualDone = prog?.visualDone == true,
                puzzleDone = prog?.puzzleDone == true,
                bookDone = prog?.bookDone == true,
                situationDone = prog?.situationDone == true,
                completedStepsCount = stepsDone,
                statusBadge = badge
            )
        }

        val stagesList = curriculum.getStages()
        val stagesRoadmap = stagesList.mapIndexed { stageIdx, stageTitle ->
            val stageUnits = allPrioritizedRepoUnits.filter { it.unit.stage == stageIdx }
            val avgMastery = if (stageUnits.isNotEmpty()) {
                stageUnits.map { it.masteryPercent }.average().roundToInt()
            } else 50
            RoadmapStageGroup(
                stageIndex = stageIdx,
                stageTitle = stageTitle,
                averageMasteryPercent = avgMastery,
                hasVital20Unit = stageUnits.any { it.isParetoVital20 },
                units = stageUnits
            )
        }

        val sortedRepoUnits = allPrioritizedRepoUnits.sortedByDescending { it.roiScore }
        val topVitalRepoUnit = sortedRepoUnits.firstOrNull()

        // Step 6: Precision-Ranked Personalized Scenarios (AMG / 911 / BVB / Esslingen)
        val personalizedLib = curriculum.getPersonalizedLibrary()
        val familyMap = personalizedLib.scenarioFamilies.associateBy { it.id }
        val prioritizedPersonalized = personalizedLib.exercises.map { ex ->
            val prog = progressMap[ex.id]
            val blindDone = prog?.visualDone == true
            val speakDone = prog?.situationDone == true
            val transferDone = prog?.puzzleDone == true
            val doneCount = listOf(blindDone, speakDone, transferDone).count { it }
            val exSnap = pillarSnapMap[ex.mappedPillar]

            val skillWeight = when (ex.skill) {
                "NATURAL_LISTENING" -> 35f
                "SPONTANEOUS_SPEAKING" -> if (rawPrecisionTelemetry.translationRiskDetected) 42f else 33f
                "SITUATIONAL_PRAGMATICS" -> 30f
                "COLLOCATIONS_PARTICLES" -> 26f
                "ENGINEERING_COMMUNICATION" -> if (selectedTrack == ContextTrack.ENGINEERING) 40f else 24f
                "FOOTBALL_COACHING" -> if (selectedTrack == ContextTrack.COACHING) 40f else 24f
                else -> 20f
            }
            val trackMatchBoost = if (ex.mappedContextTrack == selectedTrack) 22f else 0f
            val incompleteBoost = (3 - doneCount) * 15f
            val pillarGapBoost = ((100f - (exSnap?.mastery ?: 55f)) * 0.28f)
            val mistakeMatchBoost = if (unresolvedMistakes.any { m ->
                    m.pillarId == ex.mappedPillar.id ||
                        ex.errorClassifications.any { it.startsWith(m.contextSnippet.substringBefore(":"), ignoreCase = true) }
                }
            ) 24f else 0f

            PrioritizedPersonalizedExercise(
                exercise = ex,
                familyTitle = familyMap[ex.scenarioFamilyId]?.title ?: ex.subskill,
                priorityScore = skillWeight + trackMatchBoost + incompleteBoost + pillarGapBoost + mistakeMatchBoost,
                blindListeningDone = blindDone,
                spontaneousSpeakingDone = speakDone,
                transferDone = transferDone
            )
        }.sortedByDescending { it.priorityScore }

        val directive = synthesizeDirective(
            calibrated = config.diagnosticCalibrated,
            snapshots = snapshots,
            orderedModules = orderedModules,
            topRepoUnit = topVitalRepoUnit,
            topPersonalized = prioritizedPersonalized.firstOrNull(),
            selectedTrack = selectedTrack,
            unresolvedMistakes = unresolvedMistakes,
            dominantError = dominantError,
            cognitiveLoad = cognitiveLoad,
            strategyMode = strategyMode,
            dueCount = dueCount,
            overallIndex = overallIndex
        )

        // Separate Functional C1 (Listening, Spontaneous Automaticity, Pragmatics) vs. Goethe Exam C1
        val functionalPillarsAvg = listOf(
            pillarSnapMap[C1Pillar.MODALPARTIKELN]?.mastery ?: 54f,
            pillarSnapMap[C1Pillar.SATZGEFUEGE]?.mastery ?: 52f,
            pillarSnapMap[C1Pillar.NOMEN_VERB]?.mastery ?: 58f
        ).average().toFloat()
        val personalizedCompletedBeats = prioritizedPersonalized.sumOf { it.completedBeatsCount }
        val automaticityBonus = (rawPrecisionTelemetry.automaticityIndexPercent - 60) * 0.12f
        val functionalReadiness = (
            functionalPillarsAvg * 0.76f +
                min(18f, personalizedCompletedBeats * 1.2f) +
                automaticityBonus +
                min(4f, config.streakDays.toFloat())
            ).roundToInt().coerceIn(0, 100)

        val examPillarsAvg = listOf(
            pillarSnapMap[C1Pillar.NOMINALISIERUNG]?.mastery ?: 48f,
            pillarSnapMap[C1Pillar.KONJUNKTIV]?.mastery ?: 55f,
            pillarSnapMap[C1Pillar.RHETORIK]?.mastery ?: 50f
        ).average().toFloat()
        val readiness = (
            (overallIndex * 0.50f + examPillarsAvg * 0.35f) +
                (if (config.diagnosticCalibrated) 10f else 0f) +
                min(5f, config.streakDays.toFloat())
            ).roundToInt().coerceIn(0, 100)

        // Step 7: State-of-the-Art EVPM Stint Sequence Optimizer & Content Adapter
        val activeQuizSync = buildActiveQuizListSync(aiQuiz, prunedIdSet)
        val mediaMissions = curriculum.getMediaImmersionMissions()
        val completedMediaIds = unitProgress
            .filter { it.unitId.startsWith("media_") }
            .map { it.unitId.removePrefix("media_") }
            .toSet()

        val stintPlan = StintSequenceOptimizer.buildOptimalStint(
            dailyGoalMinutes = config.dailyGoalMinutes,
            calibrated = config.diagnosticCalibrated,
            snapshots = snapshots,
            unresolvedMistakes = unresolvedMistakes,
            dominantError = dominantError,
            cognitiveLoad = cognitiveLoad,
            translationRiskDetected = rawPrecisionTelemetry.translationRiskDetected,
            strategyMode = strategyMode,
            prioritizedPersonalized = prioritizedPersonalized,
            prioritizedRepoUnits = sortedRepoUnits,
            orderedSrs = orderedSrs,
            activeQuizPool = activeQuizSync,
            footballList = curriculum.getFootballScenarios(),
            mediationList = curriculum.getMediations(),
            selectedTrack = selectedTrack,
            stintOffset = stintOffset,
            mediaMissions = mediaMissions,
            completedMediaIds = completedMediaIds,
            commanderOverride = commanderOverride
        )

        val finalPrecisionTelemetry = rawPrecisionTelemetry.copy(
            reorderedSectorsReason = stintPlan.decisionHeadlineDe,
            activeContentModificationsCount = stintPlan.modificationsCount
        )

        val topBeat = stintPlan.orderedBeats.firstOrNull()
        val topVitalSnap = snapshots.first()
        val decisionFeed = buildList {
            add(
                ControllerDecisionEvent(
                    titleDe = if (commanderOverride.commanderEnabled) "Commander-Steuerung aktiv" else "Sektor-1 Sieger (EV/min)",
                    detailDe = stintPlan.decisionHeadlineDe,
                    metricBadge = "${topBeat?.evPerMinute?.roundToInt() ?: 18} EV/m",
                    isOverride = commanderOverride.commanderEnabled
                )
            )
            add(
                ControllerDecisionEvent(
                    titleDe = "3PL-RT Automatik & Latenz",
                    detailDe = if (finalPrecisionTelemetry.translationRiskDetected) {
                        "Median ${finalPrecisionTelemetry.medianResponseSeconds}s (>12.5s) → Übersetzungs-Schutz & Direktabruf geschaltet."
                    } else {
                        "Median ${finalPrecisionTelemetry.medianResponseSeconds}s · Unsicherheit ±${topVitalSnap.confidenceSigma.roundToInt()}% σ auf ${topVitalSnap.pillar.shortLabel}."
                    },
                    metricBadge = "${finalPrecisionTelemetry.automaticityIndexPercent}% Auto"
                )
            )
            add(
                ControllerDecisionEvent(
                    titleDe = "FSRS-4.5 Gedächtnis-Kante",
                    detailDe = "$dueCount Satzrahmen unter 90%-Abruf-Schwelle (Ø ${finalPrecisionTelemetry.meanRetrievabilityPercent}% R).",
                    metricBadge = "$dueCount fällig"
                )
            )
            add(
                ControllerDecisionEvent(
                    titleDe = "Fehlerspeicher & 80/20 Filter",
                    detailDe = if (unresolvedMistakes.isNotEmpty()) {
                        "${unresolvedMistakes.size} aktive Fehler priorisiert · ${prunedItems.size} leichte Items gefiltert."
                    } else {
                        "0 akute Fehler · ${prunedItems.size} leichte Items entfernt (${minutesSaved}m gespart)."
                    },
                    metricBadge = if (unresolvedMistakes.isNotEmpty()) "${unresolvedMistakes.size} Repair" else "${minutesSaved}m+"
                )
            )
        }

        val briefing = buildString {
            append("Functional C1: $functionalReadiness% | Goethe Exam C1: $readiness% ($cefrLevel). ")
            append("Precision: ${finalPrecisionTelemetry.diagnosticPrecisionPercent}% | Automaticity: ${finalPrecisionTelemetry.automaticityIndexPercent}% (Median ${finalPrecisionTelemetry.medianResponseSeconds}s). ")
            if (finalPrecisionTelemetry.translationRiskDetected) {
                append("TRANSLATION LATENCY DETECTED (>12.5s) -> Prioritize rapid non-translated German sentence frames. ")
            }
            append("Track: ${selectedTrack.titleDe} (Mercedes-AMG Powertrain / Porsche 911 / BVB 09 & SV 1845 Esslingen). ")
            if (unresolvedMistakes.isNotEmpty()) {
                append("Active Mistakes (${unresolvedMistakes.size}): ")
                append(unresolvedMistakes.take(3).joinToString("; ") { "'${it.wrongChoice}' -> '${it.correctForm}'" })
                append(". ")
            }
            append("Pruned Low-ROI Items: ${prunedItems.size} (${minutesSaved}m saved).")
        }

        return MasterControlState(
            overallC1Index = overallIndex,
            cefrLevel = cefrLevel,
            functionalC1ReadinessPercent = functionalReadiness,
            examReadinessPercent = readiness,
            precisionTelemetry = finalPrecisionTelemetry,
            selectedContextTrack = selectedTrack,
            strategyMode = strategyMode,
            cognitiveLoad = cognitiveLoad,
            calibrated = config.diagnosticCalibrated,
            paretoSummary = paretoSummary,
            pillarSnapshots = snapshots,
            stagesRoadmap = stagesRoadmap,
            prioritizedRepoUnits = sortedRepoUnits,
            personalizedLibrary = personalizedLib,
            prioritizedPersonalizedExercises = prioritizedPersonalized,
            mediaMissions = mediaMissions,
            yearAuditReport = curriculum.getYearAuditReport(),
            conversations = curriculum.getConversations(),
            mediations = curriculum.getMediations(),
            footballScenarios = curriculum.getFootballScenarios(),
            errorStats = errorSnapshots,
            dominantErrorArchetype = dominantError,
            unresolvedMistakes = unresolvedMistakes,
            prunedItems = prunedItems,
            directive = directive,
            dailySessionQueue = stintPlan.orderedBeats,
            orderedModules = orderedModules,
            orderedSrsCards = orderedSrs,
            dueSrsCount = dueCount,
            dailyGoalMinutes = config.dailyGoalMinutes,
            minutesToday = config.minutesToday,
            streakDays = config.streakDays,
            totalXp = config.totalXp,
            aiDiagnosticBriefing = briefing,
            commanderConfig = commanderOverride,
            decisionFeed = decisionFeed
        )
    }

    private fun buildActiveQuizListSync(
        aiQuizEntities: List<AiGeneratedQuizEntity>,
        prunedIds: Set<String>
    ): List<C1QuizItem> {
        val dynamicItems = aiQuizEntities.mapNotNull { entity ->
            val pillar = C1Pillar.entries.find { it.id == entity.pillarId } ?: return@mapNotNull null
            val archetype = ErrorArchetype.entries.find { it.id == entity.archetypeId }
                ?: ErrorArchetype.REGISTER_COLLOCATION
            C1QuizItem(
                id = entity.id,
                pillar = pillar,
                difficulty = entity.difficulty,
                archetype = archetype,
                promptDe = entity.promptDe,
                contextSnippet = entity.contextSnippet,
                options = listOf(entity.option0, entity.option1, entity.option2, entity.option3),
                correctIndex = entity.correctIndex,
                visualDiagram = geminiService.diagramForPillar(pillar),
                explanationDe = entity.explanationDe,
                explanationEn = entity.explanationEn,
                trapNote = entity.trapNote,
                isAiGenerated = true
            )
        }
        val combined = dynamicItems + curriculum.getAllQuizItems()
        val filtered = combined.filterNot { it.id in prunedIds }
        return filtered.ifEmpty { curriculum.getAllQuizItems() }
    }

    suspend fun getActiveQuizPool(): List<C1QuizItem> {
        val prunedIds = dao.getPrunedContentOnce().map { it.contentId }.toSet()
        val aiQuizEntities = dao.getAiQuizOnce()
        return buildActiveQuizListSync(aiQuizEntities, prunedIds)
    }

    /**
     * State-of-the-Art 3PL Fisher Information + Upper Confidence Bound (UCB) + Surgical Mistake Item Selector
     */
    fun selectNextAdaptiveItem(
        pool: List<C1QuizItem>,
        state: MasterControlState,
        alreadyAskedIds: Set<String>,
        recentConsecutiveErrors: Int,
        calibrationMode: Boolean,
        coveredPillarsInSession: Set<C1Pillar>,
        pinnedPillar: C1Pillar? = null,
        mistakeRepairMode: Boolean = false
    ): C1QuizItem? {
        val candidates = pool.filterNot { it.id in alreadyAskedIds }.ifEmpty { pool }
        val pillarMap = state.pillarSnapshots.associateBy { it.pillar }
        val activeMistakeSourceIds = state.unresolvedMistakes.map { it.sourceItemId }.toSet()
        val activeMistakeArchetypes = state.unresolvedMistakes.map { it.archetypeId }.toSet()

        val prioritizedCandidates = if (mistakeRepairMode) {
            candidates.filter { it.id in activeMistakeSourceIds }.ifEmpty { candidates }
        } else {
            candidates
        }

        return prioritizedCandidates.maxByOrNull { item ->
            val snap = pillarMap[item.pillar]
            val mastery = snap?.mastery ?: 50f
            val sigma = snap?.confidenceSigma ?: 20f
            val targetTier = snap?.targetDifficultyTier ?: 2

            val theta = PsychometricMathEngine.masteryToTheta(mastery) + state.strategyMode.targetDifficultyBias
            val adjustedTheta = if (recentConsecutiveErrors >= 2 || state.cognitiveLoad == CognitiveLoadLevel.FATIGUED) {
                theta - 0.78f
            } else {
                theta
            }

            val a = PsychometricMathEngine.difficultyToDiscrimination(item.difficulty, item.isAiGenerated)
            val b = PsychometricMathEngine.difficultyToBeta(item.difficulty)
            val fisher3Pl = PsychometricMathEngine.fisherInformation3Pl(adjustedTheta, a, b, 0.22f)

            val tierAlignment = if (item.difficulty == targetTier) 1.38f else 1.0f
            val ucbExplore = 1f + (sigma / 32f)

            val roiMultiplier = when {
                pinnedPillar != null && item.pillar == pinnedPillar -> 3.6f
                pinnedPillar != null -> 0.18f
                calibrationMode && item.pillar !in coveredPillarsInSession -> 3.2f
                snap?.isParetoVital20 == true -> 1.92f
                snap?.statusTag == PillarStatusTag.MASTERED -> 0.30f
                else -> ((snap?.roiScore ?: 25f) / 28f).coerceIn(0.55f, 2.1f)
            }

            val mistakeBoost = when {
                mistakeRepairMode && item.id in activeMistakeSourceIds -> 5.5f
                mistakeRepairMode && item.archetype.id in activeMistakeArchetypes -> 2.8f
                item.id in activeMistakeSourceIds -> 2.6f
                item.archetype == state.dominantErrorArchetype -> 1.48f
                else -> 1.0f
            }

            val aiCustomBoost = if (item.isAiGenerated) 1.50f else 1.0f

            fisher3Pl * tierAlignment * ucbExplore * roiMultiplier * mistakeBoost * aiCustomBoost
        }
    }

    /**
     * Records a quiz response using Bayesian 3PL + Response-Time IRT posterior updating.
     * Returns true if a critical mistake cluster warrants autonomous background AI curriculum mutation.
     */
    suspend fun recordQuizAnswer(
        item: C1QuizItem,
        selectedIndex: Int = item.correctIndex,
        isCorrect: Boolean,
        responseTimeMs: Long,
        markCalibrated: Boolean = false
    ): Boolean {
        ensureInitialized()
        val now = System.currentTimeMillis()
        val currentPillars = dao.getAllPillarMasteryOnce().associateBy { it.pillarId }
        val existing = currentPillars[item.pillar.id] ?: PillarMasteryEntity(
            item.pillar.id, 52f, 22f, 2, 0, 0, 0, 0, now
        )

        val (projectedMastery, projectedSigma) = PsychometricMathEngine.projectTemporalPrior(
            existing.masteryScore,
            existing.confidenceSigma,
            existing.lastPracticedAt,
            now
        )

        val posterior = PsychometricMathEngine.computeBayesian3PlUpdate(
            priorMastery = projectedMastery,
            priorSigma = projectedSigma,
            currentTier = existing.targetDifficultyTier,
            recentStreak = existing.recentStreak,
            itemDifficulty = item.difficulty,
            isAiSurgical = item.isAiGenerated,
            isCorrect = isCorrect,
            responseTimeMs = responseTimeMs,
            guessingFloorC = 0.22f
        )

        val newStreak = if (isCorrect) existing.recentStreak + 1 else 0

        dao.upsertPillarMastery(
            existing.copy(
                masteryScore = posterior.updatedMastery,
                confidenceSigma = posterior.updatedSigma,
                targetDifficultyTier = posterior.updatedTier,
                attemptsCount = existing.attemptsCount + 1,
                correctCount = existing.correctCount + if (isCorrect) 1 else 0,
                recentStreak = newStreak,
                lastPracticedAt = now
            )
        )

        // Precision Mistake Ledger & Automaticity-Gated Auto-Pruning
        val mistakeKey = "quiz_${item.id}"
        val existingMistake = dao.getMistakeByKey(mistakeKey)
        if (!isCorrect) {
            val chosenText = item.options.getOrElse(selectedIndex) { "Incorrect option" }
            val correctText = item.options[item.correctIndex]
            dao.upsertMistake(
                MistakeLedgerEntity(
                    mistakeKey = mistakeKey,
                    sourceItemId = item.id,
                    pillarId = item.pillar.id,
                    archetypeId = item.archetype.id,
                    contextSnippet = item.contextSnippet,
                    wrongChoice = chosenText,
                    correctForm = correctText,
                    explanationDe = item.explanationDe,
                    occurrences = (existingMistake?.occurrences ?: 0) + 1,
                    remediationPasses = 0,
                    isResolved = false,
                    lastSeenAt = now
                )
            )
        } else if (existingMistake != null && !existingMistake.isResolved) {
            val passes = existingMistake.remediationPasses + 1
            // Recurring errors (occurrences >= 2) demand 2 clean passes before full resolution!
            val requiredPasses = if (existingMistake.occurrences >= 2) 2 else 1
            dao.upsertMistake(
                existingMistake.copy(
                    remediationPasses = passes,
                    isResolved = passes >= requiredPasses,
                    lastSeenAt = now
                )
            )
        } else if (
            isCorrect &&
            posterior.automaticity == AutomaticityClassification.AUTOMATIC_C1 &&
            item.difficulty <= 2 &&
            posterior.updatedMastery >= 78f
        ) {
            // Only auto-prune when retrieval is truly automatic (<5.5s), never on slow translated guesses!
            dao.upsertPrunedContent(
                PrunedContentEntity(
                    contentId = item.id,
                    contentType = "QUIZ",
                    pillarId = item.pillar.id,
                    titleLabel = item.contextSnippet.take(48),
                    reason = "Auto-pruned (3PL-RT): Automatic C1 retrieval (${responseTimeMs / 1000}s) at ${posterior.updatedMastery.roundToInt()}% mastery.",
                    minutesSaved = 2,
                    prunedAt = now
                )
            )
        }

        val errorMap = dao.getErrorPatternsOnce().associateBy { it.archetypeId }
        val errEntity = errorMap[item.archetype.id] ?: ErrorPatternEntity(item.archetype.id, 0, 0, 0L)
        dao.upsertErrorPattern(
            if (isCorrect) errEntity.copy(remediationCount = errEntity.remediationCount + 1)
            else errEntity.copy(errorCount = errEntity.errorCount + 1, lastTriggeredAt = now)
        )

        dao.insertSessionLog(
            DiagnosticSessionLogEntity(
                timestamp = now,
                pillarId = item.pillar.id,
                difficulty = item.difficulty,
                isCorrect = isCorrect,
                responseTimeMs = responseTimeMs,
                archetypeId = item.archetype.id
            )
        )

        dao.getControllerConfigOnce()?.let { config ->
            val xpGain = if (isCorrect) 15 + item.difficulty * 5 else 5
            dao.upsertControllerConfig(
                updateDailyActivity(config, xpGain, 1, config.diagnosticCalibrated || markCalibrated)
            )
        }

        // Check if unresolved mistakes in this pillar warrant autonomous AI content synthesis
        val pillarUnresolved = dao.getMistakeLedgerOnce().count { !it.isResolved && it.pillarId == item.pillar.id }
        val existingAiForPillar = dao.getAiQuizOnce().count { it.pillarId == item.pillar.id }
        return !isCorrect && pillarUnresolved >= 2 && existingAiForPillar < 6
    }

    /**
     * Runs an immediate 80/20 Low-ROI Prune Sweep using Bayesian mastery + FSRS-4.5 stability.
     */
    suspend fun runParetoPruneSweep(): Int {
        ensureInitialized()
        val now = System.currentTimeMillis()
        val existingPruned = dao.getPrunedContentOnce().map { it.contentId }.toSet()
        val pillars = dao.getAllPillarMasteryOnce().associateBy { it.pillarId }
        val srsStates = dao.getAllSrsStatesOnce().associateBy { it.cardId }
        val toPrune = mutableListOf<PrunedContentEntity>()

        curriculum.getAllQuizItems().forEach { q ->
            if (q.id !in existingPruned) {
                val pEntity = pillars[q.pillar.id]
                val pMastery = pEntity?.masteryScore ?: 50f
                val pSigma = pEntity?.confidenceSigma ?: 22f
                if ((q.difficulty == 1 && pMastery >= 55f) || (q.difficulty <= 2 && pMastery >= 79f && pSigma <= 16f)) {
                    toPrune.add(
                        PrunedContentEntity(
                            contentId = q.id,
                            contentType = "QUIZ",
                            pillarId = q.pillar.id,
                            titleLabel = q.contextSnippet.take(48),
                            reason = "80/20 Sweep: Low Fisher Information (L${q.difficulty} vs ${pMastery.roundToInt()}% mastery).",
                            minutesSaved = 2,
                            prunedAt = now
                        )
                    )
                }
            }
        }

        curriculum.getAllVocabCards().forEach { card ->
            if (card.id !in existingPruned) {
                val st = srsStates[card.id]
                val r = PsychometricMathEngine.computeFsrsRetrievability(st, now)
                if (st != null && st.stabilityDays >= 14f && st.lapses == 0 && r >= 0.92f) {
                    toPrune.add(
                        PrunedContentEntity(
                            contentId = card.id,
                            contentType = "VOCAB",
                            pillarId = card.pillar.id,
                            titleLabel = card.expression,
                            reason = "FSRS-4.5 Sweep: Stability ${st.stabilityDays.roundToInt()}d (${(r * 100).roundToInt()}% R) with 0 lapses.",
                            minutesSaved = 2,
                            prunedAt = now
                        )
                    )
                }
            }
        }

        if (toPrune.isNotEmpty()) {
            dao.upsertAllPrunedContent(toPrune)
        }
        return toPrune.size
    }

    suspend fun restorePrunedItem(contentId: String) {
        dao.restorePrunedContent(contentId)
    }

    /**
     * Full Closed-Loop Firebase AI Curriculum Orchestration:
     * Feeds Bayesian IRT posteriors, RT-IRT automaticity, FSRS retention, and personal AMG/911/BVB context
     * to recalibrate difficulty tiers, prune low-ROI items, and synthesize tailored visual & quiz content.
     */
    suspend fun orchestrateWithFirebaseAi(currentState: MasterControlState): Result<AiMutationReport> {
        ensureInitialized()
        val now = System.currentTimeMillis()
        val existingPruned = dao.getPrunedContentOnce().map { it.contentId }.toSet()

        val lowRoiCandidates = curriculum.getAllQuizItems()
            .filter { it.difficulty <= 2 && it.id !in existingPruned }
            .take(6)

        val pt = currentState.precisionTelemetry
        val telemetryPayload = buildString {
            appendLine("Learner Context: Automotive Systems M.Eng. (HS Esslingen), Powertrain Intern at Mercedes-Benz, Porsche 911 & AMG enthusiast, BVB 09 fan & SV 1845 Esslingen F-Jugend Co-Trainer.")
            appendLine("Active Context Track: ${currentState.selectedContextTrack.titleDe}")
            appendLine("Functional C1: ${currentState.functionalC1ReadinessPercent}% | Goethe C1 Exam: ${currentState.examReadinessPercent}% (${currentState.cefrLevel})")
            appendLine("Bayesian Precision: ${pt.diagnosticPrecisionPercent}% | Automaticity: ${pt.automaticityIndexPercent}% (Median RT: ${pt.medianResponseSeconds}s, TranslationRisk=${pt.translationRiskDetected})")
            appendLine("FSRS Mean Retrievability: ${pt.meanRetrievabilityPercent}% | Cognitive Load: ${currentState.cognitiveLoad.label}")
            appendLine("Pillars (sorted by Bayesian 80/20 Marginal ROI):")
            currentState.pillarSnapshots.forEach { s ->
                appendLine("- ${s.pillar.id}: mastery=${s.mastery.roundToInt()}%, sigma=±${s.confidenceSigma.roundToInt()}%, currentTier=L${s.targetDifficultyTier}, ROI=${s.roiScore.roundToInt()}, mistakes=${s.unresolvedMistakesCount}")
            }
            appendLine("Active Unresolved Mistakes:")
            if (currentState.unresolvedMistakes.isEmpty()) {
                appendLine("- None logged yet; target #1 highest-ROI weak pillar (${currentState.pillarSnapshots.first().pillar.titleDe}) in ${currentState.selectedContextTrack.shortLabel} context.")
            } else {
                currentState.unresolvedMistakes.take(5).forEach { m ->
                    appendLine("- [${m.pillarId}] Context: '${m.contextSnippet}' | Chose '${m.wrongChoice}' instead of '${m.correctForm}' (seen ${m.occurrences}x)")
                }
            }
            appendLine("LowRoiMasteredCandidates (IDs eligible for pruning):")
            lowRoiCandidates.forEach { c ->
                appendLine("- ${c.id}: [${c.pillar.id} L${c.difficulty}] ${c.contextSnippet}")
            }
        }

        val topRoiPillar = currentState.pillarSnapshots.first().pillar
        val planResult = geminiService.orchestrateCurriculumWithAi(
            telemetryPayload = telemetryPayload,
            fallbackFocusPillar = topRoiPillar,
            dominantArchetype = currentState.dominantErrorArchetype
        )

        return planResult.map { plan ->
            val pillarEntities = dao.getAllPillarMasteryOnce().associateBy { it.pillarId }.toMutableMap()
            plan.difficultyAdjustments.forEach { adj ->
                pillarEntities[adj.pillar.id]?.let { existing ->
                    pillarEntities[adj.pillar.id] = existing.copy(
                        targetDifficultyTier = adj.newDifficultyTier.coerceIn(1, 4)
                    )
                }
            }
            dao.upsertAllPillarMastery(pillarEntities.values.toList())

            dao.getControllerConfigOnce()?.let { cfg ->
                dao.upsertControllerConfig(
                    updateDailyActivity(
                        cfg.copy(strategyModeId = plan.recommendedStrategy.id),
                        xpGain = 25,
                        minutesGain = 1,
                        calibrated = true
                    )
                )
            }

            val validCandidateMap = lowRoiCandidates.associateBy { it.id }
            val prunedEntities = plan.pruneCandidateIds.mapNotNull { id ->
                val item = validCandidateMap[id] ?: return@mapNotNull null
                PrunedContentEntity(
                    contentId = item.id,
                    contentType = "QUIZ",
                    pillarId = item.pillar.id,
                    titleLabel = item.contextSnippet.take(48),
                    reason = "AI 80/20 Optimizer: Pruned low-ROI L${item.difficulty} item to accelerate C1 gains.",
                    minutesSaved = 2,
                    prunedAt = now
                )
            }
            if (prunedEntities.isNotEmpty()) {
                dao.upsertAllPrunedContent(prunedEntities)
            }

            val slide = plan.newVisualSlide
            dao.insertAiSlide(
                AiGeneratedSlideEntity(
                    id = slide.id,
                    pillarId = slide.pillar.id,
                    headline = slide.headline,
                    subheadline = slide.subheadline,
                    diagramTypeName = slide.diagramType.name,
                    formulaBefore = slide.formulaBefore,
                    formulaAfter = slide.formulaAfter,
                    highlightRule = slide.highlightRule,
                    keyInsight = slide.keyInsight,
                    microExampleDe = slide.microExampleDe,
                    microExampleGloss = slide.microExampleGloss,
                    commonTrap = slide.commonTrap,
                    createdAt = now
                )
            )

            val vocab = plan.newVocabCard
            dao.insertAiVocab(
                AiGeneratedVocabEntity(
                    id = vocab.id,
                    pillarId = vocab.pillar.id,
                    expression = vocab.expression,
                    grammarTag = vocab.grammarTag,
                    register = vocab.register,
                    meaningEn = vocab.meaningEn,
                    exampleDe = vocab.exampleDe,
                    trapWarning = vocab.trapWarning,
                    visualHint = vocab.visualHint,
                    diagramTypeName = vocab.diagramType.name,
                    createdAt = now
                )
            )

            val quizEntities = plan.newRemediationProbes.map { q ->
                AiGeneratedQuizEntity(
                    id = q.id,
                    pillarId = q.pillar.id,
                    difficulty = q.difficulty,
                    archetypeId = q.archetype.id,
                    promptDe = q.promptDe,
                    contextSnippet = q.contextSnippet,
                    option0 = q.options[0],
                    option1 = q.options[1],
                    option2 = q.options[2],
                    option3 = q.options[3],
                    correctIndex = q.correctIndex,
                    explanationDe = q.explanationDe,
                    explanationEn = q.explanationEn,
                    trapNote = q.trapNote,
                    createdAt = now
                )
            }
            if (quizEntities.isNotEmpty()) {
                dao.insertAllAiQuiz(quizEntities)
            }

            AiMutationReport(
                executiveSummary = plan.executiveSummary,
                paretoInsight = plan.paretoInsight,
                nextFocusPillar = plan.nextFocusPillar,
                adjustedPillarsCount = plan.difficultyAdjustments.size,
                addedSlideHeadline = slide.headline,
                addedVocabExpression = vocab.expression,
                addedProbesCount = quizEntities.size,
                prunedItemsCount = prunedEntities.size
            )
        }
    }

    suspend fun recordVisualSlideProgress(pillar: C1Pillar, slideIndex: Int, checkpointCorrect: Boolean?) {
        ensureInitialized()
        val now = System.currentTimeMillis()
        val existing = dao.getAllPillarMasteryOnce().find { it.pillarId == pillar.id } ?: return
        val completedCount = max(existing.visualSlidesCompleted, slideIndex)
        val bonus = when (checkpointCorrect) {
            true -> 3.5f
            false -> -1.0f
            null -> 1.2f
        }
        val newMastery = (existing.masteryScore + bonus).coerceIn(15f, 99f)
        val newSigma = (existing.confidenceSigma * 0.95f).coerceIn(5f, 26f)
        dao.upsertPillarMastery(
            existing.copy(
                masteryScore = newMastery,
                confidenceSigma = newSigma,
                targetDifficultyTier = PsychometricMathEngine.computeOptimalDifficultyTier(newMastery, newSigma),
                visualSlidesCompleted = completedCount,
                lastPracticedAt = now
            )
        )
        dao.getControllerConfigOnce()?.let { config ->
            dao.upsertControllerConfig(updateDailyActivity(config, 10, 1, config.diagnosticCalibrated))
        }
    }

    suspend fun recordSrsReview(card: C1VocabCard, quality: Int) {
        ensureInitialized()
        val now = System.currentTimeMillis()
        val prev = dao.getSrsState(card.id)
        val schedule = PsychometricMathEngine.computeFsrsNextState(prev, quality, now)

        dao.upsertSrsState(
            SrsCardStateEntity(
                cardId = card.id,
                stabilityDays = schedule.stabilityDays,
                difficulty = schedule.difficulty,
                repetitions = (prev?.repetitions ?: 0) + 1,
                lapses = schedule.lapses,
                nextReviewAt = now + (schedule.optimalIntervalDays * 86_400_000f).toLong(),
                lastQuality = quality
            )
        )

        // Cross-modality coupling: if a C1 collocation lapses (q == 1), log it for active structural repair!
        val vocabMistakeKey = "vocab_${card.id}"
        if (quality == 1) {
            val existingMistake = dao.getMistakeByKey(vocabMistakeKey)
            dao.upsertMistake(
                MistakeLedgerEntity(
                    mistakeKey = vocabMistakeKey,
                    sourceItemId = card.id,
                    pillarId = card.pillar.id,
                    archetypeId = ErrorArchetype.REGISTER_COLLOCATION.id,
                    contextSnippet = "FSRS-Lapse (${card.grammarTag})",
                    wrongChoice = card.meaningEn,
                    correctForm = card.expression,
                    explanationDe = card.trapWarning,
                    occurrences = (existingMistake?.occurrences ?: 0) + 1,
                    remediationPasses = 0,
                    isResolved = false,
                    lastSeenAt = now
                )
            )
        } else if (quality >= 3) {
            dao.getMistakeByKey(vocabMistakeKey)?.takeIf { !it.isResolved }?.let { m ->
                dao.upsertMistake(m.copy(remediationPasses = m.remediationPasses + 1, isResolved = true, lastSeenAt = now))
            }
        }

        if (quality == 4 && schedule.stabilityDays >= 15f && schedule.lapses == 0) {
            dao.upsertPrunedContent(
                PrunedContentEntity(
                    contentId = card.id,
                    contentType = "VOCAB",
                    pillarId = card.pillar.id,
                    titleLabel = card.expression,
                    reason = "Auto-pruned (FSRS-4.5): Effortless recall with ${schedule.stabilityDays.roundToInt()}d stability.",
                    minutesSaved = 2,
                    prunedAt = now
                )
            )
        }

        dao.getAllPillarMasteryOnce().find { it.pillarId == card.pillar.id }?.let { entity ->
            val delta = when (quality) { 1 -> -2.4f; 2 -> 0.8f; 3 -> 2.1f; else -> 3.2f }
            val updated = (entity.masteryScore + delta).coerceIn(15f, 99f)
            val newSigma = (entity.confidenceSigma * 0.96f).coerceIn(5f, 26f)
            dao.upsertPillarMastery(
                entity.copy(
                    masteryScore = updated,
                    confidenceSigma = newSigma,
                    targetDifficultyTier = PsychometricMathEngine.computeOptimalDifficultyTier(updated, newSigma),
                    lastPracticedAt = now
                )
            )
        }

        dao.getControllerConfigOnce()?.let { config ->
            dao.upsertControllerConfig(updateDailyActivity(config, quality * 4, 1, config.diagnosticCalibrated))
        }
    }

    suspend fun recordWritingDiagnostic(result: WritingDiagnosticResult) {
        ensureInitialized()
        val now = System.currentTimeMillis()
        val errorMap = dao.getErrorPatternsOnce().associateBy { it.archetypeId }
        result.detectedArchetypes.forEach { arch ->
            val current = errorMap[arch.id] ?: ErrorPatternEntity(arch.id, 0, 0, 0L)
            dao.upsertErrorPattern(current.copy(errorCount = current.errorCount + 1, lastTriggeredAt = now))
        }
        result.transformations.take(2).forEachIndexed { idx, t ->
            val key = "writing_${t.originalPhrase.hashCode()}_$idx"
            dao.upsertMistake(
                MistakeLedgerEntity(
                    mistakeKey = key,
                    sourceItemId = "q_rhet_1",
                    pillarId = C1Pillar.RHETORIK.id,
                    archetypeId = result.detectedArchetypes.firstOrNull()?.id ?: ErrorArchetype.REGISTER_COLLOCATION.id,
                    contextSnippet = "Draft Stil-Upgrade (${t.ruleTag})",
                    wrongChoice = t.originalPhrase,
                    correctForm = t.upgradedPhrase,
                    explanationDe = t.explanation,
                    occurrences = 1,
                    remediationPasses = 0,
                    isResolved = false,
                    lastSeenAt = now
                )
            )
        }
        dao.getAllPillarMasteryOnce()
            .filter { it.pillarId == C1Pillar.RHETORIK.id || it.pillarId == C1Pillar.NOMEN_VERB.id }
            .forEach { entity ->
                val boost = ((result.c1Score - entity.masteryScore) * 0.18f).coerceIn(1f, 6f)
                val updated = (entity.masteryScore + boost).coerceIn(15f, 99f)
                dao.upsertPillarMastery(
                    entity.copy(
                        masteryScore = updated,
                        targetDifficultyTier = PsychometricMathEngine.computeOptimalDifficultyTier(updated, entity.confidenceSigma),
                        lastPracticedAt = now
                    )
                )
            }
        dao.getControllerConfigOnce()?.let { config ->
            dao.upsertControllerConfig(updateDailyActivity(config, 35, 3, config.diagnosticCalibrated))
        }
    }

    suspend fun updateContextTrack(track: ContextTrack) {
        ensureInitialized()
        dao.getControllerConfigOnce()?.let {
            dao.upsertControllerConfig(it.copy(selectedContextTrackId = track.id))
        }
    }

    suspend fun recordRepoUnitStep(
        unitId: String,
        stepType: String,
        scoreBoost: Float = 8f
    ) {
        ensureInitialized()
        val now = System.currentTimeMillis()
        val repoUnit = curriculum.getRepoUnitById(unitId) ?: return
        val existing = dao.getRepoUnitProgressById(unitId)
        val currentScore = existing?.masteryScore ?: 48f
        val updated = RepoUnitProgressEntity(
            unitId = unitId,
            masteryScore = (currentScore + scoreBoost).coerceIn(20f, 99f),
            visualDone = (existing?.visualDone == true) || stepType == "VISUAL",
            puzzleDone = (existing?.puzzleDone == true) || stepType == "PUZZLE",
            bookDone = (existing?.bookDone == true) || stepType == "BOOK",
            situationDone = (existing?.situationDone == true) || stepType == "SITUATION",
            lastPracticedAt = now
        )
        dao.upsertRepoUnitProgress(updated)

        dao.getAllPillarMasteryOnce().find { it.pillarId == repoUnit.mappedPillar.id }?.let { p ->
            val newMastery = (p.masteryScore + scoreBoost * 0.35f).coerceIn(15f, 99f)
            val newSigma = (p.confidenceSigma * 0.94f).coerceIn(5f, 26f)
            dao.upsertPillarMastery(
                p.copy(
                    masteryScore = newMastery,
                    confidenceSigma = newSigma,
                    targetDifficultyTier = PsychometricMathEngine.computeOptimalDifficultyTier(newMastery, newSigma),
                    visualSlidesCompleted = p.visualSlidesCompleted + 1,
                    lastPracticedAt = now
                )
            )
        }
        dao.getControllerConfigOnce()?.let { cfg ->
            dao.upsertControllerConfig(updateDailyActivity(cfg, 18, 2, cfg.diagnosticCalibrated))
        }
    }

    suspend fun recordSituationEvaluation(
        scenarioTitle: String,
        userReply: String,
        feedback: SituationAiFeedback,
        pillar: C1Pillar = C1Pillar.MODALPARTIKELN
    ) {
        ensureInitialized()
        val now = System.currentTimeMillis()
        if (feedback.scorePercent < 78) {
            dao.upsertMistake(
                MistakeLedgerEntity(
                    mistakeKey = "sit_${scenarioTitle.hashCode()}",
                    sourceItemId = "uquiz_s3-negotiate",
                    pillarId = pillar.id,
                    archetypeId = ErrorArchetype.REGISTER_COLLOCATION.id,
                    contextSnippet = "Situation: $scenarioTitle",
                    wrongChoice = userReply.take(80),
                    correctForm = feedback.upgradedResponseDe.take(110),
                    explanationDe = feedback.keyTakeawayDe,
                    occurrences = 1,
                    remediationPasses = 0,
                    isResolved = false,
                    lastSeenAt = now
                )
            )
        }
        dao.getAllPillarMasteryOnce().find { it.pillarId == pillar.id }?.let { p ->
            val delta = ((feedback.scorePercent - p.masteryScore) * 0.18f).coerceIn(-2.5f, 6.5f)
            val newMastery = (p.masteryScore + delta).coerceIn(15f, 99f)
            val newSigma = (p.confidenceSigma * 0.92f).coerceIn(5f, 26f)
            dao.upsertPillarMastery(
                p.copy(
                    masteryScore = newMastery,
                    confidenceSigma = newSigma,
                    targetDifficultyTier = PsychometricMathEngine.computeOptimalDifficultyTier(newMastery, newSigma),
                    lastPracticedAt = now
                )
            )
        }
        dao.getControllerConfigOnce()?.let { cfg ->
            dao.upsertControllerConfig(updateDailyActivity(cfg, 30, 3, cfg.diagnosticCalibrated))
        }
    }

    suspend fun recordPersonalizedBeat(
        exerciseId: String,
        beatType: String
    ) {
        ensureInitialized()
        val now = System.currentTimeMillis()
        val ex = curriculum.getPersonalizedExerciseById(exerciseId) ?: return
        val existing = dao.getRepoUnitProgressById(exerciseId)
        val currentScore = existing?.masteryScore ?: 50f
        dao.upsertRepoUnitProgress(
            RepoUnitProgressEntity(
                unitId = exerciseId,
                masteryScore = (currentScore + 12f).coerceIn(25f, 99f),
                visualDone = (existing?.visualDone == true) || beatType == "BLIND_LISTEN",
                situationDone = (existing?.situationDone == true) || beatType == "SPONTANEOUS_SPEAK",
                puzzleDone = (existing?.puzzleDone == true) || beatType == "TRANSFER",
                bookDone = existing?.bookDone == true,
                lastPracticedAt = now
            )
        )
        dao.getAllPillarMasteryOnce().find { it.pillarId == ex.mappedPillar.id }?.let { p ->
            val newMastery = (p.masteryScore + 2.8f).coerceIn(15f, 99f)
            val newSigma = (p.confidenceSigma * 0.94f).coerceIn(5f, 26f)
            dao.upsertPillarMastery(
                p.copy(
                    masteryScore = newMastery,
                    confidenceSigma = newSigma,
                    targetDifficultyTier = PsychometricMathEngine.computeOptimalDifficultyTier(newMastery, newSigma),
                    lastPracticedAt = now
                )
            )
        }
        dao.getControllerConfigOnce()?.let { cfg ->
            dao.upsertControllerConfig(updateDailyActivity(cfg, 22, 2, cfg.diagnosticCalibrated))
        }
    }

    suspend fun recordSpontaneousCoachingEval(
        exercise: PersonalizedExercise,
        userReply: String,
        eval: SpontaneousCoachingEvaluation
    ) {
        recordPersonalizedBeat(exercise.id, "SPONTANEOUS_SPEAK")
        val now = System.currentTimeMillis()
        if (eval.naturalnessPercent < 82) {
            val mappedArchetype = when (eval.primaryErrorCategory) {
                PersonalizationErrorCategory11.GRAMMAR_UNDER_PRESSURE,
                PersonalizationErrorCategory11.SENTENCE_FORMULATION -> ErrorArchetype.WORD_ORDER_KLAMMER
                PersonalizationErrorCategory11.INTERPRETATION -> ErrorArchetype.MOOD_MODALITY
                else -> ErrorArchetype.REGISTER_COLLOCATION
            }
            dao.upsertMistake(
                MistakeLedgerEntity(
                    mistakeKey = "pf_${exercise.id}_${eval.primaryErrorCategory.id}",
                    sourceItemId = "uquiz_s5-spontaneous",
                    pillarId = exercise.mappedPillar.id,
                    archetypeId = mappedArchetype.id,
                    contextSnippet = "${eval.primaryErrorCategory.id}: ${exercise.title}",
                    wrongChoice = userReply.take(85),
                    correctForm = (eval.naturalAlternatives.firstOrNull() ?: exercise.delayedRetrievalTarget).take(115),
                    explanationDe = "${eval.primaryErrorCategory.labelDe} — ${eval.targetedRepetitionPromptDe}",
                    occurrences = 1,
                    remediationPasses = 0,
                    isResolved = false,
                    lastSeenAt = now
                )
            )
        }
    }

    suspend fun recordMediaMissionCompleted(missionId: String, quizCorrect: Boolean) {
        ensureInitialized()
        val now = System.currentTimeMillis()
        val mission = curriculum.getMediaImmersionMissionById(missionId)
        val pillar = mission?.mappedPillar ?: C1Pillar.MODALPARTIKELN

        dao.upsertRepoUnitProgress(
            RepoUnitProgressEntity(
                unitId = "media_$missionId",
                masteryScore = if (quizCorrect) 84f else 62f,
                visualDone = true,
                puzzleDone = quizCorrect,
                bookDone = true,
                situationDone = true,
                lastPracticedAt = now
            )
        )

        dao.getAllPillarMasteryOnce().find { it.pillarId == pillar.id }?.let { p ->
            val delta = if (quizCorrect) 3.2f else -0.8f
            val updated = (p.masteryScore + delta).coerceIn(15f, 99f)
            val newSigma = (p.confidenceSigma * 0.93f).coerceIn(5f, 26f)
            dao.upsertPillarMastery(
                p.copy(
                    masteryScore = updated,
                    confidenceSigma = newSigma,
                    targetDifficultyTier = PsychometricMathEngine.computeOptimalDifficultyTier(updated, newSigma),
                    lastPracticedAt = now
                )
            )
        }

        dao.getControllerConfigOnce()?.let { cfg ->
            dao.upsertControllerConfig(
                updateDailyActivity(cfg, xpGain = if (quizCorrect) 28 else 16, minutesGain = 4, calibrated = true)
            )
        }
    }

    /**
     * State-of-the-Art Active Post-Session Adaptation:
     * Runs immediately when the user finishes a Daily Stint (no initial assessment needed):
     * 1) Computes empirical accuracy & RT latency across recent attempts
     * 2) Upgrades/stabilizes pillar target difficulty tiers based on automaticity
     * 3) Shifts ControllerStrategyMode if mistakes cluster or exam-sprint threshold is reached
     * 4) Sweeps mastered low-ROI items & computes tomorrow's optimal next-stint preview
     */
    suspend fun concludeDailyStintAndAdapt(
        completedSectorsCount: Int,
        nextStintOffset: Int
    ): PostSessionAdaptationSummary {
        ensureInitialized()
        val now = System.currentTimeMillis()
        val recentCutoff = now - 2_700_000L // last 45 min
        val allLogs = dao.getRecentLogsOnce()
        val stintLogs = allLogs.filter { it.timestamp >= recentCutoff }.ifEmpty { allLogs.take(12) }

        val sessionAcc = if (stintLogs.isNotEmpty()) {
            ((stintLogs.count { it.isCorrect }.toFloat() / stintLogs.size) * 100f).roundToInt()
        } else {
            80
        }

        val sortedRt = stintLogs.map { it.responseTimeMs }.sorted()
        val medianMs = if (sortedRt.isNotEmpty()) sortedRt[sortedRt.size / 2] else 5400L
        val medianSec = ((medianMs / 100f).roundToInt() / 10f).coerceIn(1.0f, 35.0f)

        val logsByPillar = stintLogs.groupBy { it.pillarId }
        val pillarEntities = dao.getAllPillarMasteryOnce().associateBy { it.pillarId }.toMutableMap()
        val adjustmentNotes = mutableListOf<String>()

        logsByPillar.forEach { (pillarId, pLogs) ->
            val entity = pillarEntities[pillarId] ?: return@forEach
            val pAcc = (pLogs.count { it.isCorrect }.toFloat() / pLogs.size) * 100f
            val pMedRt = pLogs.map { it.responseTimeMs }.sorted().let { it[it.size / 2] }
            val oldTier = entity.targetDifficultyTier
            val newTier = when {
                pAcc >= 85f && pMedRt < 6_500L -> (oldTier + 1).coerceAtMost(4)
                pAcc < 58f || pMedRt > 12_500L -> (oldTier - 1).coerceAtLeast(1)
                else -> oldTier
            }
            if (newTier != oldTier) {
                pillarEntities[pillarId] = entity.copy(targetDifficultyTier = newTier, lastPracticedAt = now)
                val short = C1Pillar.entries.find { it.id == pillarId }?.shortLabel ?: pillarId
                adjustmentNotes += if (newTier > oldTier) "$short ↑ L$newTier" else "$short ↓ L$newTier"
            }
        }
        if (adjustmentNotes.isNotEmpty()) {
            dao.upsertAllPillarMastery(pillarEntities.values.toList())
        }

        val unresolvedCount = dao.getMistakeLedgerOnce().count { !it.isResolved }
        val overallAvg = pillarEntities.values.map { it.masteryScore }.average().toFloat()
        val oldConfig = dao.getControllerConfigOnce()
        val prevMode = ControllerStrategyMode.entries.find { it.id == oldConfig?.strategyModeId }
            ?: ControllerStrategyMode.PARETO_80_20

        val nextMode = when {
            unresolvedCount >= 2 || sessionAcc < 62 -> ControllerStrategyMode.INTENSIVE_REPAIR
            overallAvg >= 72f && sessionAcc >= 80 && unresolvedCount == 0 -> ControllerStrategyMode.EXAM_SPRINT
            else -> ControllerStrategyMode.PARETO_80_20
        }

        oldConfig?.let { cfg ->
            dao.upsertControllerConfig(
                updateDailyActivity(
                    cfg.copy(strategyModeId = nextMode.id),
                    xpGain = 35,
                    minutesGain = 2,
                    calibrated = true
                )
            )
        }

        val prunedDelta = runParetoPruneSweep()

        val nextControlState = buildMasterControlState(
            pillars = dao.getAllPillarMasteryOnce(),
            errorPatterns = dao.getErrorPatternsOnce(),
            srsStates = dao.getAllSrsStatesOnce(),
            recentLogs = dao.getRecentLogsOnce(),
            configEntity = dao.getControllerConfigOnce(),
            mistakes = dao.getMistakeLedgerOnce(),
            prunedItems = dao.getPrunedContentOnce(),
            aiSlides = dao.getAiSlidesOnce(),
            aiVocab = dao.getAiVocabOnce(),
            aiQuiz = dao.getAiQuizOnce(),
            unitProgress = dao.getRepoUnitProgressOnce(),
            stintOffset = nextStintOffset
        )

        val focusPillar = nextControlState.pillarSnapshots.firstOrNull()?.pillar?.shortLabel ?: "Nominalisierung"
        val rationaleDe = buildString {
            append("$completedSectorsCount Sektoren ausgewertet ($sessionAcc% Präzision, ${medianSec}s Median-Reaktion). ")
            if (nextMode != prevMode) {
                append("Strategiewechsel: ${prevMode.title} → ${nextMode.title}. ")
            }
            if (medianSec > 12.5f) {
                append("Übersetzungslatenz erkannt → nächste Stint-Audio-Phase auf schnelles Schwäbisch/Umgangssprache & kurze Chunks verschärft. ")
            } else if (sessionAcc >= 84) {
                append("Starke C1-Abrufgeschwindigkeit → Schwierigkeit für $focusPillar hochskaliert. ")
            }
            if (unresolvedCount > 0) {
                append("$unresolvedCount offene Fehler werden morgen im Sektor 1 gezielt repariert.")
            } else {
                append("Nächster Tag priorisiert $focusPillar + frischen Fach-/Alltags-Podcast.")
            }
        }

        return PostSessionAdaptationSummary(
            completedSectorsCount = completedSectorsCount,
            sessionAccuracyPercent = sessionAcc,
            medianLatencySeconds = medianSec,
            strategyModeAfter = nextMode,
            strategyChanged = nextMode != prevMode,
            adjustedPillarsSummary = adjustmentNotes.ifEmpty { listOf("$focusPillar (Optimal-Tier gehalten)") },
            prunedLowRoiCount = prunedDelta,
            primaryAdaptationReasonDe = rationaleDe,
            nextStintPreviewTitles = nextControlState.dailySessionQueue.take(4).map { "${it.beatType.shortSectorLabel}: ${it.title}" },
            aiOptimizationTriggered = true
        )
    }

    suspend fun updateStrategyMode(mode: ControllerStrategyMode) {
        ensureInitialized()
        dao.getControllerConfigOnce()?.let { dao.upsertControllerConfig(it.copy(strategyModeId = mode.id)) }
    }

    suspend fun updateDailyGoal(minutes: Int) {
        ensureInitialized()
        dao.getControllerConfigOnce()?.let { dao.upsertControllerConfig(it.copy(dailyGoalMinutes = minutes.coerceIn(5, 60))) }
    }

    suspend fun resetDiagnosticTelemetry() {
        dao.clearSessionLogs()
        dao.clearSrsStates()
        dao.clearMistakes()
        dao.clearPrunedContent()
        dao.clearRepoUnitProgress()
        val now = System.currentTimeMillis()
        val seededPillars = C1Pillar.entries.map { pillar ->
            val (baseScore, initSigma) = when (pillar) {
                C1Pillar.NOMINALISIERUNG -> 48f to 20f
                C1Pillar.SATZGEFUEGE -> 45f to 18f
                C1Pillar.MODALPARTIKELN -> 46f to 18f
                C1Pillar.RHETORIK -> 49f to 17f
                C1Pillar.KONJUNKTIV -> 54f to 16f
                C1Pillar.NOMEN_VERB -> 56f to 15f
            }
            PillarMasteryEntity(
                pillarId = pillar.id,
                masteryScore = baseScore,
                confidenceSigma = initSigma,
                targetDifficultyTier = PsychometricMathEngine.computeOptimalDifficultyTier(baseScore, initSigma),
                attemptsCount = 0,
                correctCount = 0,
                recentStreak = 0,
                visualSlidesCompleted = 0,
                lastPracticedAt = now
            )
        }
        dao.upsertAllPillarMastery(seededPillars)
        dao.upsertAllErrorPatterns(ErrorArchetype.entries.map { ErrorPatternEntity(it.id, 0, 0, 0L) })
        dao.getControllerConfigOnce()?.let {
            dao.upsertControllerConfig(it.copy(diagnosticCalibrated = true, minutesToday = 0))
        }
    }

    private fun buildActiveVocabList(
        aiVocab: List<AiGeneratedVocabEntity>,
        prunedIds: Set<String>
    ): List<C1VocabCard> {
        val dynamic = aiVocab.mapNotNull { entity ->
            val pillar = C1Pillar.entries.find { it.id == entity.pillarId } ?: return@mapNotNull null
            val diagram = runCatching { VisualDiagramType.valueOf(entity.diagramTypeName) }
                .getOrElse { geminiService.diagramForPillar(pillar) }
            C1VocabCard(
                id = entity.id,
                pillar = pillar,
                expression = entity.expression,
                grammarTag = entity.grammarTag,
                register = entity.register,
                meaningEn = entity.meaningEn,
                exampleDe = entity.exampleDe,
                trapWarning = entity.trapWarning,
                visualHint = entity.visualHint,
                diagramType = diagram,
                isAiGenerated = true
            )
        }
        val active = (dynamic + curriculum.getAllVocabCards()).filterNot { it.id in prunedIds }
        return active.ifEmpty { curriculum.getAllVocabCards() }
    }

    private fun buildEnrichedModule(
        pillar: C1Pillar,
        aiSlides: List<AiGeneratedSlideEntity>,
        prunedIds: Set<String>
    ): VisualLessonModule {
        val base = curriculum.getModuleForPillar(pillar)
        val keptBaseSlides = base.slides.filterNot { it.id in prunedIds }.ifEmpty { base.slides }
        val convertedAi = aiSlides.mapIndexed { idx, entity ->
            val diagram = runCatching { VisualDiagramType.valueOf(entity.diagramTypeName) }
                .getOrElse { base.keyVisualDiagram }
            VisualSlide(
                id = entity.id,
                pillar = pillar,
                slideIndex = keptBaseSlides.size + idx + 1,
                totalSlides = keptBaseSlides.size + aiSlides.size,
                headline = entity.headline,
                subheadline = entity.subheadline,
                diagramType = diagram,
                formulaBefore = entity.formulaBefore,
                formulaAfter = entity.formulaAfter,
                highlightRule = entity.highlightRule,
                keyInsight = entity.keyInsight,
                microExampleDe = entity.microExampleDe,
                microExampleGloss = entity.microExampleGloss,
                commonTrap = entity.commonTrap,
                checkpoint = null,
                isAiSynthesized = true
            )
        }
        val allSlides = (keptBaseSlides + convertedAi).mapIndexed { i, sl ->
            sl.copy(slideIndex = i + 1, totalSlides = keptBaseSlides.size + convertedAi.size)
        }
        return base.copy(slides = allSlides)
    }

    private fun synthesizeDirective(
        calibrated: Boolean,
        snapshots: List<PillarDiagnosticSnapshot>,
        orderedModules: List<PrioritizedModule>,
        topRepoUnit: PrioritizedRepoUnit?,
        topPersonalized: PrioritizedPersonalizedExercise?,
        selectedTrack: ContextTrack,
        unresolvedMistakes: List<MistakeLedgerEntity>,
        dominantError: ErrorArchetype?,
        cognitiveLoad: CognitiveLoadLevel,
        strategyMode: ControllerStrategyMode,
        dueCount: Int,
        overallIndex: Float
    ): ControllerDirective {
        val topVital = snapshots.first()
        val targetModId = orderedModules.firstOrNull { it.module.pillar == topVital.pillar }?.module?.id
        val roiTag = "ROI +${topVital.roiScore.roundToInt()} pts · Tier L${topVital.targetDifficultyTier}"

        if (unresolvedMistakes.size >= 2) {
            val topMistakePillar = C1Pillar.entries.find { it.id == unresolvedMistakes.first().pillarId } ?: topVital.pillar
            return ControllerDirective(
                actionType = DirectiveActionType.FIX_CRITICAL_MISTAKES,
                headline = "Surgical Repair: ${unresolvedMistakes.size} Active Mistakes",
                rationale = "Fixing logged errors (${dominantError?.shortCode ?: "Register/Kasus"}) delivers 3.4× higher C1 ROI than passive reading.",
                badgeText = "MISTAKE REPAIR",
                roiBadgeText = "3.4× EXAM ROI",
                targetPillar = topMistakePillar,
                targetModuleId = targetModId,
                targetRepoUnitId = topRepoUnit?.unit?.id,
                estimatedMinutes = 3
            )
        }

        if (topRepoUnit != null && topRepoUnit.completedStepsCount < 3) {
            val bookBadge = topRepoUnit.unit.bookBridge?.shortBookBadge ?: "Stufe ${topRepoUnit.unit.stage}"
            val ctxGoal = topRepoUnit.unit.contexts[selectedTrack].orEmpty()
            return ControllerDirective(
                actionType = DirectiveActionType.LAUNCH_REPO_UNIT,
                headline = topRepoUnit.unit.title,
                rationale = "[$bookBadge · ${selectedTrack.shortLabel}] $ctxGoal",
                badgeText = "80/20 VITAL UNIT",
                roiBadgeText = roiTag,
                targetPillar = topRepoUnit.unit.mappedPillar,
                targetModuleId = targetModId,
                targetRepoUnitId = topRepoUnit.unit.id,
                targetPersonalizedId = topPersonalized?.exercise?.id,
                estimatedMinutes = 5
            )
        }

        if (topPersonalized != null && (!topPersonalized.blindListeningDone || !topPersonalized.spontaneousSpeakingDone)) {
            return ControllerDirective(
                actionType = DirectiveActionType.LAUNCH_PERSONALIZED_LAB,
                headline = topPersonalized.exercise.title,
                rationale = topPersonalized.exercise.learningObjective,
                badgeText = "VISUAL C1 LAB · ${topPersonalized.exercise.difficulty}",
                roiBadgeText = "BLIND AUDIO + SPONTAN",
                targetPillar = topPersonalized.exercise.mappedPillar,
                targetModuleId = targetModId,
                targetRepoUnitId = topRepoUnit?.unit?.id,
                targetPersonalizedId = topPersonalized.exercise.id,
                estimatedMinutes = 5
            )
        }

        if (strategyMode == ControllerStrategyMode.EXAM_SPRINT || overallIndex >= 68f) {
            return ControllerDirective(
                actionType = DirectiveActionType.LAUNCH_SITUATIONS_SIMULATOR,
                headline = "Situations-Training & Gegenwind",
                rationale = "Train spontaneous C1 reactions under unexpected interruptions (${selectedTrack.shortLabel}).",
                badgeText = "LIVE SITUATION",
                roiBadgeText = roiTag,
                targetPillar = C1Pillar.MODALPARTIKELN,
                targetModuleId = targetModId,
                targetRepoUnitId = topRepoUnit?.unit?.id,
                estimatedMinutes = 5
            )
        }

        return ControllerDirective(
            actionType = DirectiveActionType.LAUNCH_TARGETED_DRILL,
            headline = "80/20 High-ROI Drill: ${topVital.pillar.shortLabel}",
            rationale = "Calibrated to Tier L${topVital.targetDifficultyTier}/4 (${topVital.roiSharePercent}% of total C1 upside).",
            badgeText = "PARETO CAT",
            roiBadgeText = roiTag,
            targetPillar = topVital.pillar,
            targetModuleId = targetModId,
            targetRepoUnitId = topRepoUnit?.unit?.id,
            estimatedMinutes = 4
        )
    }

    private fun updateDailyActivity(
        config: ControllerConfigEntity,
        xpGain: Int,
        minutesGain: Int,
        calibrated: Boolean
    ): ControllerConfigEntity {
        val today = currentEpochDay()
        val newStreak = when {
            config.lastActiveEpochDay == today -> config.streakDays
            config.lastActiveEpochDay == today - 1 -> config.streakDays + 1
            else -> 1
        }
        val newMinutes = if (config.lastActiveEpochDay == today) config.minutesToday + minutesGain else minutesGain
        return config.copy(
            minutesToday = newMinutes,
            streakDays = newStreak,
            totalXp = config.totalXp + xpGain,
            lastActiveEpochDay = today,
            diagnosticCalibrated = calibrated
        )
    }

    private fun currentEpochDay(): Long = System.currentTimeMillis() / 86_400_000L
}
