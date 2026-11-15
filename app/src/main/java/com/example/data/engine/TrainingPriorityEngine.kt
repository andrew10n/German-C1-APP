package com.example.data.engine

import com.example.data.model.SkillCategory
import com.example.data.model.SkillScore

/**
 * Intelligent Scoring Engine that calculates which training activity provides the
 * highest learning value for reaching genuine German C1.
 *
 * Implements the multi-factor weighted priority algorithm:
 * - Weakness Severity: 30%
 * - Importance for C1: 25%
 * - Recurring Errors: 20%
 * - Time Since Last Training: 15%
 * - Recent Performance: 10%
 */
class TrainingPriorityEngine(
    val weaknessWeight: Double = WEAKNESS_WEIGHT,
    val importanceWeight: Double = IMPORTANCE_WEIGHT,
    val recurringErrorsWeight: Double = RECURRING_ERRORS_WEIGHT,
    val timeSinceLastTrainingWeight: Double = TIME_SINCE_LAST_TRAINING_WEIGHT,
    val recentPerformanceWeight: Double = RECENT_PERFORMANCE_WEIGHT
) {

    companion object {
        const val WEAKNESS_WEIGHT = 0.30
        const val IMPORTANCE_WEIGHT = 0.25
        const val RECURRING_ERRORS_WEIGHT = 0.20
        const val TIME_SINCE_LAST_TRAINING_WEIGHT = 0.15
        const val RECENT_PERFORMANCE_WEIGHT = 0.10
    }

    init {
        val total = weaknessWeight + importanceWeight + recurringErrorsWeight + timeSinceLastTrainingWeight + recentPerformanceWeight
        require(kotlin.math.abs(total - 1.0) < 0.001) {
            "Sum of priority weights must equal 1.0 (current sum: $total)"
        }
    }

    /**
     * Standardized activity input representation with 0..100 normalized metrics.
     */
    data class TrainingActivity(
        val id: String,
        val title: String,
        val category: SkillCategory,
        val weaknessSeverity: Double,       // 0..100 (100 = most severe weakness)
        val importanceForC1: Double,        // 0..100 (100 = critical functional C1 competency)
        val recurringErrorsScore: Double,   // 0..100 (100 = high count of unmastered recurring errors)
        val timeSinceLastTraining: Double,  // 0..100 (100 = longest elapsed time since practice)
        val recentPerformanceNeed: Double,  // 0..100 (100 = recent struggle requiring immediate reinforcement)
        val recommendedTaskTitle: String = "",
        val environment: String = "Beruf & Akademie"
    )

    /**
     * Comprehensive evaluation result with score breakdown and rationale.
     */
    data class PriorityEvaluation(
        val activity: TrainingActivity,
        val learningValueScore: Double, // 0.0 - 100.0
        val normalizedPriorityPercent: Int, // 1 - 99%
        val rank: Int,
        val weaknessContribution: Double,
        val importanceContribution: Double,
        val errorsContribution: Double,
        val timeSinceTrainingContribution: Double,
        val recentPerformanceContribution: Double,
        val rationaleHeadline: String,
        val detailedRationaleDe: String
    )

    /**
     * Core scoring formula directly evaluating the 5 weighted dimensions.
     *
     * Learning Value = (Weakness Severity * 0.30) +
     *                  (Importance for C1 * 0.25) +
     *                  (Recurring Errors * 0.20) +
     *                  (Time Since Last Training * 0.15) +
     *                  (Recent Performance * 0.10)
     */
    fun calculateLearningValue(
        weaknessSeverity: Double,       // 0.0 - 100.0 (30%)
        importanceForC1: Double,        // 0.0 - 100.0 (25%)
        recurringErrors: Double,        // 0.0 - 100.0 (20%)
        timeSinceLastTraining: Double,  // 0.0 - 100.0 (15%)
        recentPerformance: Double       // 0.0 - 100.0 (10%)
    ): Double {
        val w = weaknessSeverity.coerceIn(0.0, 100.0) * weaknessWeight
        val i = importanceForC1.coerceIn(0.0, 100.0) * importanceWeight
        val e = recurringErrors.coerceIn(0.0, 100.0) * recurringErrorsWeight
        val t = timeSinceLastTraining.coerceIn(0.0, 100.0) * timeSinceLastTrainingWeight
        val p = recentPerformance.coerceIn(0.0, 100.0) * recentPerformanceWeight

        return (w + i + e + t + p).coerceIn(0.0, 100.0)
    }

    /**
     * Evaluates a training activity and generates an attribution breakdown and diagnostic rationale.
     */
    fun evaluate(activity: TrainingActivity, rank: Int = 1): PriorityEvaluation {
        val cWeakness = activity.weaknessSeverity.coerceIn(0.0, 100.0) * weaknessWeight
        val cImportance = activity.importanceForC1.coerceIn(0.0, 100.0) * importanceWeight
        val cErrors = activity.recurringErrorsScore.coerceIn(0.0, 100.0) * recurringErrorsWeight
        val cTime = activity.timeSinceLastTraining.coerceIn(0.0, 100.0) * timeSinceLastTrainingWeight
        val cPerformance = activity.recentPerformanceNeed.coerceIn(0.0, 100.0) * recentPerformanceWeight

        val totalScore = (cWeakness + cImportance + cErrors + cTime + cPerformance).coerceIn(1.0, 99.0)
        val percent = totalScore.toInt()

        val headline = "Fokus: ${activity.category.titleDe} (${percent}% Lernwert)"

        val rationale = buildString {
            append("Prioritäts-Diagnose ($percent%): ")
            if (activity.recurringErrorsScore >= 40.0) {
                append("Ungelöste Wiederholungsfehler verlangen Festigung. ")
            }
            if (activity.timeSinceLastTraining >= 40.0) {
                append("Längeres Übungsintervall verstrichen (Auffrischungsbedarf). ")
            } else if (activity.timeSinceLastTraining <= 10.0) {
                append("Kürzlich geübt; gezielte Konsolidierung. ")
            }
            if (activity.importanceForC1 >= 80.0) {
                append("Kritische Kernkompetenz für spontane C1-Handlungsfähigkeit im Alltag & Beruf.")
            } else {
                append("Spezifische Festigung der Präzision.")
            }
        }

        return PriorityEvaluation(
            activity = activity,
            learningValueScore = totalScore,
            normalizedPriorityPercent = percent,
            rank = rank,
            weaknessContribution = cWeakness,
            importanceContribution = cImportance,
            errorsContribution = cErrors,
            timeSinceTrainingContribution = cTime,
            recentPerformanceContribution = cPerformance,
            rationaleHeadline = headline,
            detailedRationaleDe = rationale
        )
    }

    /**
     * Ranks all candidate activities from highest learning value to lowest.
     */
    fun rankActivities(activities: List<TrainingActivity>): List<PriorityEvaluation> {
        return activities
            .map { evaluate(it) }
            .sortedByDescending { it.learningValueScore }
            .mapIndexed { index, eval -> eval.copy(rank = index + 1) }
    }

    /**
     * Selects the single highest-value training activity.
     */
    fun selectHighestValueActivity(activities: List<TrainingActivity>): PriorityEvaluation {
        require(activities.isNotEmpty()) { "Cannot select from an empty training activities list" }
        return rankActivities(activities).first()
    }

    /**
     * Factory method creating a normalized TrainingActivity from repository state models.
     */
    fun createActivityFromSkill(
        skill: SkillScore,
        unresolvedErrorCount: Int,
        importanceOverride: Double? = null,
        recentPerformanceScore: Double = 30.0
    ): TrainingActivity {
        val defaultImportance = when (skill.category) {
            SkillCategory.INTERAKTION, SkillCategory.SPONTANES_SPRECHEN -> 95.0
            SkillCategory.HOERVERSTEHEN -> 90.0
            SkillCategory.WORTSCHATZ, SkillCategory.GRAMMATIK -> 85.0
            SkillCategory.MEDIATION -> 80.0
            SkillCategory.LESEVERSTEHEN, SkillCategory.SCHREIBEN -> 75.0
            SkillCategory.AUSSPRACHE_PROSODIE -> 70.0
        }

        val weakness = (100 - skill.score).toDouble().coerceIn(0.0, 100.0)
        val errorsScore = (unresolvedErrorCount * 25.0).coerceIn(0.0, 100.0)
        val timeScore = (skill.lastTestedDaysAgo * 25.0).coerceIn(0.0, 100.0)

        val taskTitle = when (skill.category) {
            SkillCategory.INTERAKTION -> "Unterbrechungs-Stresstest im Führungskreis"
            SkillCategory.HOERVERSTEHEN -> "Schwäbischer Werkstattdialog & Schnellsprech-Inferenz"
            SkillCategory.SPONTANES_SPRECHEN -> "Spontane Gegendarstellung unter Zeitdruck"
            SkillCategory.MEDIATION -> "Fachjuristisches Gutachten in Bürgersprache vermitteln"
            SkillCategory.SCHREIBEN -> "Schlichtungsbericht mit präziser Registerabstufung"
            SkillCategory.GRAMMATIK -> "Subtile Passiversatzformen & Zustandspassiv"
            SkillCategory.WORTSCHATZ -> "Nomen-Verb-Verbindungen im Verhandlungsgespräch"
            SkillCategory.LESEVERSTEHEN -> "Feuilleton-Glosse: Ironie & implizite Subtexte"
            SkillCategory.AUSSPRACHE_PROSODIE -> "Emphatische Satzakzentuierung bei Einwänden"
        }

        val env = when (skill.category) {
            SkillCategory.INTERAKTION, SkillCategory.SPONTANES_SPRECHEN -> "Beruf & Führung (35%)"
            SkillCategory.HOERVERSTEHEN -> "Alltag & Medien (40%)"
            SkillCategory.LESEVERSTEHEN, SkillCategory.MEDIATION -> "Technik & Diskurs (25%)"
            else -> "Alltag & Soziales (40%)"
        }

        return TrainingActivity(
            id = "act_${skill.category.name.lowercase()}",
            title = skill.category.titleDe,
            category = skill.category,
            weaknessSeverity = weakness,
            importanceForC1 = importanceOverride ?: defaultImportance,
            recurringErrorsScore = errorsScore,
            timeSinceLastTraining = timeScore,
            recentPerformanceNeed = recentPerformanceScore,
            recommendedTaskTitle = taskTitle,
            environment = env
        )
    }
}
