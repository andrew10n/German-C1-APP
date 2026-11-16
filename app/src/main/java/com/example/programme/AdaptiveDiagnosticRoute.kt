package com.example.programme

enum class DiagnosticCheckpointState {
    NEEDS_OBJECTIVE_CHECK,
    NEEDS_PRODUCTION,
    NEEDS_REMEDIATION,
    COMPLETE,
    UNAVAILABLE
}

data class DiagnosticCheckpoint(
    val state: DiagnosticCheckpointState,
    val completedProductionTasks: Int,
    val feedback: List<String>
)

/**
 * Advances the private practice route from objective quiz results and known, local text signals.
 * A completed checkpoint means practice requirements were met; it is not a proficiency rating.
 */
object AdaptiveDiagnosticRoute {
    private val productionTasks = setOf("speaking", "interaction", "writing", "transfer")

    fun evaluate(
        unit: CurriculumUnit,
        attempts: List<Attempt>,
        now: Long
    ): DiagnosticCheckpoint {
        if (unit.content == null || unit.stage !in 0..6) {
            return DiagnosticCheckpoint(DiagnosticCheckpointState.UNAVAILABLE, 0, emptyList())
        }

        val history = attempts.filter {
            it.unitId == unit.id && it.timestamp <= now && it.response.isNotBlank()
        }
        val latestQuiz = history.filter { it.taskId == "quiz" }.maxByOrNull { it.timestamp }
        if (latestQuiz == null) {
            return DiagnosticCheckpoint(DiagnosticCheckpointState.NEEDS_OBJECTIVE_CHECK, 0, emptyList())
        }
        if (latestQuiz.source != EvidenceSource.AUTOMATIC_QUIZ ||
            latestQuiz.scores[Dimension.COMPREHENSION] != 4
        ) {
            return DiagnosticCheckpoint(
                DiagnosticCheckpointState.NEEDS_REMEDIATION,
                0,
                listOf("Die Verständnisfrage braucht einen erneuten Abruf.")
            )
        }

        val latestProduction = history
            .filter { it.source == EvidenceSource.SELF_REFLECTION && it.taskId in productionTasks && !it.assisted }
            .groupBy { it.taskId }
            .values
            .mapNotNull { taskAttempts -> taskAttempts.maxByOrNull { it.timestamp } }
        val detectedErrors = latestProduction.flatMap { attempt ->
            AdaptiveLearning.detectResponseSignals(attempt.response, unit)
        }.distinctBy { it.code }
        if (detectedErrors.isNotEmpty()) {
            return DiagnosticCheckpoint(
                DiagnosticCheckpointState.NEEDS_REMEDIATION,
                latestProduction.size,
                detectedErrors.map { "${it.label}: ${it.remediation}" }
            )
        }
        if (latestProduction.size < 2) {
            return DiagnosticCheckpoint(
                DiagnosticCheckpointState.NEEDS_PRODUCTION,
                latestProduction.size,
                emptyList()
            )
        }
        return DiagnosticCheckpoint(DiagnosticCheckpointState.COMPLETE, latestProduction.size, emptyList())
    }

    fun isCheckpointComplete(unit: CurriculumUnit, attempts: List<Attempt>, now: Long): Boolean =
        evaluate(unit, attempts, now).state == DiagnosticCheckpointState.COMPLETE

    fun isUnlocked(unit: CurriculumUnit, attempts: List<Attempt>, now: Long): Boolean {
        val catalogue = CurriculumCatalogue.units.associateBy { it.id }
        fun ready(candidate: CurriculumUnit, visiting: Set<String>): Boolean {
            if (candidate.content == null || candidate.stage !in 1..6 || candidate.id in visiting) return false
            val priorStages = CurriculumCatalogue.coreUnits
                .filter { it.stage in 1 until candidate.stage }
                .map { it.id }
            return (candidate.prerequisites + priorStages).distinct().all { id ->
                val prerequisite = catalogue[id]
                prerequisite != null &&
                    isCheckpointComplete(prerequisite, attempts, now) &&
                    ready(prerequisite, visiting + candidate.id)
            }
        }
        return ready(unit, emptySet())
    }

