package com.example.programme

enum class AdaptiveAction {
    PRIORITIZE_LISTENING,
    PRIORITIZE_COMPREHENSION,
    PRIORITIZE_RETRIEVAL,
    PRIORITIZE_FORMULATION,
    PRIORITIZE_INTERACTION,
    PRIORITIZE_READING,
    PRIORITIZE_SPEAKING,
    PRIORITIZE_WRITING,
    ADD_CONVERSATION_VARIATION,
    SPACE_RETRIEVAL,
    DEFER_GENERIC_READING
}

enum class AdaptiveReasonCode {
    PERSONAL_REPAIR_DUE,
    LESSON_REVIEW_DUE,
    QUIZ_REPAIR,
    RESPONSE_REPAIR,
    FIRST_RETRIEVAL,
    NEXT_INPUT,
    NEXT_LISTENING,
    NEXT_COMPREHENSION_CHECK,
    NEXT_CONTROLLED_PRACTICE,
    NEXT_PRODUCTION,
    RECURRING_SIGNAL,
    BALANCED_PRACTICE,
    COURSE_COMPLETE
}

data class AdaptiveControlDecision(
    val primaryTaskId: String?,
    val actions: List<AdaptiveAction>,
    val reason: String,
    val confidence: DiagnosticConfidence,
    val dialogueBriefId: String?,
    val taskOrder: List<String>,
    val focusMinutes: Int?,
    val recommendedUnitId: String?,
    val recommendedContext: ContextTrack?,
    val reasonCode: AdaptiveReasonCode,
    val focusSignal: WeaknessSignal?
)

/**
 * Chooses the lesson and its next step from eligible evidence. Every saved response
 * can advance the sequence; completion remains practice progress, not proficiency.
 */
object AdaptiveControlEngine {
    private val taskSequence =
        listOf("retrieval", "reading", "listening", "quiz", "controlled", "speaking", "interaction", "writing")
    private val productionTasks = setOf("speaking", "interaction", "writing", "transfer")

    fun decide(
        attempts: List<Attempt>,
        experiences: List<RealLifeExperience>,
        now: Long
    ): AdaptiveControlDecision = decideInternal(attempts, experiences, now, null)

    fun decideForUnit(
        unit: CurriculumUnit,
        attempts: List<Attempt>,
        experiences: List<RealLifeExperience>,
        now: Long
    ): AdaptiveControlDecision = decideInternal(attempts, experiences, now, unit)

