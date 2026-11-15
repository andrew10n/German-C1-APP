package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveErrorRecyclingTest {
    @Test
    fun repeatedGrammarErrorCreatesSpacedObjectiveLinkedSupportPlan() {
        val unit = CurriculumCatalogue.units.first { it.content != null }
        val response = "Ich glaube, dass ich komme heute."
        val attempts = listOf(
            Attempt("a1", unit.id, "writing", 1_000L, response, EvidenceSource.SELF_REFLECTION, emptyMap(), false, false, true, ContextTrack.GENERAL, listOf("subordinate-verb-position"), 1, 1),
            Attempt("a2", unit.id, "writing", 2_000L, response, EvidenceSource.SELF_REFLECTION, emptyMap(), false, false, true, ContextTrack.GENERAL, listOf("subordinate-verb-position"), 1, 1)
        )

        val recommendation = AdaptiveLearning.recommend(unit, attempts, 3_000L)
        val plan = recommendation.recyclingPlans.first { it.code == "subordinate-verb-position" }
        assertEquals(2, plan.repeatedCount)
        assertEquals(AdaptiveDifficulty.SUPPORT, plan.difficulty)
        assertTrue(plan.nextReviewAt > 3_000L)
        assertTrue(recommendation.difficulty == AdaptiveDifficulty.SUPPORT)
    }
}
