package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RetrievalPolicyTest {
    @Test
    fun adaptiveScheduleUsesQualityAndHistoryInsteadOfFixedCheckpointSpacing() {
        val hardFirstRecall = RetrievalPolicy.nextSchedule(2, 0, 2.5f, 0)
        val easyFirstRecall = RetrievalPolicy.nextSchedule(3, 0, 2.5f, 0)
        val hardMatureRecall = RetrievalPolicy.nextSchedule(2, 8, 2.5f, 3)
        val easyMatureRecall = RetrievalPolicy.nextSchedule(3, 8, 2.5f, 3)

        assertEquals(1, hardFirstRecall.intervalDays)
        assertEquals(2, easyFirstRecall.intervalDays)
        assertTrue(easyMatureRecall.intervalDays > hardMatureRecall.intervalDays)
        assertEquals(4, easyMatureRecall.repetitions)
    }

    @Test
    fun failedRecallResetsRepetitionsAndKeepsShortRetry() {
        val schedule = RetrievalPolicy.nextSchedule(0, 21, 2.3f, 5)

        assertEquals(1, schedule.intervalDays)
        assertEquals(0, schedule.repetitions)
        assertTrue(schedule.easeFactor < 2.3f)
    }
}
