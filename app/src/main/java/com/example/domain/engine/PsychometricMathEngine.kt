package com.example.domain.engine

import com.example.data.local.DiagnosticSessionLogEntity
import com.example.data.local.SrsCardStateEntity
import com.example.domain.model.CognitiveLoadLevel
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

enum class AutomaticityClassification(val labelDe: String) {
    AUTOMATIC_C1("C1-Automatik (<6s)"),
    CONTROLLED_FLUENT("Kontrolliert-Flüssig"),
    TRANSLATION_LATENCY("Übersetzungs-Latenz (>13s)"),
    IMPULSIVE_ERROR("Impulsiv-Fehler (<1.8s)")
}

data class Bayesian3PlPosterior(
    val updatedMastery: Float,
    val updatedSigma: Float,
    val updatedTier: Int,
    val fisherInformation: Float,
    val surpriseResidual: Float,
    val automaticity: AutomaticityClassification
)

data class FsrsNextSchedule(
    val stabilityDays: Float,
    val difficulty: Float,
    val retrievabilityNow: Float,
    val optimalIntervalDays: Float,
    val lapses: Int
)

/**
 * State-of-the-Art Psychometric & Memory Engine:
 * 1. Kalman-Filtered Temporal Decay + 3-Parameter Logistic (3PL) Item Response Theory
 *    coupled with Hierarchical Response-Time (RT-IRT) Automaticity Scoring.
 * 2. FSRS-4.5 Power-Law Forgetting Curve R(t, S) & Desirable-Difficulty Scheduling.
 * 3. Drift-Diffusion Cognitive Load & Translation-Latency Detection.
 */
object PsychometricMathEngine {

    private const val FSRS_FACTOR = 19f / 81f
    private const val FSRS_DECAY = -0.5f
    private const val TARGET_RETENTION = 0.90f

    /**
     * Applies Kalman process noise (uncertainty inflation over unpracticed days) and
     * gentle Ebbinghaus drift toward baseline when a pillar sits idle > 3 days.
     */
    fun projectTemporalPrior(
        storedMastery: Float,
        storedSigma: Float,
        lastPracticedAtMs: Long,
        nowMs: Long
    ): Pair<Float, Float> {
        val elapsedDays = ((nowMs - lastPracticedAtMs).coerceAtLeast(0L) / 86_400_000f).coerceAtMost(30f)
        // Variance inflation: sigma_t = sqrt(sigma_0^2 + q * dt)
        val processVariance = 1.65f * elapsedDays
        val inflatedSigma = sqrt(storedSigma * storedSigma + processVariance).coerceIn(4.5f, 26.5f)

        // Gentle skill decay only after 3 days of zero practice
        val decayDays = (elapsedDays - 3f).coerceAtLeast(0f)
        val retentionFactor = exp(-0.012f * decayDays)
        val anchor = 48f
        val projectedMastery = (anchor + (storedMastery - anchor) * retentionFactor).coerceIn(15f, 99f)
        return projectedMastery to inflatedSigma
    }

    fun difficultyToBeta(difficultyTier: Int): Float = when (difficultyTier.coerceIn(1, 4)) {
        1 -> -1.15f
        2 -> -0.25f
        3 -> 0.60f
        else -> 1.40f
    }

    fun difficultyToDiscrimination(difficultyTier: Int, isAiSurgical: Boolean): Float {
        val baseA = when (difficultyTier.coerceIn(1, 4)) {
            1 -> 1.15f
            2 -> 1.30f
            3 -> 1.48f
            else -> 1.62f
        }
        return if (isAiSurgical) baseA * 1.10f else baseA
    }

    fun masteryToTheta(mastery: Float): Float = (mastery - 50f) / 22f

    fun thetaToMastery(theta: Float): Float = (50f + theta * 22f).coerceIn(15f, 99f)

    /**
     * 3PL Item Characteristic Curve: P(X=1 | theta, a, b, c)
     */
    fun prob3Pl(theta: Float, a: Float, b: Float, c: Float = 0.22f): Float {
        val logistic = 1f / (1f + exp(-a * (theta - b)))
        return (c + (1f - c) * logistic).coerceIn(0.02f, 0.995f)
    }