    private fun decideInternal(
        attempts: List<Attempt>,
        experiences: List<RealLifeExperience>,
        now: Long,
        unitOverride: CurriculumUnit?
    ): AdaptiveControlDecision {
        val eligibleAttempts = attempts.filter { it.timestamp <= now }
        val eligibleExperiences = experiences.filter { it.createdAt <= now }
        val weaknessProfile = WeaknessEngine.profile(eligibleAttempts, eligibleExperiences, now)
        val recurringWeakness = weaknessProfile.top().firstOrNull { it.sampleCount >= 2 }
        val dueExperience = eligibleExperiences
            .filter { it.nextReviewAt <= now }
            .minByOrNull { it.nextReviewAt }

        val routedUnit = unitOverride ?: AdaptiveDiagnosticRoute.nextUnit(eligibleAttempts, now)
        val unit = routedUnit ?: leastRecentlyPracticedUnit(eligibleAttempts)
        val unitHistory = unit?.let { selected ->
            eligibleAttempts.filter { it.unitId == selected.id && it.response.isNotBlank() }
        }.orEmpty()
        val checkpoint = unit?.let { AdaptiveDiagnosticRoute.evaluate(it, eligibleAttempts, now) }
        val latestQuiz = unitHistory
            .filter { it.taskId == "quiz" }
            .maxByOrNull { it.timestamp }
        val quizFailed = latestQuiz != null &&
            (latestQuiz.source != EvidenceSource.AUTOMATIC_QUIZ ||
                latestQuiz.scores[Dimension.COMPREHENSION] != 4)
        val productionNeedsRepair = unit?.let { selected ->
            unitHistory
                .filter {
                    it.source == EvidenceSource.SELF_REFLECTION &&
                        it.taskId in responseRepairTasks &&
                        !it.assisted
                }
                .groupBy { it.taskId }
                .values
                .mapNotNull { it.maxByOrNull(Attempt::timestamp) }
                .any { AdaptiveLearning.detectResponseSignals(it.response, selected).isNotEmpty() }
        } ?: false
        val reviewDue = unit != null &&
            checkpoint?.state == DiagnosticCheckpointState.COMPLETE &&
            (AdaptiveDiagnosticRoute.nextReview(eligibleAttempts, unit.id, now) ?: Long.MAX_VALUE) <= now

        val selected = when {
            dueExperience != null -> Selection(
                task = taskFor(dueExperience.issueType),
                reasonCode = AdaptiveReasonCode.PERSONAL_REPAIR_DUE,
                confidence = DiagnosticConfidence.LOW,
                focusSignal = issueSignal(dueExperience.issueType)
            )
            reviewDue -> Selection(
                task = "retrieval",
                reasonCode = AdaptiveReasonCode.LESSON_REVIEW_DUE,
                confidence = DiagnosticConfidence.LOW,
                focusSignal = null
            )
            unit == null -> Selection(
                task = null,
                reasonCode = AdaptiveReasonCode.COURSE_COMPLETE,
                confidence = DiagnosticConfidence.LOW,
                focusSignal = null
            )
            quizFailed -> Selection(
                task = "quiz",
                reasonCode = AdaptiveReasonCode.QUIZ_REPAIR,
                confidence = DiagnosticConfidence.LOW,
                focusSignal = WeaknessSignal.SITUATIONAL_INFERENCE
            )
            productionNeedsRepair -> Selection(
                task = "controlled",
                reasonCode = AdaptiveReasonCode.RESPONSE_REPAIR,
                confidence = DiagnosticConfidence.LOW,
                focusSignal = recurringWeakness?.signal
            )
            unitHistory.none { it.taskId == "retrieval" } -> Selection(
                task = "retrieval",
                reasonCode = AdaptiveReasonCode.FIRST_RETRIEVAL,
                confidence = DiagnosticConfidence.LOW,
                focusSignal = null
            )
            unitHistory.none { it.taskId == "reading" } -> Selection(
                task = "reading",
                reasonCode = AdaptiveReasonCode.NEXT_INPUT,
                confidence = DiagnosticConfidence.LOW,
                focusSignal = null
            )
            unitHistory.none { it.taskId == "listening" } -> Selection(
                task = "listening",
                reasonCode = AdaptiveReasonCode.NEXT_LISTENING,
                confidence = DiagnosticConfidence.LOW,
                focusSignal = WeaknessSignal.LISTENING_VOCABULARY
            )
            latestQuiz == null -> Selection(
                task = "quiz",
                reasonCode = AdaptiveReasonCode.NEXT_COMPREHENSION_CHECK,
                confidence = DiagnosticConfidence.LOW,
                focusSignal = null
            )
            unitHistory.none { it.taskId == "controlled" } -> Selection(
                task = "controlled",
                reasonCode = AdaptiveReasonCode.NEXT_CONTROLLED_PRACTICE,
                confidence = DiagnosticConfidence.LOW,
                focusSignal = null
            )
            checkpoint?.state == DiagnosticCheckpointState.NEEDS_PRODUCTION -> Selection(
                task = nextProductionTask(unitHistory, eligibleAttempts, recurringWeakness?.signal),
                reasonCode = AdaptiveReasonCode.NEXT_PRODUCTION,
                confidence = DiagnosticConfidence.LOW,
                focusSignal = recurringWeakness?.signal
            )
            recurringWeakness != null -> Selection(
                task = taskFor(recurringWeakness.signal),
                reasonCode = AdaptiveReasonCode.RECURRING_SIGNAL,
                confidence = confidenceFor(recurringWeakness.confidence),
                focusSignal = recurringWeakness.signal
            )
            else -> Selection(
                task = leastPracticedTask(unitHistory, eligibleAttempts, now),
                reasonCode = AdaptiveReasonCode.BALANCED_PRACTICE,
                confidence = DiagnosticConfidence.LOW,
                focusSignal = null
            )
        }
        val focusSignal = selected.focusSignal

        val context = dueExperience?.context
            ?: focusSignal?.let { signal ->
                weaknessProfile.top().firstOrNull { it.signal == signal }?.latestEvidence?.context
            }
            ?: selectContext(unitHistory, eligibleAttempts)
        val targetSignal = focusSignal
        val dialogueBriefId = OwnedDialogueLibrary.briefs
            .firstOrNull { brief ->
                brief.context == context &&
                    (targetSignal == null || targetSignal in brief.targetSignals)
            }?.id
            ?: OwnedDialogueLibrary.briefs.firstOrNull {
                targetSignal == null || targetSignal in it.targetSignals
            }?.id
        val taskOrder = orderFor(selected.task, unitHistory, eligibleAttempts, now)
        val reason = reasonFor(selected.reasonCode, selected.task, focusSignal, dueExperience, unit)

        return AdaptiveControlDecision(
            primaryTaskId = selected.task,
            actions = actionsFor(selected.task, reviewDue),
            reason = reason,
            confidence = selected.confidence,
            dialogueBriefId = dialogueBriefId,
            taskOrder = taskOrder,
            focusMinutes = selected.task?.let { focusMinutesFor(it, dueExperience != null || reviewDue) },
            recommendedUnitId = unit?.id,
            recommendedContext = context,
            reasonCode = selected.reasonCode,
            focusSignal = focusSignal
        )
    }

