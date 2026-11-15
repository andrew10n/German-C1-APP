package com.example.programme.core

enum class DailyBudget(val minutes: Int) { SHORT(20), STANDARD(60), EXTENDED(90) }
enum class PlanBlockKind { READING, LISTENING_TTS, PRODUCTION, RETRIEVAL_REVIEW }

data class ErrorRecord(
    val id: String,
    val unitId: String,
    val dimension: RubricDimension,
    val observedForm: String,
    val correctedForm: String,
    val explanation: String,
    val lastSeenDay: Long
) {
    init {
        require(listOf(id, unitId, observedForm, correctedForm, explanation).all { it.isNotBlank() })
        require(lastSeenDay >= 0)
    }
}

/** References evidence, not a checkbox or self-rating. Only actual human-observed retrieval counts. */
class ReviewRecord(val unitId: String, val evidenceId: String, errorIds: List<String> = emptyList()) {
    val errorIds = frozen(errorIds)
}

class DueReview(val unitId: String, val dueDay: Long, errorIds: List<String>) {
    val errorIds = frozen(errorIds)
}

data class PlanBlock(
    val kind: PlanBlockKind,
    val minutes: Int,
    val unitId: String,
    val resourceId: String,
    val instruction: String
)

class DailyPlan(val day: Long, val budget: DailyBudget, blocks: List<PlanBlock>, dueReviews: List<DueReview>) {
    val blocks = frozen(blocks)
    /** Queue, not a promise that every due item fits today's budget. */
    val dueReviews = frozen(dueReviews)
    val plannedMinutes: Int get() = blocks.sumOf { it.minutes }
}

/** Caller retains review/error history; this class returns a plan without mutating that history. */
class DailyPlanner(units: List<CurriculumUnit> = CurriculumCatalog.units) {
    private val units = frozen(units)
    private val policy = ProgrammePolicy(this.units)
    private val intervals = listOf(1L, 3L, 7L, 14L, 30L)

    fun dueReviews(
        today: Long,
        evidence: List<ProductionEvidence>,
        reviews: List<ReviewRecord> = emptyList(),
        errors: List<ErrorRecord> = emptyList()
    ): List<DueReview> {
        require(today >= 0)
        require(evidence.map { it.id }.distinct().size == evidence.size)
        require(errors.map { it.id }.distinct().size == errors.size)
        val evidenceById = evidence.associateBy { it.id }
        fun usable(e: ProductionEvidence) = e.day <= today &&
            e.origin == EvidenceOrigin.OBSERVED_PRODUCTION && e.authority == AssessmentAuthority.HUMAN &&
            e.assistance == Assistance.NONE && e.independent
        val result = mutableListOf<DueReview>()
        for (unit in units.filter { it.status == ContentStatus.AUTHORED }) {
            val history = reviews.filter { it.unitId == unit.id }.mapNotNull { review ->
                evidenceById[review.evidenceId]?.takeIf { it.unitId == unit.id && usable(it) }
            }.distinctBy { it.id }.sortedWith(compareBy<ProductionEvidence> { it.day }.thenBy { it.id })
            val baseline = evidence.filter { it.unitId == unit.id && usable(it) && it.rubric.passes() }
                .minWithOrNull(compareBy<ProductionEvidence> { it.day }.thenBy { it.id })
            var due = baseline?.day?.plus(1)
            var streak = 0
            val countedTasks = mutableSetOf<String>()
            val countedDays = mutableSetOf<Long>()
            for (attempt in history) {
                // Baseline and early/repeated reviews cannot accelerate a spacing interval.
                val scheduled = due ?: attempt.day
                if (attempt.day < scheduled || attempt.taskId in countedTasks || attempt.day in countedDays) continue
                countedTasks.add(attempt.taskId)
                countedDays.add(attempt.day)
                if (attempt.rubric.passes()) {
                    streak = (streak + 1).coerceAtMost(intervals.lastIndex)
                    due = attempt.day + intervals[streak]
                } else {
                    streak = 0
                    due = attempt.day + 1
                }
            }
            val activeErrors = errors.filter { error ->
                if (error.unitId != unit.id || error.lastSeenDay > today) return@filter false
                val repairs = reviews.filter { error.id in it.errorIds && it.unitId == unit.id }
                    .mapNotNull { evidenceById[it.evidenceId] }
                    .filter { it.unitId == unit.id && usable(it) && it.day > error.lastSeenDay && it.rubric.passes() }
                    .distinctBy { it.taskId }
                repairs.map { it.day }.distinct().size < 2
            }
            val errorDue = activeErrors.minOfOrNull { it.lastSeenDay + 1 }
            val nextDue = listOfNotNull(due, errorDue).minOrNull()
            if (nextDue != null && nextDue <= today) result.add(DueReview(unit.id, nextDue,
                activeErrors.filter { it.lastSeenDay + 1 <= today }.map { it.id }.sorted()))
        }
        return frozen(result.sortedWith(compareBy<DueReview> { it.dueDay }
            .thenByDescending { it.errorIds.size }.thenBy { it.unitId }))
    }