    /**
     * Exact 3PL Fisher Information I(theta) measuring diagnostic precision at ability theta.
     */
    fun fisherInformation3Pl(theta: Float, a: Float, b: Float, c: Float = 0.22f): Float {
        val p = prob3Pl(theta, a, b, c)
        val ratio = ((p - c) / (1f - c)).coerceAtLeast(0f)
        return (a * a * ratio * ratio * ((1f - p) / p)).coerceAtLeast(0.04f)
    }

    fun classifyAutomaticity(isCorrect: Boolean, responseTimeMs: Long, difficulty: Int): AutomaticityClassification {
        val targetFastMs = when (difficulty) {
            1, 2 -> 5_500L
            3 -> 6_800L
            else -> 8_200L
        }
        val translationThresholdMs = when (difficulty) {
            1, 2 -> 11_500L
            3 -> 13_500L
            else -> 16_000L
        }
        return when {
            !isCorrect && responseTimeMs < 1_800L -> AutomaticityClassification.IMPULSIVE_ERROR
            isCorrect && responseTimeMs <= targetFastMs -> AutomaticityClassification.AUTOMATIC_C1
            responseTimeMs >= translationThresholdMs -> AutomaticityClassification.TRANSLATION_LATENCY
            else -> AutomaticityClassification.CONTROLLED_FLUENT
        }
    }

    /**
     * Computes a full Bayesian 3PL + Response-Time IRT posterior update.
     * Distinguishes true automatic C1 mastery from slow mental-translation accuracy.
     */
    fun computeBayesian3PlUpdate(
        priorMastery: Float,
        priorSigma: Float,
        currentTier: Int,
        recentStreak: Int,
        itemDifficulty: Int,
        isAiSurgical: Boolean,
        isCorrect: Boolean,
        responseTimeMs: Long,
        guessingFloorC: Float = 0.22f
    ): Bayesian3PlPosterior {
        val theta = masteryToTheta(priorMastery)
        val a = difficultyToDiscrimination(itemDifficulty, isAiSurgical)
        val b = difficultyToBeta(itemDifficulty)
        val p = prob3Pl(theta, a, b, guessingFloorC)
        val info = fisherInformation3Pl(theta, a, b, guessingFloorC)
        val autoClass = classifyAutomaticity(isCorrect, responseTimeMs, itemDifficulty)

        // Convert priorSigma (on 0..100 mastery scale) to theta variance
        val sigmaTheta = (priorSigma / 22f).coerceIn(0.20f, 1.30f)
        val priorPrecision = 1f / (sigmaTheta * sigmaTheta)

        // RT-IRT evidence weight: slow correct responses indicate mental translation from EN/AR,
        // yielding lower mastery jump than automatic retrieval!
        val rtWeight = when (autoClass) {
            AutomaticityClassification.AUTOMATIC_C1 -> 1.24f
            AutomaticityClassification.CONTROLLED_FLUENT -> 1.00f
            AutomaticityClassification.TRANSLATION_LATENCY -> if (isCorrect) 0.62f else 1.12f
            AutomaticityClassification.IMPULSIVE_ERROR -> 0.72f
        }

        val posteriorPrecision = priorPrecision + info * rtWeight
        val posteriorSigmaTheta = sqrt(1f / posteriorPrecision).coerceIn(0.20f, 1.25f)

        // Score gradient of 3PL log-likelihood: d/dtheta log L
        val gradient = if (isCorrect) {
            a * ((p - guessingFloorC) / (p * (1f - guessingFloorC))) * (1f - p)
        } else {
            -a * ((p - guessingFloorC) / (1f - guessingFloorC))
        }

        val deltaTheta = (gradient / posteriorPrecision) * rtWeight
        val updatedTheta = (theta + deltaTheta).coerceIn(-1.6f, 2.25f)
        val updatedMastery = thetaToMastery(updatedTheta)
        val updatedSigma = (posteriorSigmaTheta * 22f).coerceIn(4.5f, 26f)

        val optimalTierFromMastery = computeOptimalDifficultyTier(updatedMastery, updatedSigma)
        val adaptedTier = when {
            isCorrect && autoClass == AutomaticityClassification.AUTOMATIC_C1 &&
                recentStreak >= 1 && itemDifficulty >= currentTier ->
                min(4, max(currentTier + 1, optimalTierFromMastery))
            isCorrect && autoClass == AutomaticityClassification.TRANSLATION_LATENCY ->
                currentTier // Hold tier steady until retrieval becomes automatic without translation!
            !isCorrect && recentStreak == 0 ->
                max(1, min(currentTier - 1, optimalTierFromMastery))
            else -> optimalTierFromMastery
        }

        val actual = if (isCorrect) 1f else 0f
        return Bayesian3PlPosterior(
            updatedMastery = updatedMastery,
            updatedSigma = updatedSigma,
            updatedTier = adaptedTier,
            fisherInformation = info,
            surpriseResidual = actual - p,
            automaticity = autoClass
        )
    }

