package com.example.programme

import java.util.Locale

enum class AdaptiveDifficulty { SUPPORT, CORE, CHALLENGE }

data class LearnerError(
    val code: String,
    val label: String,
    val excerpt: String,
    val explanation: String,
    val remediation: String,
    val objective: String?
)

data class AdaptiveRecommendation(
    val difficulty: AdaptiveDifficulty,
    val reason: String,
    val errors: List<LearnerError>,
    val feedbackClaims: List<FeedbackClaim>,
    val nextStep: String,
    val weaknesses: List<WeaknessSummary> = emptyList(),
    val recyclingPlans: List<ErrorRecyclingPlan> = emptyList()
)

data class ErrorRecyclingPlan(
    val code: String,
    val objective: String?,
    val repeatedCount: Int,
    val nextReviewAt: Long,
    val difficulty: AdaptiveDifficulty,
    val instruction: String
)

/** Conservative, local pattern checks. These are suggestions for revision, never proficiency scores. */
object AdaptiveLearning {
    private val PRODUCTION_TASKS = setOf("retrieval", "controlled", "speaking", "interaction", "writing", "transfer")
    fun detectResponseSignals(response: String, unit: CurriculumUnit): List<LearnerError> =
        detectErrors(response, unit)

    fun recommend(
        unit: CurriculumUnit,
        attempts: List<Attempt>,
        now: Long,
        realLifeExperiences: List<RealLifeExperience> = emptyList()
    ): AdaptiveRecommendation {
        val recent = attempts.filter {
            it.unitId == unit.id && it.timestamp <= now && it.response.isNotBlank()
        }.sortedByDescending { it.timestamp }
        val errors = recent.flatMap { detectErrors(it.response, unit) }
            .distinctBy { "${it.code}:${it.excerpt}" }
            .take(4)
        val feedbackClaims = recent
            .flatMap { attempt -> FeedbackEvidenceEngine.claimsFor(attempt, detectErrors(attempt.response, unit)) }
            .distinctBy { it.id }
            .take(4)
        val errorCounts = recent.flatMap { detectErrors(it.response, unit) }
            .groupingBy { it.code }
            .eachCount()
        val recyclingPlans = errors.map { error ->
            val repeatedCount = errorCounts[error.code] ?: 1
            val intervalIndex = (repeatedCount - 1).coerceIn(0, RetrievalPolicy.intervalsDays.lastIndex)
            val difficulty = when {
                repeatedCount >= 2 -> AdaptiveDifficulty.SUPPORT
                else -> AdaptiveDifficulty.CORE
            }
            ErrorRecyclingPlan(
                code = error.code,
                objective = error.objective,
                repeatedCount = repeatedCount,
                nextReviewAt = now + RetrievalPolicy.intervalsDays[intervalIndex] * 86_400_000L,
                difficulty = difficulty,
                instruction = error.remediation
            )
        }
        val weaknesses = WeaknessEngine.profile(attempts, realLifeExperiences, now).top()
        val weakSignals = recent.sumOf { detectErrors(it.response, unit).size }
        val incorrectQuizzes = recent.count {
            it.taskId == "quiz" && it.errors.isNotEmpty()
        }
        val latestProduction = recent
            .filter { it.source == EvidenceSource.SELF_REFLECTION && !it.assisted && it.taskId in PRODUCTION_TASKS }
            .groupBy { it.taskId }
            .values
            .mapNotNull { taskAttempts -> taskAttempts.maxByOrNull { it.timestamp } }
        val challengeReady =
            recent.any {
                it.taskId == "quiz" &&
                    it.source == EvidenceSource.AUTOMATIC_QUIZ &&
                    it.scores[Dimension.COMPREHENSION] == 4
            } &&
                latestProduction.size >= 3 &&
                latestProduction.map { it.context }.distinct().size >= 2 &&
                latestProduction.all { detectErrors(it.response, unit).isEmpty() }
        val personalStruggles = realLifeExperiences.filter { it.nextReviewAt <= now }
        val difficulty = when {
            personalStruggles.isNotEmpty() || incorrectQuizzes >= 2 || weakSignals >= 3 ||
                recyclingPlans.any { it.repeatedCount >= 2 } ||
                weaknesses.any { it.sampleCount >= 2 } -> AdaptiveDifficulty.SUPPORT
            challengeReady -> AdaptiveDifficulty.CHALLENGE
            else -> AdaptiveDifficulty.CORE
        }
        val reason = when (difficulty) {
            AdaptiveDifficulty.SUPPORT -> if (personalStruggles.isNotEmpty()) {
                "Eine echte Alltagssituation ist fällig. Repariere sie zuerst und übertrage sie anschließend in einen neuen Kontext."
            } else if (weaknesses.any { it.sampleCount >= 2 }) {
                "Ein wiederkehrendes Muster wird zuerst gezielt bearbeitet: ${weaknesses.first().signal.label}."
            } else {
                "Mehrere offene Hinweise sprechen für einen kurzen Reparaturschritt."
            }
            AdaptiveDifficulty.CHALLENGE -> "Einzelne Übungsaufgaben waren unauffällig; der nächste Prompt wird anspruchsvoller. Das ist kein Kompetenznachweis."
            AdaptiveDifficulty.CORE -> "Weiter im Kernpfad; einzelne Hinweise werden gezielt wiederholt."
        }
        val nextStep = personalStruggles.firstOrNull()?.let {
            "Formuliere deine Situation neu: ${it.issueType.label.lowercase(Locale.GERMAN)} · danach eine ähnliche Situation ohne Vorlage."
        } ?: errors.firstOrNull()?.remediation
            ?: when (difficulty) {
                AdaptiveDifficulty.SUPPORT -> "Bearbeite den kontrollierten Satzbau erneut und vergleiche ihn mit der Regel."
                AdaptiveDifficulty.CHALLENGE -> "Bearbeite den anspruchsvolleren Prompt ohne Modell; bekannte Muster bleiben nur begrenzt prüfbar."
                AdaptiveDifficulty.CORE -> "Wiederhole den Abruf und formuliere anschließend einen eigenen Beispielsatz."
            }
        return AdaptiveRecommendation(
            difficulty = difficulty,
            reason = reason,
            errors = errors,
            feedbackClaims = feedbackClaims,
            nextStep = nextStep,
            weaknesses = weaknesses,
            recyclingPlans = recyclingPlans
        )
    }

