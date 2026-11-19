package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pillar_mastery")
data class PillarMasteryEntity(
    @PrimaryKey val pillarId: String,
    val masteryScore: Float,
    val confidenceSigma: Float,
    val targetDifficultyTier: Int = 2,
    val attemptsCount: Int,
    val correctCount: Int,
    val recentStreak: Int,
    val visualSlidesCompleted: Int,
    val lastPracticedAt: Long
)

@Entity(tableName = "error_patterns")
data class ErrorPatternEntity(
    @PrimaryKey val archetypeId: String,
    val errorCount: Int,
    val remediationCount: Int,
    val lastTriggeredAt: Long
)

@Entity(tableName = "srs_card_state")
data class SrsCardStateEntity(
    @PrimaryKey val cardId: String,
    val stabilityDays: Float,
    val difficulty: Float,
    val repetitions: Int,
    val lapses: Int,
    val nextReviewAt: Long,
    val lastQuality: Int
)

@Entity(tableName = "diagnostic_session_logs")
data class DiagnosticSessionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val pillarId: String,
    val difficulty: Int,
    val isCorrect: Boolean,
    val responseTimeMs: Long,
    val archetypeId: String
)

@Entity(tableName = "controller_config")
data class ControllerConfigEntity(
    @PrimaryKey val id: Int = 1,
    val strategyModeId: String,
    val dailyGoalMinutes: Int,
    val minutesToday: Int,
    val streakDays: Int,
    val totalXp: Int,
    val lastActiveEpochDay: Long,
    val diagnosticCalibrated: Boolean,
    val selectedContextTrackId: String = "GENERAL"
)

@Entity(tableName = "mistake_ledger")
data class MistakeLedgerEntity(
    @PrimaryKey val mistakeKey: String,
    val sourceItemId: String,
    val pillarId: String,
    val archetypeId: String,
    val contextSnippet: String,
    val wrongChoice: String,
    val correctForm: String,
    val explanationDe: String,
    val occurrences: Int,
    val remediationPasses: Int,
    val isResolved: Boolean,
    val lastSeenAt: Long
)

@Entity(tableName = "pruned_content")
data class PrunedContentEntity(
    @PrimaryKey val contentId: String,
    val contentType: String, // "QUIZ", "VOCAB", "SLIDE"
    val pillarId: String,
    val titleLabel: String,
    val reason: String,
    val minutesSaved: Int,
    val prunedAt: Long
)

@Entity(tableName = "ai_generated_slides")
data class AiGeneratedSlideEntity(
    @PrimaryKey val id: String,
    val pillarId: String,
    val headline: String,
    val subheadline: String,
    val diagramTypeName: String,
    val formulaBefore: String,
    val formulaAfter: String,
    val highlightRule: String,
    val keyInsight: String,
    val microExampleDe: String,
    val microExampleGloss: String,
    val commonTrap: String,
    val createdAt: Long
)

@Entity(tableName = "ai_generated_vocab")
data class AiGeneratedVocabEntity(
    @PrimaryKey val id: String,
    val pillarId: String,
    val expression: String,
    val grammarTag: String,
    val register: String,
    val meaningEn: String,
    val exampleDe: String,
    val trapWarning: String,
    val visualHint: String,
    val diagramTypeName: String,
    val createdAt: Long
)

@Entity(tableName = "ai_generated_quiz")
data class AiGeneratedQuizEntity(
    @PrimaryKey val id: String,
    val pillarId: String,
    val difficulty: Int,
    val archetypeId: String,
    val promptDe: String,
    val contextSnippet: String,
    val option0: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val correctIndex: Int,
    val explanationDe: String,
    val explanationEn: String,
    val trapNote: String,
    val createdAt: Long
)

@Entity(tableName = "repo_unit_progress")
data class RepoUnitProgressEntity(
    @PrimaryKey val unitId: String,
    val masteryScore: Float,
    val visualDone: Boolean,
    val puzzleDone: Boolean,
    val bookDone: Boolean,
    val situationDone: Boolean,
    val lastPracticedAt: Long
)