    private fun orderFor(
        primaryTask: String?,
        unitHistory: List<Attempt>,
        attempts: List<Attempt>,
        now: Long
    ): List<String> {
        val recentCounts = attempts
            .filter { now - it.timestamp <= THIRTY_DAYS_MILLIS && it.timestamp <= now }
            .groupingBy { it.taskId }
            .eachCount()
        val lessonTasks = unitHistory.map { it.taskId }.toSet()
        val followUp = taskSequence.filterNot { it == primaryTask }.sortedWith(
            compareBy<String> { if (it in lessonTasks) 1 else 0 }
                .thenBy { recentCounts[it] ?: 0 }
                .thenBy { taskSequence.indexOf(it) }
        )
        return listOfNotNull(primaryTask) + followUp
    }

    private fun actionsFor(task: String?, reviewDue: Boolean): List<AdaptiveAction> = buildList {
        if (reviewDue) add(AdaptiveAction.SPACE_RETRIEVAL)
        when (task) {
            "reading" -> add(AdaptiveAction.PRIORITIZE_READING)
            "listening" -> addAll(
                listOf(AdaptiveAction.PRIORITIZE_LISTENING, AdaptiveAction.DEFER_GENERIC_READING)
            )
            "quiz" -> add(AdaptiveAction.PRIORITIZE_COMPREHENSION)
            "controlled" -> add(AdaptiveAction.PRIORITIZE_FORMULATION)
            "interaction" -> addAll(
                listOf(AdaptiveAction.PRIORITIZE_INTERACTION, AdaptiveAction.ADD_CONVERSATION_VARIATION)
            )
            "retrieval" -> add(AdaptiveAction.PRIORITIZE_RETRIEVAL)
            "speaking" -> add(AdaptiveAction.PRIORITIZE_SPEAKING)
            "writing" -> add(AdaptiveAction.PRIORITIZE_WRITING)
        }
    }

