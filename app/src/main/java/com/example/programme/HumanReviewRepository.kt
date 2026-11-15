package com.example.programme

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class HumanReviewer(
    val id: String,
    val displayName: String,
    val verified: Boolean
)

data class HumanReviewSubmission(
    val id: String,
    val attemptId: String,
    val reviewer: HumanReviewer,
    val rubricVersion: Int,
    val scores: Map<Dimension, Int>,
    val comment: String,
    val createdAt: Long
)

@Entity(tableName = "human_reviews")
data class HumanReviewEntity(
    @androidx.room.PrimaryKey val id: String,
    val attemptId: String,
    val reviewerId: String,
    val reviewerName: String,
    val reviewerVerified: Boolean,
    val rubricVersion: Int,
    val scoresJson: String,
    val comment: String,
    val createdAt: Long
)

@Dao
interface HumanReviewDao {
    @Query("SELECT * FROM human_reviews ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<HumanReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(review: HumanReviewEntity)

    @Query("DELETE FROM human_reviews")
    suspend fun deleteAll()
}

class HumanReviewRepository(private val dao: HumanReviewDao) {
    val reviews: Flow<List<HumanReviewSubmission>> = dao.observeAll().map { rows ->
        rows.map { it.toModel() }
    }

    suspend fun record(review: HumanReviewSubmission) {
        require(review.id.isNotBlank()) { "Review-ID ist erforderlich." }
        require(review.attemptId.isNotBlank()) { "Review muss mit einem Versuch verknüpft sein." }
        require(review.reviewer.id.isNotBlank()) { "Reviewer identity is required." }
        require(review.reviewer.displayName.isNotBlank()) { "Reviewer name is required." }
        require(!review.reviewer.verified) { "Local reviews cannot assert external identity verification." }
        require(review.rubricVersion > 0) { "Rubric version must be positive." }
        require(Dimension.values().all { review.scores[it] in 0..4 }) {
            "Every rubric dimension needs a score from 0 to 4."
        }
        dao.insert(review.toEntity())
    }

    suspend fun deleteAll() = dao.deleteAll()

    private fun HumanReviewSubmission.toEntity() = HumanReviewEntity(
        id = id,
        attemptId = attemptId,
        reviewerId = reviewer.id,
        reviewerName = reviewer.displayName,
        reviewerVerified = reviewer.verified,
        rubricVersion = rubricVersion,
        scoresJson = encodeScores(scores),
        comment = comment.trim().take(2_000),
        createdAt = createdAt
    )

    private fun HumanReviewEntity.toModel() = HumanReviewSubmission(
        id = id,
        attemptId = attemptId,
        reviewer = HumanReviewer(reviewerId, reviewerName, reviewerVerified),
        rubricVersion = rubricVersion,
        scores = decodeScores(scoresJson),
        comment = comment,
        createdAt = createdAt
    )

    private fun encodeScores(scores: Map<Dimension, Int>): String =
        scores.entries.joinToString(",") { "${it.key.name}:${it.value}" }

    private fun decodeScores(value: String): Map<Dimension, Int> =
        value.split(',').filter { it.isNotBlank() }.associate { entry ->
            val parts = entry.split(':', limit = 2)
            require(parts.size == 2) { "Invalid human review score encoding." }
            Dimension.valueOf(parts[0]) to parts[1].toInt()
        }
}