    fun computeOptimalDifficultyTier(mastery: Float, sigma: Float): Int {
        // Target ~72% success zone (Zone of Proximal Development), slightly conservative when sigma is high
        val conservativeMastery = mastery - (sigma - 10f).coerceAtLeast(0f) * 0.25f
        return when {
            conservativeMastery < 46f -> 1
            conservativeMastery < 63f -> 2
            conservativeMastery < 79f -> 3
            else -> 4
        }
    }

    /**
     * Exact FSRS-4.5 Power-Law Forgetting Curve: R(t, S) = (1 + 19/81 * t / S)^(-0.5)
     * At t = S days, R = (1 + 19/81)^(-0.5) = (100/81)^(-0.5) = 9/10 = 0.90 (90% retention).
     */
    fun computeFsrsRetrievability(
        state: SrsCardStateEntity?,
        nowMs: Long
    ): Float {
        if (state == null) return 0.38f // Unseen high-yield expression -> high initial acquisition urgency
        val stability = state.stabilityDays.coerceAtLeast(0.15f)
        val scheduledMs = (stability * 86_400_000f).toLong()
        val lastReviewMs = state.nextReviewAt - scheduledMs
        val elapsedDays = ((nowMs - lastReviewMs).coerceAtLeast(0L) / 86_400_000f)
        return (1f + FSRS_FACTOR * (elapsedDays / stability)).pow(FSRS_DECAY).coerceIn(0.05f, 1.0f)
    }

    /**
     * Desirable-Difficulty Urgency Curve:
     * Peaks sharply as retrievability approaches and drops below the 90% retention cliff.
     */
    fun computeFsrsUrgency(
        state: SrsCardStateEntity?,
        retrievability: Float
    ): Float {
        if (state == null) return 38f
        val deficit = (TARGET_RETENTION - retrievability)
        return when {
            retrievability <= 0.75f -> 72f + (0.75f - retrievability) * 85f + state.lapses * 14f
            retrievability <= 0.91f -> 48f + deficit * 120f + state.lapses * 10f
            retrievability <= 0.96f -> 16f + (0.96f - retrievability) * 180f
            else -> 3f // Freshly consolidated -> very low marginal ROI right now
        }
    }

    /**
     * FSRS-4.5 State Transition for Rating q in {1=Again, 2=Hard, 3=Good, 4=Easy}.
     */
    fun computeFsrsNextState(
        prev: SrsCardStateEntity?,
        quality: Int,
        nowMs: Long
    ): FsrsNextSchedule {
        val q = quality.coerceIn(1, 4)
        val rNow = computeFsrsRetrievability(prev, nowMs)

        if (prev == null) {
            val initS = when (q) {
                1 -> 0.25f
                2 -> 0.85f
                3 -> 2.40f
                else -> 5.80f
            }
            val initD = when (q) {
                1 -> 7.2f
                2 -> 6.1f
                3 -> 4.9f
                else -> 3.6f
            }
            return FsrsNextSchedule(
                stabilityDays = initS,
                difficulty = initD,
                retrievabilityNow = rNow,
                optimalIntervalDays = initS,
                lapses = if (q == 1) 1 else 0
            )
        }

        val prevD = prev.difficulty.coerceIn(1f, 10f)
        val prevS = prev.stabilityDays.coerceAtLeast(0.2f)

        // Mean-reverting difficulty update
        val rawD = prevD - 0.85f * (q - 3)
        val nextD = (0.15f * 5.0f + 0.85f * rawD).coerceIn(1.2f, 9.8f)

        val nextS = if (q == 1) {
            // Post-lapse stability (FSRS forget formula)
            (0.32f * nextD.pow(-0.35f) * (prevS + 1f).pow(0.42f)).coerceIn(0.20f, max(0.5f, prevS * 0.45f))
        } else {
            val hardPenalty = if (q == 2) 0.58f else 1.0f
            val easyBonus = if (q == 4) 1.38f else 1.0f
            val retrievabilityGain = exp(0.82f * (1f - rNow)) - 1f
            val growth = 1f + 3.1f * (11f - nextD) * prevS.pow(-0.18f) * retrievabilityGain.coerceAtLeast(0.18f) * hardPenalty * easyBonus
            (prevS * growth).coerceIn(0.5f, 120f)
        }

        // Interval for target retention R_target = 0.90 -> I = S * 81/19 * (R^(-2) - 1) = S when R=0.90
        val intervalDays = nextS * (81f / 19f) * (TARGET_RETENTION.pow(-2f) - 1f)
        return FsrsNextSchedule(
            stabilityDays = nextS,
            difficulty = nextD,
            retrievabilityNow = rNow,
            optimalIntervalDays = intervalDays.coerceIn(0.20f, 120f),
            lapses = prev.lapses + if (q == 1) 1 else 0
        )
    }