    fun plan(
        today: Long,
        budget: DailyBudget,
        evidence: List<ProductionEvidence> = emptyList(),
        verifications: List<HumanVerification> = emptyList(),
        reviews: List<ReviewRecord> = emptyList(),
        errors: List<ErrorRecord> = emptyList()
    ): DailyPlan {
        val decisions = policy.evaluateAll(today, evidence, verifications)
        val available = units.filter {
            it.status == ContentStatus.AUTHORED && decisions.getValue(it.id).status != MasteryStatus.LOCKED
        }
        val queue = dueReviews(today, evidence, reviews, errors).filter { due -> available.any { it.id == due.unitId } }
        val focus = available.firstOrNull { decisions.getValue(it.id).status != MasteryStatus.MASTERED }
            ?: available.firstOrNull()
            ?: return DailyPlan(today, budget, emptyList(), queue)
        val content = requireNotNull(focus.content)
        val reviewUnit = queue.firstOrNull()?.let { due -> available.first { it.id == due.unitId } } ?: focus
        val reviewTask = requireNotNull(reviewUnit.content).tasks.first { it.kind == ActivityKind.DELAYED_REVIEW }
        val productiveKinds = listOf(ActivityKind.SPEAKING, ActivityKind.INTERACTION, ActivityKind.WRITING, ActivityKind.MEDIATION)
        val task = content.tasks.first { it.kind == productiveKinds[(today % 4).toInt()] }
        val minutes = when (budget) {
            DailyBudget.SHORT -> listOf(3, 3, 9, 5)
            DailyBudget.STANDARD -> listOf(9, 9, 27, 15)
            DailyBudget.EXTENDED -> listOf(12, 13, 45, 20)
        }
        return DailyPlan(today, budget, listOf(
            PlanBlock(PlanBlockKind.READING, minutes[0], focus.id,
                content.sources.first { it.kind == SourceKind.READING }.id,
                "Lesen; These, Beleg und Einschränkung ohne Abschreiben notieren."),
            PlanBlock(PlanBlockKind.LISTENING_TTS, minutes[1], focus.id,
                content.sources.first { it.kind == SourceKind.LISTENING_TTS }.id,
                "Synthetisches TTS hören, zunächst ohne Transkript; anschließend Aussagen prüfen."),
            PlanBlock(PlanBlockKind.PRODUCTION, minutes[2], focus.id, task.id,
                task.prompt + " Modell erst nach dem eigenen Versuch öffnen; Hilfen als solche erfassen."),
            PlanBlock(PlanBlockKind.RETRIEVAL_REVIEW, minutes[3], reviewUnit.id, reviewTask.id,
                if (queue.isEmpty()) "Kurzer Abruf ohne Vorlage; vor Tag 7 nur Übung, kein verzögerter Nachweis. " + reviewTask.prompt
                else "Ohne Vorlage abrufen; Fehler korrigieren und in einem neuen Beispiel verwenden. " + reviewTask.prompt)
        ), queue)
    }
}
