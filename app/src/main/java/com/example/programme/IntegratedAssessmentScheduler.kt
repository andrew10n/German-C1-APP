package com.example.programme

/**
 * Scheduling contract for the weekly integrated checkpoint.
 * Protected here means deterministic selection from original in-app material, not cryptographic
 * secrecy. A production server must still protect answer keys and issue unseen variants remotely.
 */
data class WeeklyAssessmentSchedule(
    val weekStartUtc: Long,
    val dueAt: Long,
    val seed: Int,
    val sessionKind: AssessmentSessionKind = AssessmentSessionKind.WEEKLY_INTEGRATED
)

object IntegratedAssessmentScheduler {
    const val WEEK_MILLIS = 7L * 24L * 60L * 60L * 1_000L

    fun schedule(now: Long, lastCompletedAt: Long? = null): WeeklyAssessmentSchedule {
        val weekStart = Math.floorDiv(now, WEEK_MILLIS) * WEEK_MILLIS
        val dueAt = lastCompletedAt?.let { it + WEEK_MILLIS } ?: weekStart
        val dueWeek = Math.floorDiv(dueAt, WEEK_MILLIS)
        return WeeklyAssessmentSchedule(
            weekStartUtc = weekStart,
            dueAt = dueAt,
            seed = Math.floorMod(dueWeek.toInt(), CurriculumCatalogue.units.size)
        )
    }

    fun isDue(now: Long, lastCompletedAt: Long? = null): Boolean =
        now >= schedule(now, lastCompletedAt).dueAt

    fun createDueSession(now: Long, lastCompletedAt: Long? = null): AssessmentSession? {
        val planned = schedule(now, lastCompletedAt)
        if (now < planned.dueAt) return null
        return AssessmentDeliveryEngine.createSession(
            kind = planned.sessionKind,
            now = now,
            seed = planned.seed
        )
    }
}
