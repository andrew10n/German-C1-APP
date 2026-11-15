package com.example.programme

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Index
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class RealLifeIssueType(val label: String) {
    LISTENING("Nicht verstanden"),
    RETRIEVAL("Wort oder Satz fehlte"),
    GRAMMAR_UNDER_PRESSURE("Grammatik unter Druck"),
    NATURALNESS("Klang unnatürlich"),
    REGISTER("Formell oder informell unsicher"),
    REPAIR("Nicht gut nachfragen oder reparieren")
}

data class RealLifeExperience(
    val id: String,
    val createdAt: Long,
    val context: ContextTrack,
    val situation: String,
    val heard: String,
    val intended: String,
    val said: String,
    val issueType: RealLifeIssueType,
    val reviewStage: Int,
    val nextReviewAt: Long,
    val reviewResponse: String = ""
)

@Entity(
    tableName = "real_life_experiences",
    indices = [Index(value = ["nextReviewAt"]), Index(value = ["createdAt"])]
)
data class RealLifeExperienceEntity(
    @androidx.room.PrimaryKey val id: String,
    val createdAt: Long,
    val context: String,
    val situation: String,
    val heard: String,
    val intended: String,
    val said: String,
    val issueType: String,
    val reviewStage: Int,
    val nextReviewAt: Long,
    val reviewResponse: String
)

@Dao
interface RealLifeExperienceDao {
    @Query("SELECT * FROM real_life_experiences ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<RealLifeExperienceEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(experience: RealLifeExperienceEntity)

    @Query("DELETE FROM real_life_experiences")
    suspend fun deleteAll()

    @Query("UPDATE real_life_experiences SET reviewStage = :reviewStage, nextReviewAt = :nextReviewAt, reviewResponse = :reviewResponse WHERE id = :id")
    suspend fun updateReview(id: String, reviewStage: Int, nextReviewAt: Long, reviewResponse: String)
}

class RealLifeExperienceRepository(private val dao: RealLifeExperienceDao) {
    val experiences: Flow<List<RealLifeExperience>> = dao.observeAll().map { rows ->
        rows.map { it.toModel() }
    }

    suspend fun record(experience: RealLifeExperience) {
        dao.insert(experience.toEntity())
    }

    suspend fun deleteAll() = dao.deleteAll()

    suspend fun completeReview(experience: RealLifeExperience, response: String, now: Long) {
        val nextStage = (experience.reviewStage + 1).coerceAtMost(REVIEW_INTERVALS_DAYS.lastIndex)
        val intervalDays = REVIEW_INTERVALS_DAYS[nextStage]
        dao.updateReview(
            id = experience.id,
            reviewStage = nextStage,
            nextReviewAt = now + intervalDays * DAY_MILLIS,
            reviewResponse = response.trim().take(500)
        )
    }

    private fun RealLifeExperience.toEntity() = RealLifeExperienceEntity(
        id = id,
        createdAt = createdAt,
        context = context.name,
        situation = situation,
        heard = heard,
        intended = intended,
        said = said,
        issueType = issueType.name,
        reviewStage = reviewStage,
        nextReviewAt = nextReviewAt,
        reviewResponse = reviewResponse
    )

    private fun RealLifeExperienceEntity.toModel() = RealLifeExperience(
        id = id,
        createdAt = createdAt,
        context = ContextTrack.valueOf(context),
        situation = situation,
        heard = heard,
        intended = intended,
        said = said,
        issueType = RealLifeIssueType.valueOf(issueType),
        reviewStage = reviewStage,
        nextReviewAt = nextReviewAt,
        reviewResponse = reviewResponse
    )

    companion object {
        val REVIEW_INTERVALS_DAYS = listOf(1L, 3L, 7L, 21L)
        const val DAY_MILLIS = 86_400_000L
    }
}