    /**
     * Multi-signal Drift-Diffusion Cognitive Load & Precision Telemetry Analysis.
     */
    fun analyzeSessionTelemetry(
        logs: List<DiagnosticSessionLogEntity>,
        pillarSigmas: List<Float>,
        srsRetrievabilities: List<Float>
    ): Pair<CognitiveLoadLevel, EnginePrecisionTelemetry> {
        val avgSigma = if (pillarSigmas.isNotEmpty()) pillarSigmas.average().toFloat() else 22f
        val precisionPct = (100f - (avgSigma - 4.5f) * 3.4f).roundToInt().coerceIn(24, 99)

        val meanRetPct = if (srsRetrievabilities.isNotEmpty()) {
            (srsRetrievabilities.average() * 100.0).roundToInt().coerceIn(35, 100)
        } else 82

        if (logs.isEmpty()) {
            return CognitiveLoadLevel.FRESH to EnginePrecisionTelemetry(
                diagnosticPrecisionPercent = precisionPct,
                automaticityIndexPercent = 64,
                meanRetrievabilityPercent = meanRetPct,
                medianResponseSeconds = 6.2f,
                translationRiskDetected = false,
                reorderedSectorsReason = "Bayesianischer Kaltstart · Priorisiert Kalibrierung & Blind-Hören",
                activeContentModificationsCount = 0
            )
        }

        val recent = logs.take(12)
        val accuracy = recent.count { it.isCorrect }.toFloat() / recent.size
        val sortedMs = recent.map { it.responseTimeMs }.sorted()
        val medianMs = sortedMs[sortedMs.size / 2]
        val medianSec = ((medianMs / 100L).toFloat() / 10f).coerceIn(1.0f, 45.0f)

        // Compare recent 4 trials vs older trials to detect acute latency drift (fatigue)
        val head4Ms = recent.take(4).map { it.responseTimeMs }.average()
        val tailMs = if (recent.size > 4) recent.drop(4).map { it.responseTimeMs }.average() else head4Ms
        val latencyDriftRatio = if (tailMs > 0) (head4Ms / tailMs).toFloat() else 1.0f

        val load = when {
            accuracy < 0.45f || (accuracy < 0.62f && medianMs > 14_500L) || latencyDriftRatio > 1.45f ->
                CognitiveLoadLevel.FATIGUED
            accuracy < 0.68f || medianMs > 11_800L || latencyDriftRatio > 1.25f ->
                CognitiveLoadLevel.ELEVATED
            accuracy >= 0.82f && medianMs <= 7_200L ->
                CognitiveLoadLevel.FRESH
            else ->
                CognitiveLoadLevel.OPTIMAL
        }

        val slowCorrectCount = recent.count { it.isCorrect && it.responseTimeMs > 12_500L }
        val translationRisk = slowCorrectCount >= 2 || medianMs > 12_200L

        // Automaticity combines accuracy + non-translation response speed
        val speedScore = (100f - ((medianSec - 4.2f).coerceAtLeast(0f) * 5.8f)).coerceIn(25f, 100f)
        val automaticityPct = (accuracy * 55f + speedScore * 0.45f).roundToInt().coerceIn(28, 99)

        return load to EnginePrecisionTelemetry(
            diagnosticPrecisionPercent = precisionPct,
            automaticityIndexPercent = automaticityPct,
            meanRetrievabilityPercent = meanRetPct,
            medianResponseSeconds = medianSec,
            translationRiskDetected = translationRisk,
            reorderedSectorsReason = "",
            activeContentModificationsCount = 0
        )
    }

    fun lnSafe(x: Float): Float = ln(x.coerceAtLeast(1e-4f))
}
