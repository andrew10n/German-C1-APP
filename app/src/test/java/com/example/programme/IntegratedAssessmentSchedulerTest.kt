package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IntegratedAssessmentSchedulerTest {
    @Test
    fun weeklyAssessmentIsDueAtWeekStartWhenNoPriorCompletionExists() {
        val now = 10L * IntegratedAssessmentScheduler.WEEK_MILLIS + 123L
        val schedule = IntegratedAssessmentScheduler.schedule(now)
        assertEquals(10L * IntegratedAssessmentScheduler.WEEK_MILLIS, schedule.dueAt)
        assertTrue(IntegratedAssessmentScheduler.isDue(now))
        assertNotNull(IntegratedAssessmentScheduler.createDueSession(now))
    }

    @Test
    fun weeklyAssessmentWaitsSevenDaysAfterCompletion() {
        val completed = 10L * IntegratedAssessmentScheduler.WEEK_MILLIS + 5_000L
        val beforeDue = completed + IntegratedAssessmentScheduler.WEEK_MILLIS - 1L
        val due = completed + IntegratedAssessmentScheduler.WEEK_MILLIS
        assertFalse(IntegratedAssessmentScheduler.isDue(beforeDue, completed))
        assertTrue(IntegratedAssessmentScheduler.isDue(due, completed))
        assertNull(IntegratedAssessmentScheduler.createDueSession(beforeDue, completed))
    }

    @Test
    fun integratedSessionContainsOnlyDeliveredTextEvidenceAndDeterministicVariantIds() {
        val first = AssessmentDeliveryEngine.createSession(AssessmentSessionKind.WEEKLY_INTEGRATED, 1_000L, 2)
        val second = AssessmentDeliveryEngine.createSession(AssessmentSessionKind.WEEKLY_INTEGRATED, 2_000L, 2)
        assertEquals(5, first.tasks.size)
        assertTrue(first.tasks.all { it.evidence == AssessmentEvidence.TEXT_RESPONSE })
        assertTrue(first.tasks.none { it.unfamiliar })
        assertEquals(first.tasks.map { it.variationId }, second.tasks.map { it.variationId })
        assertEquals(60, first.durationMinutes)
    }
}
