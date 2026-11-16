package com.example.programme

data class AdaptiveLessonAddition(
    val taskId: String,
    val title: String,
    val instruction: String,
    val prompt: String,
    val context: ContextTrack,
    val reviewExperienceId: String? = null
)

data class AdaptiveLessonContentPlan(
    val difficulty: AdaptiveDifficulty,
    val focusTaskId: String,
    val reason: String,
    val addition: AdaptiveLessonAddition?,
    val recommendation: AdaptiveRecommendation
)

/**
 * Builds a focused lesson variant from this learner's saved practice. Support is
 * triggered by local repair needs; stretch tasks require varied, successful practice.
 */
object AdaptiveLessonContentEngine {
    fun create(
        unit: CurriculumUnit,
        attempts: List<Attempt>,
        experiences: List<RealLifeExperience>,
        context: ContextTrack,
        now: Long
    ): AdaptiveLessonContentPlan {
        val unitHistory = attempts.filter {
            it.unitId == unit.id && it.timestamp <= now && it.response.isNotBlank()
        }
        val eligibleExperiences = experiences.filter { it.createdAt <= now }
        val decision = AdaptiveControlEngine.decideForUnit(unit, attempts, eligibleExperiences, now)
        val recommendation = AdaptiveLearning.recommend(unit, unitHistory, now, eligibleExperiences)
        val checkpoint = AdaptiveDiagnosticRoute.evaluate(unit, unitHistory, now)
        val dueExperience = eligibleExperiences
            .filter { it.nextReviewAt <= now }
            .minByOrNull { it.nextReviewAt }

        val difficulty = when {
            checkpoint.state == DiagnosticCheckpointState.NEEDS_REMEDIATION ||
                dueExperience != null ||
                recommendation.errors.isNotEmpty() ||
                recommendation.difficulty == AdaptiveDifficulty.SUPPORT -> AdaptiveDifficulty.SUPPORT
            recommendation.difficulty == AdaptiveDifficulty.CHALLENGE -> AdaptiveDifficulty.CHALLENGE
            else -> AdaptiveDifficulty.CORE
        }
        val addition = when (difficulty) {
            AdaptiveDifficulty.SUPPORT -> supportAddition(
                unit = unit,
                context = context,
                recommendation = recommendation,
                dueExperience = dueExperience,
                checkpoint = checkpoint
            )
            AdaptiveDifficulty.CHALLENGE -> challengeAddition(unit, unitHistory, context)
            AdaptiveDifficulty.CORE -> null
        }
        val reason = when (difficulty) {
            AdaptiveDifficulty.SUPPORT -> "Gezielte Zusatzhilfe aus einer fälligen Wiederholung oder einem lokalen Antwortsignal."
            AdaptiveDifficulty.CORE -> "Der Kerninhalt bleibt im üblichen Schrittumfang."
            AdaptiveDifficulty.CHALLENGE -> "Zusatztransfer erst nach richtigem Check und vielfältigen, unassistierten Antworten."
        }
        return AdaptiveLessonContentPlan(
            difficulty = difficulty,
            focusTaskId = decision.primaryTaskId ?: "retrieval",
            reason = reason,
            addition = addition,
            recommendation = recommendation
        )
    }

    private fun supportAddition(
        unit: CurriculumUnit,
        context: ContextTrack,
        recommendation: AdaptiveRecommendation,
        dueExperience: RealLifeExperience?,
        checkpoint: DiagnosticCheckpoint
    ): AdaptiveLessonAddition? {
        val content = unit.content ?: return null
        if (dueExperience != null) {
            val repair = RealLifeRepairEngine.create(dueExperience)
            return AdaptiveLessonAddition(
                taskId = when (dueExperience.issueType) {
                    RealLifeIssueType.LISTENING -> "listening"
                    RealLifeIssueType.GRAMMAR_UNDER_PRESSURE -> "controlled"
                    else -> "interaction"
                },
                title = "Fällige Alltagsreparatur · ${dueExperience.issueType.label}",
                instruction = repair.explanation,
                prompt = repair.transferPrompt,
                context = dueExperience.context,
                reviewExperienceId = dueExperience.id
            )
        }

        val error = recommendation.errors.firstOrNull()
        if (error != null) {
            return AdaptiveLessonAddition(
                taskId = "controlled",
                title = "Gezielte Reparatur · ${error.label}",
                instruction = error.explanation,
                prompt = buildString {
                    append("Prüfe zuerst deine gespeicherte Formulierung: „")
                    append(error.excerpt)
                    append("“. ")
                    append(error.label)
                    append(": ")
                    append(error.remediation)
                    append(" Formuliere danach einen neuen Beispielsatz zum Lernziel: ")
                    append(unit.objectives.firstOrNull() ?: unit.title)
                },
                context = context
            )
        }

        if (checkpoint.state == DiagnosticCheckpointState.NEEDS_REMEDIATION) {
            return AdaptiveLessonAddition(
                taskId = "controlled",
                title = "Verständnis neu aufbauen",
                instruction = content.explanation,
                prompt = "Erkläre die Frage ohne Antwortoptionen in eigenen Worten: ${content.question} " +
                    "Nenne den entscheidenden Hinweis aus dem Lektionsinput und formuliere anschließend, " +
                    "welche Information dir noch fehlt.",
                context = context
            )
        }

        return AdaptiveLessonAddition(
            taskId = "controlled",
            title = "Zusätzliche angeleitete Formulierung",
            instruction = recommendation.nextStep,
            prompt = "Bearbeite die Kernaufgabe ohne Modellhilfe: ${content.controlledPrompt} " +
                "Prüfe danach dein Lernziel: ${unit.objectives.firstOrNull() ?: unit.title}",
            context = context
        )
    }

    private fun challengeAddition(
        unit: CurriculumUnit,
        unitHistory: List<Attempt>,
        context: ContextTrack
    ): AdaptiveLessonAddition? {
        val content = unit.content ?: return null
        val production = unitHistory.filter {
            it.taskId in productionTasks && it.source == EvidenceSource.SELF_REFLECTION && !it.assisted
        }
        val unusedContext = ContextTrack.values().firstOrNull { candidate ->
            production.none { it.context == candidate }
        }
        val challengeContext = unusedContext ?: context
        val challengeIndex = unitHistory.count { it.taskId == "transfer" }
        val objective = if (unit.objectives.isEmpty()) unit.title
        else unit.objectives[challengeIndex % unit.objectives.size]
        val scenario = unit.contexts[challengeContext].orEmpty()
        return AdaptiveLessonAddition(
            taskId = "transfer",
            title = "Neue Transferaufgabe · ${contextLabel(challengeContext)}",
            instruction = "Diese Zusatzaufgabe wird angeboten, weil der Verständnischeck richtig war und mehrere " +
                "unassistierte Antworten in unterschiedlichen Kontexten vorliegen. Das ist keine C1-Einstufung.",
            prompt = "Übertrage das Lernziel „$objective“ auf diesen neuen Kontext: $scenario " +
                "Formuliere eine klare Aussage, begründe sie mit einem konkreten Beispiel und benenne eine " +
                "Einschränkung oder mögliche Rückfrage. Passe dein Register an; verwende keine Vorlage.",
            context = challengeContext
        )
    }

    private fun contextLabel(context: ContextTrack): String = when (context) {
        ContextTrack.GENERAL -> "Alltag"
        ContextTrack.SOCIAL -> "soziale Gespräche"
        ContextTrack.ENGINEERING -> "Ingenieurwesen"
        ContextTrack.COACHING -> "Jugendtraining"
    }

    private val productionTasks = setOf("speaking", "interaction", "writing", "transfer")
}
