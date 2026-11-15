package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.BaselineProfileDao
import com.example.data.local.dao.DailyStreakDao
import com.example.data.local.dao.ProficiencySnapshotDao
import com.example.data.local.dao.VocabularyItemDao
import com.example.data.local.entity.BaselineProfileEntity
import com.example.data.local.entity.DailyStreakEntity
import com.example.data.local.entity.ProficiencySnapshotEntity
import com.example.data.local.entity.VocabularyItemEntity
import com.example.programme.ProgrammeAttemptDao
import com.example.programme.ProgrammeAttemptEntity
import com.example.programme.RealLifeExperienceDao
import com.example.programme.RealLifeExperienceEntity
import com.example.programme.FeedbackChallengeDao
import com.example.programme.FeedbackChallengeEntity
import com.example.programme.HumanReviewDao
import com.example.programme.HumanReviewEntity
import com.example.programme.AssessmentSessionDao
import com.example.programme.AssessmentSessionEntity
import com.example.programme.ReviewAssignmentEntity
import com.example.programme.ReviewAuditEventEntity
import com.example.programme.ReassessmentLinkEntity
import com.example.programme.ReviewerWorkflowDao

@Database(
    entities = [
        BaselineProfileEntity::class,
        VocabularyItemEntity::class,
        ProficiencySnapshotEntity::class,
        DailyStreakEntity::class,
        ProgrammeAttemptEntity::class,
        RealLifeExperienceEntity::class,
        FeedbackChallengeEntity::class,
        HumanReviewEntity::class,
        AssessmentSessionEntity::class,
        ReviewAssignmentEntity::class,
        ReviewAuditEventEntity::class,
        ReassessmentLinkEntity::class
    ],
    version = 14,
    exportSchema = false
)
abstract class C1AppDatabase : RoomDatabase() {

    abstract fun baselineProfileDao(): BaselineProfileDao
    abstract fun vocabularyItemDao(): VocabularyItemDao
    abstract fun proficiencySnapshotDao(): ProficiencySnapshotDao
    abstract fun dailyStreakDao(): DailyStreakDao
    abstract fun programmeAttemptDao(): ProgrammeAttemptDao
    abstract fun realLifeExperienceDao(): RealLifeExperienceDao
    abstract fun feedbackChallengeDao(): FeedbackChallengeDao
    abstract fun humanReviewDao(): HumanReviewDao
    abstract fun assessmentSessionDao(): AssessmentSessionDao
    abstract fun reviewerWorkflowDao(): ReviewerWorkflowDao

