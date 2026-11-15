package com.example

import android.app.Application
import android.util.Log
import com.example.data.local.C1AppDatabase
import com.example.data.repository.C1LearningRepository
import com.example.data.repository.DiagnosticRepository
import com.example.data.repository.ProficiencyProgressRepository
import com.example.data.repository.VocabularySrsRepository

/**
 * Main application class responsible for initializing core singletons,
 * pre-warming the Room database before UI composition begins, and providing
 * dependency injection instances to ViewModels and activities.
 */
class MainApplication : Application() {

    // Lazy Room Database Singleton instance
    val database: C1AppDatabase by lazy {
        C1AppDatabase.getDatabase(this)
    }

    val programmeRepository: com.example.programme.ProgrammeRepository by lazy {
        com.example.programme.ProgrammeRepository(database.programmeAttemptDao())
    }

    val realLifeExperienceRepository: com.example.programme.RealLifeExperienceRepository by lazy {
        com.example.programme.RealLifeExperienceRepository(database.realLifeExperienceDao())
    }

    val feedbackChallengeRepository: com.example.programme.FeedbackChallengeRepository by lazy {
        com.example.programme.FeedbackChallengeRepository(database.feedbackChallengeDao())
    }

    val humanReviewRepository: com.example.programme.HumanReviewRepository by lazy {
        com.example.programme.HumanReviewRepository(database.humanReviewDao())
    }

    val assessmentSessionRepository: com.example.programme.AssessmentSessionRepository by lazy {
        com.example.programme.AssessmentSessionRepository(database.assessmentSessionDao())
    }

    val externalListeningPracticeRepository: com.example.programme.ExternalListeningPracticeRepository by lazy {
        com.example.programme.ExternalListeningPracticeRepository.create(this)
    }

    val practiceDraftRepository: com.example.programme.PracticeDraftRepository by lazy {
        com.example.programme.PracticeDraftRepository.create(this)
    }

    val bookAudioSelectionRepository: com.example.programme.BookAudioSelectionRepository by lazy {
        com.example.programme.BookAudioSelectionRepository.create(this)
    }

    val reviewerWorkflowRepository: com.example.programme.ReviewerWorkflowRepository by lazy {
        com.example.programme.ReviewerWorkflowRepository(database.reviewerWorkflowDao())
    }

    // Lazy DAOs for dependency injection
    val baselineProfileDao by lazy { database.baselineProfileDao() }
    val vocabularyItemDao by lazy { database.vocabularyItemDao() }
    val proficiencySnapshotDao by lazy { database.proficiencySnapshotDao() }
    val dailyStreakDao by lazy { database.dailyStreakDao() }

    // Lazy Repositories for ViewModel DI
    val learningRepository: C1LearningRepository by lazy {
        C1LearningRepository()
    }

    val diagnosticRepository: DiagnosticRepository by lazy {
        DiagnosticRepository(baselineProfileDao)
    }

    val vocabularySrsRepository: VocabularySrsRepository by lazy {
        VocabularySrsRepository(vocabularyItemDao)
    }

    val proficiencyProgressRepository: ProficiencyProgressRepository by lazy {
        ProficiencyProgressRepository(
            proficiencySnapshotDao,
            baselineProfileDao,
            vocabularyItemDao
        )
    }

    val dailyStreakRepository: com.example.data.repository.DailyStreakRepository by lazy {
        com.example.data.repository.DailyStreakRepository(dailyStreakDao)
    }

    val speakingPracticeRepository: com.example.data.repository.SpeakingPracticeRepository by lazy {
        com.example.data.repository.SpeakingPracticeRepository()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Pre-warm Room database instance and schema before UI composition starts
        preWarmDatabase()
    }

    private fun preWarmDatabase() {
        try {
            // Eagerly instantiate the RoomDatabase singleton on the app process start
            val db = database
            Log.d(TAG, "C1AppDatabase singleton initialized successfully in MainApplication")

            // Asynchronously pre-warm the SQLite disk connection and run any pending migrations
            // on a background thread so the first UI composition reads instantly without blocking the main thread.
            Thread {
                try {
                    db.openHelper.writableDatabase
                    Log.d(TAG, "Room SQLite database connection pre-warmed successfully on background thread")
                } catch (e: Exception) {
                    Log.e(TAG, "Room background connection pre-warm encountered an error (deferred)", e)
                }
            }.apply {
                name = "RoomPreWarmThread"
                isDaemon = true
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Room database instance in MainApplication", e)
        }
    }

    companion object {
        private const val TAG = "MainApplication"

        lateinit var instance: MainApplication
            private set
    }
}
