package com.example.programme

enum class AdaptiveAction {
    PRIORITIZE_LISTENING,
    PRIORITIZE_COMPREHENSION,
    PRIORITIZE_RETRIEVAL,
    PRIORITIZE_FORMULATION,
    PRIORITIZE_INTERACTION,
    ADD_CONVERSATION_VARIATION,
    DEFER_GENERIC_READING
}

data class AdaptiveControlDecision(
    val primaryTaskId: String?,
    val actions: List<AdaptiveAction>,
    val reason: String,
    val confidence: DiagnosticConfidence,
    val dialogueBriefId: String?,
    val taskOrder: List<String>,
    val focusMinutes: Int?
)

/**
 * Single decision point for daily ordering. It can reorder existing work and
 * request variation; it never invents evidence or silently deletes required work.
 */
object AdaptiveControlEngine {
    fun decide(
        attempts: List<Attempt>,
        experiences: List<RealLifeExperience>,
        now: Long
    ): AdaptiveControlDecision {
        val diagnostic = LearnerDiagnosticEngine.analyze(attempts, experiences, now)
        val weakness = WeaknessEngine.profile(attempts, experiences, now).top(1).firstOrNull()
        val due = experiences.firstOrNull { it.nextReviewAt <= now }
        val finding = diagnostic.findings.firstOrNull { it.status == DiagnosticStatus.FRAGILE }
        val task = when {
            due != null -> taskFor(due.issueType)
            weakness != null -> taskFor(weakness.signal)
            finding != null -> taskFor(finding.dimension)
            else -> null
        }
        val context = weakness?.latestEvidence?.context ?: due?.context
        val targetSignal = weakness?.signal ?: due?.let { issueSignal(it.issueType) }
        val dialogueBriefId = OwnedDialogueLibrary.briefs
            .firstOrNull { brief ->
                (context == null || brief.context == context) &&
                    (targetSignal == null || targetSignal in brief.targetSignals)
            }?.id
            ?: OwnedDialogueLibrary.briefs.firstOrNull {
                targetSignal == null || targetSignal in it.targetSignals
            }?.id
        val actions = when (task) {
            "listening" -> listOf(AdaptiveAction.PRIORITIZE_LISTENING, AdaptiveAction.DEFER_GENERIC_READING)
            "quiz" -> listOf(AdaptiveAction.PRIORITIZE_COMPREHENSION)
            "controlled" -> listOf(AdaptiveAction.PRIORITIZE_FORMULATION)
            "interaction" -> listOf(
                AdaptiveAction.PRIORITIZE_INTERACTION,
                AdaptiveAction.ADD_CONVERSATION_VARIATION
            )
            "retrieval" -> listOf(AdaptiveAction.PRIORITIZE_RETRIEVAL)
            else -> emptyList()
        }
        val taskOrder = orderFor(task)
        return AdaptiveControlDecision(
            primaryTaskId = task,
            actions = actions,
            reason = due?.let { "Fälliger persönlicher Reparaturschritt: ${it.issueType.label}." }
                ?: weakness?.let { "Wiederkehrendes Signal: ${it.signal.label}." }
                ?: finding?.let { "Fragiles Signal: ${it.dimension.label}." }
                ?: "Noch kein belastbares Prioritätssignal; der Kernpfad bleibt aktiv.",
            confidence = when {
                weakness?.sampleCount ?: 0 >= 3 -> DiagnosticConfidence.HIGH
                weakness?.sampleCount ?: 0 >= 2 -> DiagnosticConfidence.MEDIUM
                task != null -> DiagnosticConfidence.LOW
                else -> DiagnosticConfidence.LOW
            },
            dialogueBriefId = dialogueBriefId,
            taskOrder = taskOrder,
            focusMinutes = task?.let { focusMinutesFor(it, due != null) }
        )
    }

    private fun orderFor(task: String?): List<String> = when (task) {
        "quiz" -> listOf("quiz", "reading", "retrieval", "listening", "interaction", "speaking", "controlled", "writing")
        "listening" -> listOf("listening", "quiz", "retrieval", "interaction", "speaking", "controlled", "writing", "reading")
        "controlled" -> listOf("retrieval", "controlled", "speaking", "writing", "interaction", "listening", "quiz", "reading")
        "interaction" -> listOf("listening", "interaction", "speaking", "retrieval", "controlled", "writing", "quiz", "reading")
        "retrieval" -> listOf("retrieval", "controlled", "speaking", "interaction", "writing", "listening", "quiz", "reading")
        else -> listOf("retrieval", "listening", "quiz", "controlled", "speaking", "interaction", "writing", "reading")
    }

    private fun focusMinutesFor(task: String, isDueRepair: Boolean): Int =
        if (isDueRepair || task == "interaction") 5 else 4

    private fun taskFor(issue: RealLifeIssueType): String = when (issue) {
        RealLifeIssueType.LISTENING -> "listening"
        RealLifeIssueType.RETRIEVAL, RealLifeIssueType.NATURALNESS,
        RealLifeIssueType.REGISTER, RealLifeIssueType.REPAIR -> "interaction"
        RealLifeIssueType.GRAMMAR_UNDER_PRESSURE -> "controlled"
    }

    private fun taskFor(dimension: DiagnosticDimension): String = when (dimension) {
        DiagnosticDimension.WORD_RECOGNITION,
        DiagnosticDimension.CONNECTED_SPEECH,
        DiagnosticDimension.INFORMATION_RETENTION,
        DiagnosticDimension.MULTI_SPEAKER_TRACKING,
        DiagnosticDimension.IMPLIED_MEANING -> "listening"
        DiagnosticDimension.SITUATIONAL_MEANING -> "quiz"
        DiagnosticDimension.EXPRESSION_RETRIEVAL,
        DiagnosticDimension.SENTENCE_FORMULATION,
        DiagnosticDimension.NATURAL_SPEAKING -> "controlled"
        DiagnosticDimension.SPONTANEOUS_RESPONSE,
        DiagnosticDimension.RESPONSE_INITIATION,
        DiagnosticDimension.INTERRUPTION_HANDLING,
        DiagnosticDimension.CONVERSATIONAL_REPAIR -> "interaction"
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

    private fun issueSignal(issue: RealLifeIssueType): WeaknessSignal = when (issue) {
        RealLifeIssueType.LISTENING -> WeaknessSignal.LISTENING_VOCABULARY
        RealLifeIssueType.RETRIEVAL -> WeaknessSignal.ACTIVE_RETRIEVAL
        RealLifeIssueType.GRAMMAR_UNDER_PRESSURE -> WeaknessSignal.GRAMMAR_UNDER_PRESSURE
        RealLifeIssueType.NATURALNESS -> WeaknessSignal.NATURALNESS
        RealLifeIssueType.REGISTER -> WeaknessSignal.REGISTER
        RealLifeIssueType.REPAIR -> WeaknessSignal.CONVERSATIONAL_REPAIR
    }
}
