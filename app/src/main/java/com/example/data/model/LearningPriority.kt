package com.example.data.model

data class TrainingPriorityRecommendation(
    val category: SkillCategory,
    val calculatedPriority: Int, // 0 - 100
    val priorityRank: Int, // 1 is highest
    val reasonHeadline: String,
    val detailedRationale: String,
    val recommendedTaskTitle: String,
    val sessionDurationMin: Int = 18,
    val targetEnvironment: String, // Alltag (40%), Beruf (35%), Technik (25%)
    val weightBreakdown: PriorityWeights
)

data class PriorityWeights(
    val severityOfWeaknessPct: Int,    // 30% weight
    val functionalImportancePct: Int,  // 25% weight
    val recurringErrorsPct: Int,       // 20% weight
    val daysSinceTrainedPct: Int,      // 15% weight
    val fatiguePerformancePct: Int     // 10% weight
)

object PriorityCalculator {
    fun calculate(
        skill: SkillScore,
        unresolvedErrorCount: Int,
        daysSinceTrained: Int,
        functionalWeight: Int, // 1 to 10
        fatigueScore: Int // 1 to 10
    ): TrainingPriorityRecommendation {
        // 1. Weakness severity (lower score = higher priority)
        val weaknessScore = (100 - skill.score).coerceIn(0, 100)
        val partWeakness = weaknessScore * 0.30

        // 2. Functional importance (multiplied to 0..100)
        val impScore = (functionalWeight * 10).coerceIn(0, 100)
        val partImportance = impScore * 0.25

        // 3. Recurring errors
        val errScore = (unresolvedErrorCount * 25).coerceIn(0, 100)
        val partErrors = errScore * 0.20

        // 4. Days since last trained
        val recencyScore = (daysSinceTrained * 25).coerceIn(0, 100)
        val partRecency = recencyScore * 0.15

        // 5. Fatigue and recent cognitive strain (0..100)
        val fatigueWeighted = ((10 - fatigueScore) * 10).coerceIn(0, 100)
        val partFatigue = fatigueWeighted * 0.10

        val totalPriority = (partWeakness + partImportance + partErrors + partRecency + partFatigue).toInt().coerceIn(1, 99)

        val env = when (skill.category) {
            SkillCategory.INTERAKTION, SkillCategory.SPONTANES_SPRECHEN -> "Beruf & Führung (35%)"
            SkillCategory.HOERVERSTEHEN -> "Alltag & Medien (40%)"
            SkillCategory.LESEVERSTEHEN, SkillCategory.MEDIATION -> "Technik & Fachdiskurs (25%)"
            SkillCategory.SCHREIBEN -> "Berufliche Korrespondenz"
            else -> "Alltag & Spontansprache"
        }

        val rationale = buildString {
            append("Intelligente Priorisierung ($totalPriority%): ")
            if (unresolvedErrorCount > 0) {
                append("$unresolvedErrorCount ungelöste Wiederholungsfehler verlangen Festigung. ")
            }
            if (daysSinceTrained >= 2) {
                append("Zuletzt vor $daysSinceTrained Tagen trainiert (Auffrischungsintervall). ")
            }
            append("Hohe Relevanz für spontane Handlungskompetenz.")
        }

        return TrainingPriorityRecommendation(
            category = skill.category,
            calculatedPriority = totalPriority,
            priorityRank = 1,
            reasonHeadline = "Fokus: ${skill.criticalWeakness}",
            detailedRationale = rationale,
            recommendedTaskTitle = when (skill.category) {
                SkillCategory.INTERAKTION -> "Unterbrechungs-Stresstest im Projektmeeting"
                SkillCategory.HOERVERSTEHEN -> "Schwäbischer Werkstattdialog & Schnellsprech-Inferenz"
                SkillCategory.SPONTANES_SPRECHEN -> "Spontane Gegendarstellung unter Zeitdruck"
                SkillCategory.MEDIATION -> "Patentanspruch in verständliches Kunden-Deutsch übersetzen"
                SkillCategory.SCHREIBEN -> "Schlichtungsbericht mit feiner Registerabstufung"
                SkillCategory.GRAMMATIK -> "Subtile Passiversatzformen & Zustandspassiv"
                SkillCategory.WORTSCHATZ -> "Nomen-Verb-Verbindungen im Verhandlungsgespräch"
                SkillCategory.LESEVERSTEHEN -> "Feuilleton-Glosse: Ironie & implizite Subtexte"
                SkillCategory.AUSSPRACHE_PROSODIE -> "Emphatische Satzakzentuierung bei Einwänden"
            },
            targetEnvironment = env,
            weightBreakdown = PriorityWeights(
                severityOfWeaknessPct = (partWeakness / 0.30).toInt(),
                functionalImportancePct = impScore,
                recurringErrorsPct = errScore,
                daysSinceTrainedPct = recencyScore,
                fatiguePerformancePct = fatigueWeighted
            )
        )
    }
}