    private fun detectErrors(response: String, unit: CurriculumUnit): List<LearnerError> {
        val text = response.trim()
        if (text.isBlank()) return emptyList()
        val lower = text.lowercase(Locale.GERMAN)
        val objective = unit.objectives.firstOrNull { objectiveText ->
            unit.content?.grammar?.let { grammar ->
                objectiveText.split(" ").any { word ->
                    word.length > 5 && grammar.contains(word, ignoreCase = true)
                }
            } == true
        }
        return buildList {
            if (Regex("\\b(weil|dass|obwohl|während|damit)\\b\\s+[^.!?]*\\b(ich|du|er|sie|wir|ihr|man)\\s+\\w+\\s+\\w+").containsMatchIn(lower)) {
                add(LearnerError(
                    "subordinate-verb-position", "Verb im Nebensatz prüfen", text.take(120),
                    "Nach weil, dass, obwohl, während und damit steht das konjugierte Verb im Nebensatz normalerweise am Ende.",
                    "Formuliere den Satz mit dem finiten Verb am Ende neu und lies ihn langsam vor.",
                    objective
                ))
            }
            if (Regex("\\b(seit|seid)\\s+\\w+").containsMatchIn(lower) &&
                Regex("\\bseit\\s+ihr\\b|\\bihr\\s+seit\\b").containsMatchIn(lower).not()
            ) {
                add(LearnerError(
                    "seit-seid", "seit / seid unterscheiden", text.take(120),
                    "seit bezeichnet Zeit; seid ist die Verbform von sein bei ihr.",
                    "Ersetze die Stelle probeweise durch 'schon damals' (seit) oder 'bin/sind' (seid).",
                    objective
                ))
            }
            if (Regex(",?\\s+dass\\s+[A-ZÄÖÜ]").containsMatchIn(text)) {
                add(LearnerError(
                    "comma-dass", "Komma vor dass prüfen", text.take(120),
                    "Ein dass-Nebensatz wird im Deutschen durch ein Komma vom Hauptsatz getrennt.",
                    "Markiere Hauptsatz und dass-Nebensatz und setze das Komma an die Satzgrenze.",
                    objective
                ))
            }
            if (text.length >= 80 && text.firstOrNull()?.isLowerCase() == true) {
                add(LearnerError(
                    "sentence-capitalization", "Satzanfang prüfen", text.take(120),
                    "Ein neuer deutscher Satz beginnt mit einem Großbuchstaben.",
                    "Teile den Text in Sätze und korrigiere jeden Satzanfang einzeln.",
                    objective
                ))
            }
        }
    }
}
