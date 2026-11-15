package com.example.programme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking

class HumanReviewRepositoryTest {
    @Test
    fun reviewerSubmissionRequiresEveryRubricDimension() {
        val review = HumanReviewSubmission(
            id = "review-1",
            attemptId = "attempt-1",
            reviewer = HumanReviewer("reviewer-1", "Coach", verified = false),
            rubricVersion = 1,
            scores = Dimension.values().associateWith { 3 },
            comment = "Begründung",
            createdAt = 1
        )

        assertEquals(10, review.scores.size)
        assertEquals(false, review.reviewer.verified)
    }

    @Test
    fun localRepositoryRejectsSelfAssertedReviewerVerification() = runBlocking {
        val dao = InMemoryHumanReviewDao()
        val repository = HumanReviewRepository(dao)
        val review = HumanReviewSubmission(
            id = "review-verified",
            attemptId = "attempt-1",
            reviewer = HumanReviewer("reviewer-1", "Coach", verified = true),
            rubricVersion = 1,
            scores = Dimension.values().associateWith { 3 },
            comment = "Begründung",
            createdAt = 1
        )

        var rejected = false
        try {
            repository.record(review)
        } catch (_: IllegalArgumentException) {
            rejected = true
        }

        assertTrue(rejected)
        assertTrue(dao.rows.value.isEmpty())
    }

    @Test
    fun localReviewMustIncludeAnAttemptIdentifier() = runBlocking {
        val dao = InMemoryHumanReviewDao()
        val repository = HumanReviewRepository(dao)
        val review = HumanReviewSubmission(
            id = "review-unlinked",
            attemptId = "",
            reviewer = HumanReviewer("reviewer-1", "Coach", verified = false),
            rubricVersion = 1,
            scores = Dimension.values().associateWith { 3 },
            comment = "",
            createdAt = 1
        )

        var rejected = false
        try {
            repository.record(review)
        } catch (_: IllegalArgumentException) {
            rejected = true
        }

        assertTrue(rejected)
        assertTrue(dao.rows.value.isEmpty())
    }

    private class InMemoryHumanReviewDao : HumanReviewDao {
        val rows = MutableStateFlow<List<HumanReviewEntity>>(emptyList())

        override fun observeAll(): Flow<List<HumanReviewEntity>> = rows

        override suspend fun insert(review: HumanReviewEntity) {
            rows.value = rows.value + review
        }

        override suspend fun deleteAll() {
            rows.value = emptyList()
        }
    }
}