    fun nextReview(attempts: List<Attempt>, unitId: String, now: Long): Long? {
        val unit = CurriculumCatalogue.units.firstOrNull { it.id == unitId } ?: return null
        val history = attempts.filter {
            it.unitId == unitId && it.timestamp <= now && it.response.isNotBlank()
        }
        val latestProduction = history
            .filter { it.source == EvidenceSource.SELF_REFLECTION && it.taskId in productionTasks && !it.assisted }
            .groupBy { it.taskId }
            .values
            .mapNotNull { taskAttempts -> taskAttempts.maxByOrNull { it.timestamp } }
        val unresolvedSignalAt = latestProduction
            .filter { AdaptiveLearning.detectResponseSignals(it.response, unit).isNotEmpty() }
            .maxOfOrNull { it.timestamp }
        if (unresolvedSignalAt != null) {
            return plusDays(unresolvedSignalAt, RetrievalPolicy.errorRetryDays.toLong())
        }

        val quizzes = history.filter { it.taskId == "quiz" && it.source == EvidenceSource.AUTOMATIC_QUIZ }
        val latestQuiz = quizzes.maxByOrNull { it.timestamp } ?: return null
        if (latestQuiz.scores[Dimension.COMPREHENSION] != 4) {
            return plusDays(latestQuiz.timestamp, RetrievalPolicy.errorRetryDays.toLong())
        }
        val latestRetrieval = history
            .filter { it.taskId == "retrieval" && it.source == EvidenceSource.SELF_REFLECTION && !it.assisted }
            .maxByOrNull { it.timestamp }
            ?.takeIf { it.timestamp > latestQuiz.timestamp }
        if (latestRetrieval != null) {
            if (AdaptiveLearning.detectResponseSignals(latestRetrieval.response, unit).isNotEmpty()) {
                return plusDays(latestRetrieval.timestamp, RetrievalPolicy.errorRetryDays.toLong())
            }
            val successfulReviews = history
                .filter {
                    it.taskId == "retrieval" &&
                        it.source == EvidenceSource.SELF_REFLECTION &&
                        !it.assisted &&
                        it.timestamp >= latestQuiz.timestamp &&
                        AdaptiveLearning.detectResponseSignals(it.response, unit).isEmpty()
                }
                .map { it.timestamp / MasteryEngine.DAY_MILLIS }
                .distinct()
                .size
            val successfulDays = quizzes
                .filter { it.scores[Dimension.COMPREHENSION] == 4 }
                .map { it.timestamp / MasteryEngine.DAY_MILLIS }
                .distinct()
                .size
            val intervalIndex = (successfulDays + successfulReviews - 1)
                .coerceIn(0, RetrievalPolicy.intervalsDays.lastIndex)
            return plusDays(
                latestRetrieval.timestamp,
                RetrievalPolicy.intervalsDays[intervalIndex].toLong()
            )
        }
        val successfulDays = quizzes
            .filter { it.scores[Dimension.COMPREHENSION] == 4 }
            .map { it.timestamp / MasteryEngine.DAY_MILLIS }
            .distinct()
            .size
        val intervalIndex = (successfulDays - 1).coerceIn(0, RetrievalPolicy.intervalsDays.lastIndex)
        return plusDays(latestQuiz.timestamp, RetrievalPolicy.intervalsDays[intervalIndex].toLong())
    }

    fun completedCoreUnits(attempts: List<Attempt>, now: Long): Int =
        CurriculumCatalogue.coreUnits.count { isCheckpointComplete(it, attempts, now) }

    fun completedAuthoredUnits(attempts: List<Attempt>, now: Long): Int =
        CurriculumCatalogue.units.count { isCheckpointComplete(it, attempts, now) }

    fun nextUnit(attempts: List<Attempt>, now: Long): CurriculumUnit? {
        val available = CurriculumCatalogue.coreUnits.filter { isUnlocked(it, attempts, now) }
        val remediation = available.firstOrNull { unit ->
            evaluate(unit, attempts, now).state == DiagnosticCheckpointState.NEEDS_REMEDIATION &&
                attempts.any { it.unitId == unit.id && it.timestamp <= now }
        }
        if (remediation != null) return remediation
        val due = available.mapNotNull { unit ->
            nextReview(attempts, unit.id, now)?.takeIf { it <= now }?.let { unit to it }
        }.minWithOrNull(compareBy<Pair<CurriculumUnit, Long>> { it.second }.thenBy { it.first.stage })
        if (due != null) return due.first
        return available.firstOrNull {
            !isCheckpointComplete(it, attempts, now)
        }
    }

    private fun plusDays(timestamp: Long, days: Long): Long {
        val offset = days * MasteryEngine.DAY_MILLIS
        return if (timestamp > Long.MAX_VALUE - offset) Long.MAX_VALUE else timestamp + offset
    }
}