    companion object {
        @Volatile
        private var INSTANCE: C1AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `vocabulary_items` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `germanCollocation` TEXT NOT NULL,
                        `meaningDe` TEXT NOT NULL,
                        `exampleSentence` TEXT NOT NULL,
                        `register` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `stage` INTEGER NOT NULL,
                        `easeFactor` REAL NOT NULL,
                        `intervalDays` INTEGER NOT NULL,
                        `repetitions` INTEGER NOT NULL,
                        `lastReviewedTimestamp` INTEGER,
                        `nextReviewTimestamp` INTEGER NOT NULL,
                        `consecutiveErrors` INTEGER NOT NULL,
                        `isMastered` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `proficiency_snapshots` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `periodLabel` TEXT NOT NULL,
                        `overallScore` INTEGER NOT NULL,
                        `functionalScore` INTEGER NOT NULL,
                        `listeningScore` INTEGER NOT NULL,
                        `speakingScore` INTEGER NOT NULL,
                        `readingScore` INTEGER NOT NULL,
                        `writingScore` INTEGER NOT NULL,
                        `grammarScore` INTEGER NOT NULL,
                        `vocabularyScore` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `daily_streaks` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `currentStreak` INTEGER NOT NULL,
                        `longestStreak` INTEGER NOT NULL,
                        `lastActiveEpochDay` INTEGER NOT NULL,
                        `streakFreezesAvailable` INTEGER NOT NULL,
                        `totalSessionsCompleted` INTEGER NOT NULL,
                        `activeDaysBitmask` INTEGER NOT NULL,
                        `lastActivityTimestamp` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `programme_attempts` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `unitId` TEXT NOT NULL,
                        `taskId` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `response` TEXT NOT NULL,
                        `source` TEXT NOT NULL,
                        `scoresJson` TEXT NOT NULL,
                        `assisted` INTEGER NOT NULL,
                        `unfamiliar` INTEGER NOT NULL,
                        `independentProduction` INTEGER NOT NULL,
                        `context` TEXT NOT NULL,
                        `errorsJson` TEXT NOT NULL,
                        `taskVersion` INTEGER NOT NULL DEFAULT 1,
                        `rubricVersion` INTEGER NOT NULL DEFAULT 1
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `real_life_experiences` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `createdAt` INTEGER NOT NULL,
                        `context` TEXT NOT NULL,
                        `situation` TEXT NOT NULL,
                        `heard` TEXT NOT NULL,
                        `intended` TEXT NOT NULL,
                        `said` TEXT NOT NULL,
                        `issueType` TEXT NOT NULL,
                        `reviewStage` INTEGER NOT NULL,
                        `nextReviewAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `real_life_experiences` ADD COLUMN `reviewResponse` TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `feedback_challenges` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `claimId` TEXT NOT NULL,
                        `learnerDecision` TEXT,
                        `correctedFeedback` TEXT,
                        `status` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `human_reviews` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `attemptId` TEXT NOT NULL,
                        `reviewerId` TEXT NOT NULL,
                        `reviewerName` TEXT NOT NULL,
                        `reviewerVerified` INTEGER NOT NULL,
                        `rubricVersion` INTEGER NOT NULL,
                        `scoresJson` TEXT NOT NULL,
                        `comment` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `assessment_sessions` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `kind` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `durationMinutes` INTEGER NOT NULL,
                        `tasksJson` TEXT NOT NULL,
                        `responsesJson` TEXT NOT NULL,
                        `currentIndex` INTEGER NOT NULL,
                        `completed` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS `review_assignments` (`id` TEXT NOT NULL PRIMARY KEY, `attemptId` TEXT NOT NULL, `reviewerId` TEXT NOT NULL, `reviewerName` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `dueAt` INTEGER, `completedAt` INTEGER)""")
                db.execSQL("""CREATE TABLE IF NOT EXISTS `review_audit_events` (`id` TEXT NOT NULL PRIMARY KEY, `assignmentId` TEXT NOT NULL, `reviewId` TEXT, `actorId` TEXT NOT NULL, `action` TEXT NOT NULL, `details` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)""")
                db.execSQL("""CREATE TABLE IF NOT EXISTS `reassessment_links` (`id` TEXT NOT NULL PRIMARY KEY, `sourceAttemptId` TEXT NOT NULL, `sourceReviewId` TEXT, `reassessmentUnitId` TEXT NOT NULL, `reassessmentTaskId` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `completedAt` INTEGER)""")
            }
        }

        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    UPDATE programme_attempts
                    SET source = 'SELF_REFLECTION',
                        scoresJson = '{}',
                        unfamiliar = 0,
                        independentProduction = 0
                    WHERE source = 'HUMAN_REVIEW'
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    UPDATE programme_attempts
                    SET unfamiliar = 0
                    WHERE source = 'SELF_REFLECTION' AND taskId LIKE 'assessment:%'
                    """.trimIndent()
                )
                db.execSQL("UPDATE human_reviews SET reviewerVerified = 0")
            }
        }

        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `feedback_challenges` ADD COLUMN `attemptId` TEXT")
                db.execSQL("ALTER TABLE `feedback_challenges` ADD COLUMN `unitId` TEXT")
                db.execSQL("ALTER TABLE `feedback_challenges` ADD COLUMN `taskId` TEXT")
                db.execSQL("ALTER TABLE `feedback_challenges` ADD COLUMN `context` TEXT")
                db.execSQL("ALTER TABLE `feedback_challenges` ADD COLUMN `responseExcerpt` TEXT")
                db.execSQL("ALTER TABLE `feedback_challenges` ADD COLUMN `claimType` TEXT")
                db.execSQL("ALTER TABLE `feedback_challenges` ADD COLUMN `proposedFeedback` TEXT")
                db.execSQL("ALTER TABLE `feedback_challenges` ADD COLUMN `createdAt` INTEGER")
                db.execSQL("ALTER TABLE `feedback_challenges` ADD COLUMN `updatedAt` INTEGER")
            }
        }

        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE `vocabulary_items` ADD COLUMN `sourceUnitId` TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_vocabulary_items_nextReviewTimestamp` ON `vocabulary_items` (`nextReviewTimestamp`)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_vocabulary_items_sourceUnitId` ON `vocabulary_items` (`sourceUnitId`)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_programme_attempts_unitId_timestamp` ON `programme_attempts` (`unitId`, `timestamp`)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_programme_attempts_timestamp` ON `programme_attempts` (`timestamp`)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_real_life_experiences_nextReviewAt` ON `real_life_experiences` (`nextReviewAt`)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_real_life_experiences_createdAt` ON `real_life_experiences` (`createdAt`)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_assessment_sessions_completed_updatedAt` ON `assessment_sessions` (`completed`, `updatedAt`)"
                )
            }
        }

        fun getDatabase(context: Context): C1AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    C1AppDatabase::class.java,
                    "c1_deutsch_database"
                )
                    .addMigrations(
                        MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4,
                        MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7,
                        MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11,
                        MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14
                    )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