    private fun reasonFor(
        reasonCode: AdaptiveReasonCode,
        task: String?,
        signal: WeaknessSignal?,
        experience: RealLifeExperience?,
        unit: CurriculumUnit?
    ): String = when (reasonCode) {
        AdaptiveReasonCode.PERSONAL_REPAIR_DUE ->
            "Fälliger persönlicher Reparaturschritt: ${experience?.issueType?.label}."
        AdaptiveReasonCode.LESSON_REVIEW_DUE ->
            "Wiederholung fällig: ${unit?.title ?: "Lektion"} wird mit aktivem Abruf gefestigt."
        AdaptiveReasonCode.QUIZ_REPAIR ->
            "Die Verständnisprüfung war noch nicht sicher; sie wird nach dem Lerninput erneut angeboten."
        AdaptiveReasonCode.RESPONSE_REPAIR ->
            "Ein lokales Antwortmuster braucht gezielte Überarbeitung, bevor die Lektion weitergeht."
        AdaptiveReasonCode.FIRST_RETRIEVAL ->
            "Beginne ohne Vorlage mit aktivem Abruf; danach führt der Plan zu Input, Hören und Anwendung."
        AdaptiveReasonCode.NEXT_INPUT ->
            "Der erste Abruf ist gespeichert; als Nächstes folgt der Lektionsinput."
        AdaptiveReasonCode.NEXT_LISTENING ->
            "Der Lektionsinput ist bearbeitet; als Nächstes folgt das integrierte Hörtraining."
        AdaptiveReasonCode.NEXT_COMPREHENSION_CHECK ->
            "Input und Hören sind bearbeitet; prüfe jetzt das Textverständnis."
        AdaptiveReasonCode.NEXT_CONTROLLED_PRACTICE ->
            "Das Verständnis ist gesichert; jetzt folgt angeleitete Formulierung."
        AdaptiveReasonCode.NEXT_PRODUCTION ->
            "Jetzt folgt eine eigenständige Anwendung in einem weiteren Aufgabentyp."
        AdaptiveReasonCode.RECURRING_SIGNAL ->
            "Wiederkehrendes Übungssignal (${signal?.label}); die nächste Aufgabe setzt dort an."
        AdaptiveReasonCode.BALANCED_PRACTICE ->
            "Der Kernpfad ist bearbeitet; als Nächstes wird ein zuletzt weniger geübter Bereich gewählt."
        AdaptiveReasonCode.COURSE_COMPLETE ->
            "Der geführte Kursweg ist abgeschlossen; aktuell ist keine Lektion verfügbar."
    }

    private fun leastRecentlyPracticedUnit(attempts: List<Attempt>): CurriculumUnit? =
        CurriculumCatalogue.coreUnits
            .filter { it.content != null }
            .minWithOrNull(
                compareBy<CurriculumUnit> { unit ->
                    attempts.filter { it.unitId == unit.id }.maxOfOrNull { it.timestamp } ?: Long.MIN_VALUE
                }.thenBy { it.stage }
            )

    private fun nextProductionTask(
        unitHistory: List<Attempt>,
        attempts: List<Attempt>,
        signal: WeaknessSignal?
    ): String {
        val practiced = unitHistory.map { it.taskId }.toSet()
        val preferred = signal?.let(::productionTaskFor)
        if (preferred != null && preferred !in practiced) return preferred
        val recentCounts = attempts
            .groupingBy { it.taskId }
            .eachCount()
        return productionTasks
            .filterNot { it in practiced }
            .minWithOrNull(compareBy<String> { recentCounts[it] ?: 0 }.thenBy { it })
            ?: "transfer"
    }

    private fun leastPracticedTask(
        unitHistory: List<Attempt>,
        attempts: List<Attempt>,
        now: Long
    ): String {
        val practiced = unitHistory.map { it.taskId }.toSet()
        val recentCounts = attempts
            .filter { it.timestamp <= now && now - it.timestamp <= THIRTY_DAYS_MILLIS }
            .groupingBy { it.taskId }
            .eachCount()
        return taskSequence
            .filterNot { it in practiced }
            .minWithOrNull(compareBy<String> { recentCounts[it] ?: 0 }.thenBy { taskSequence.indexOf(it) })
            ?: "retrieval"
    }

