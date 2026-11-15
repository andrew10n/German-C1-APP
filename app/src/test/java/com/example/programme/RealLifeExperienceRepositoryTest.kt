package com.example.programme

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.C1AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RealLifeExperienceRepositoryTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val database = Room.inMemoryDatabaseBuilder(context, C1AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `experience is persisted and scheduled for tomorrow`() = runBlocking {
        val repository = RealLifeExperienceRepository(database.realLifeExperienceDao())
        val now = 1_800_000_000_000L
        val experience = RealLifeExperience(
            id = "cashier",
            createdAt = now,
            context = ContextTrack.GENERAL,
            situation = "Artikel zweimal gescannt",
            heard = "",
            intended = "Das wurde doppelt gescannt",
            said = "Das steht zwei mal hier",
            issueType = RealLifeIssueType.NATURALNESS,
            reviewStage = 0,
            nextReviewAt = now + RealLifeExperienceRepository.DAY_MILLIS
        )

        repository.record(experience)

        assertEquals(listOf(experience), repository.experiences.first())
        assertTrue(experience.nextReviewAt > experience.createdAt)
    }

    @Test
    fun `review advances through spaced intervals`() = runBlocking {
        val repository = RealLifeExperienceRepository(database.realLifeExperienceDao())
        val experience = RealLifeExperience(
            id = "supervisor",
            createdAt = 1_800_000_000_000L,
            context = ContextTrack.ENGINEERING,
            situation = "Messwert erklären",
            heard = "Woran könnte das liegen?",
            intended = "Eine vorsichtige technische Einschätzung geben",
            said = "Ich weiß nicht",
            issueType = RealLifeIssueType.RETRIEVAL,
            reviewStage = 0,
            nextReviewAt = 1_800_000_000_000L
        )
        repository.record(experience)

        repository.completeReview(experience, "Ich würde das gern noch einmal prüfen.", experience.createdAt)

        val updated = repository.experiences.first().single()
        assertEquals(1, updated.reviewStage)
        assertEquals("Ich würde das gern noch einmal prüfen.", updated.reviewResponse)
        assertEquals(
            experience.createdAt + 3 * RealLifeExperienceRepository.DAY_MILLIS,
            updated.nextReviewAt
        )
    }

    @Test
    fun `cashier experience gets a precise but non-punitive repair`() {
        val repair = RealLifeRepairEngine.create(
            RealLifeExperience(
                id = "cashier",
                createdAt = 1,
                context = ContextTrack.GENERAL,
                situation = "Artikel zweimal gescannt",
                heard = "",
                intended = "Überprüfung erbitten",
                said = "Das steht zwei mal hier",
                issueType = RealLifeIssueType.NATURALNESS,
                reviewStage = 0,
                nextReviewAt = 2
            )
        )

        assertTrue(repair.primaryPhrase.contains("zweimal gescannt"))
        assertTrue(repair.explanation.contains("verständlich"))
    }
}
