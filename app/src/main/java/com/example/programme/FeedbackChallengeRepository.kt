package com.example.programme

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Entity(tableName = "feedback_challenges")
data class FeedbackChallengeEntity(
    @androidx.room.PrimaryKey val id: String,
    val claimId: String,
    val learnerDecision: String?,
    val correctedFeedback: String?,
    val status: String,
    val attemptId: String?,
    val unitId: String?,
    val taskId: String?,
    val context: String?,
    val responseExcerpt: String?,
    val claimType: String?,
    val proposedFeedback: String?,
    val createdAt: Long?,
    val updatedAt: Long?
)

@Dao
interface FeedbackChallengeDao {
    @Query("SELECT * FROM feedback_challenges ORDER BY id ASC")
    fun observeAll(): Flow<List<FeedbackChallengeEntity>>

    @Query("SELECT status FROM feedback_challenges WHERE id = :id")
    suspend fun findStatus(id: String): String?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(challenge: FeedbackChallengeEntity)

    @Query("DELETE FROM feedback_challenges")
    suspend fun deleteAll()

    @Query("UPDATE feedback_challenges SET status = :status, correctedFeedback = :correctedFeedback, updatedAt = :updatedAt WHERE id = :id AND status = 'OPEN'")
    suspend fun resolve(id: String, status: String, correctedFeedback: String?, updatedAt: Long): Int
}

class FeedbackChallengeRepository(private val dao: FeedbackChallengeDao) {
    val challenges: Flow<List<FeedbackChallenge>> = dao.observeAll().map { rows ->
        rows.map {
            FeedbackChallenge(
                claimId = it.claimId,
                learnerDecision = it.learnerDecision,
                correctedFeedback = it.correctedFeedback,
                status = FeedbackChallengeStatus.valueOf(it.status),
                id = it.id,
                attemptId = it.attemptId.orEmpty(),
                unitId = it.unitId.orEmpty(),
                taskId = it.taskId.orEmpty(),
                context = it.context?.let(ContextTrack::valueOf) ?: ContextTrack.GENERAL,
                responseExcerpt = it.responseExcerpt.orEmpty(),
                claimType = it.claimType.orEmpty(),
                proposedFeedback = it.proposedFeedback.orEmpty(),
                createdAt = it.createdAt ?: 0L,
                updatedAt = it.updatedAt ?: it.createdAt ?: 0L
            )
        }
    }

    suspend fun record(challenge: FeedbackChallenge) {
        require(challenge.id.isNotBlank()) { "Feedback challenge requires an immutable ID." }
        require(challenge.status == FeedbackChallengeStatus.OPEN) {
            "A new feedback challenge must start open."
        }
        require(challenge.attemptId.isNotBlank() && challenge.unitId.isNotBlank() && challenge.taskId.isNotBlank()) {
            "Feedback challenge must link to its source attempt."
        }
        dao.insert(
            FeedbackChallengeEntity(
                id = challenge.id,
                claimId = challenge.claimId,
                learnerDecision = challenge.learnerDecision,
                correctedFeedback = challenge.correctedFeedback,
                status = challenge.status.name,
                attemptId = challenge.attemptId,
                unitId = challenge.unitId,
                taskId = challenge.taskId,
                context = challenge.context.name,
                responseExcerpt = challenge.responseExcerpt,
                claimType = challenge.claimType,
                proposedFeedback = challenge.proposedFeedback,
                createdAt = challenge.createdAt,
                updatedAt = challenge.updatedAt
            )
        )
    }

    suspend fun resolve(
        challengeId: String,
        status: FeedbackChallengeStatus,
        correctedFeedback: String?,
        now: Long
    ) {
        require(status != FeedbackChallengeStatus.OPEN) {
            "Eine abgeschlossene Feedback-Herausforderung benötigt einen Ergebnisstatus."
        }
        val currentStatus = dao.findStatus(challengeId)
            ?: throw IllegalArgumentException("Feedback-Herausforderung wurde nicht gefunden.")
        require(currentStatus == FeedbackChallengeStatus.OPEN.name) {
            "Feedback-Herausforderung wurde bereits abgeschlossen."
        }
        val updated = dao.resolve(
            challengeId,
            status.name,
            correctedFeedback?.trim()?.take(2_000),
            now
        )
        check(updated == 1) { "Feedback-Herausforderung konnte nicht atomar abgeschlossen werden." }
    }

    suspend fun deleteAll() = dao.deleteAll()
}