    private fun selectContext(unitHistory: List<Attempt>, attempts: List<Attempt>): ContextTrack =
        ContextTrack.values().minWithOrNull(
            compareBy<ContextTrack> { context ->
                unitHistory.count { it.context == context } * 2 + attempts.count { it.context == context }
            }.thenBy { it.ordinal }
        ) ?: ContextTrack.GENERAL

    private fun confidenceFor(confidence: WeaknessConfidence): DiagnosticConfidence = when (confidence) {
        WeaknessConfidence.HIGH -> DiagnosticConfidence.HIGH
        WeaknessConfidence.MEDIUM -> DiagnosticConfidence.MEDIUM
        WeaknessConfidence.LOW -> DiagnosticConfidence.LOW
    }

    private data class Selection(
        val task: String?,
        val reasonCode: AdaptiveReasonCode,
        val confidence: DiagnosticConfidence,
        val focusSignal: WeaknessSignal?
    )

    private fun focusMinutesFor(task: String, isDueRepair: Boolean): Int =
        if (isDueRepair || task == "interaction") 5 else 4

    private fun taskFor(issue: RealLifeIssueType): String = when (issue) {
        RealLifeIssueType.LISTENING -> "listening"
        RealLifeIssueType.RETRIEVAL, RealLifeIssueType.NATURALNESS,
        RealLifeIssueType.REGISTER, RealLifeIssueType.REPAIR -> "interaction"
        RealLifeIssueType.GRAMMAR_UNDER_PRESSURE -> "controlled"
    }

    private fun taskFor(signal: WeaknessSignal): String = when (signal) {
        WeaknessSignal.SOUND_RECOGNITION,
        WeaknessSignal.WORD_SEGMENTATION,
        WeaknessSignal.LISTENING_VOCABULARY,
        WeaknessSignal.SITUATIONAL_INFERENCE,
        WeaknessSignal.INFORMATION_RETENTION -> "listening"
        WeaknessSignal.ACTIVE_RETRIEVAL,
        WeaknessSignal.SENTENCE_FORMULATION,
        WeaknessSignal.GRAMMAR_UNDER_PRESSURE -> "controlled"
        WeaknessSignal.NATURALNESS,
        WeaknessSignal.REGISTER,
        WeaknessSignal.RESPONSE_INITIATION,
        WeaknessSignal.CONVERSATIONAL_REPAIR,
        WeaknessSignal.INTERACTION_MANAGEMENT,
        WeaknessSignal.IMPLIED_MEANING,
        WeaknessSignal.MULTI_SPEAKER_TRACKING -> "interaction"
    }

    private fun productionTaskFor(signal: WeaknessSignal): String = when (signal) {
        WeaknessSignal.NATURALNESS,
        WeaknessSignal.REGISTER,
        WeaknessSignal.RESPONSE_INITIATION,
        WeaknessSignal.CONVERSATIONAL_REPAIR,
        WeaknessSignal.INTERACTION_MANAGEMENT -> "interaction"
        WeaknessSignal.SENTENCE_FORMULATION,
        WeaknessSignal.GRAMMAR_UNDER_PRESSURE -> "writing"
        else -> "speaking"
    }

    private fun issueSignal(issue: RealLifeIssueType): WeaknessSignal = when (issue) {
        RealLifeIssueType.LISTENING -> WeaknessSignal.LISTENING_VOCABULARY
        RealLifeIssueType.RETRIEVAL -> WeaknessSignal.ACTIVE_RETRIEVAL
        RealLifeIssueType.GRAMMAR_UNDER_PRESSURE -> WeaknessSignal.GRAMMAR_UNDER_PRESSURE
        RealLifeIssueType.NATURALNESS -> WeaknessSignal.NATURALNESS
        RealLifeIssueType.REGISTER -> WeaknessSignal.REGISTER
        RealLifeIssueType.REPAIR -> WeaknessSignal.CONVERSATIONAL_REPAIR
    }

    private const val THIRTY_DAYS_MILLIS = 30L * 86_400_000L

    private val responseRepairTasks =
        setOf("retrieval", "controlled", "speaking", "interaction", "writing", "transfer")
}
