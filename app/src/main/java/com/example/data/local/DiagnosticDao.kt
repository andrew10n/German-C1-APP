package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosticDao {
    @Query("SELECT * FROM pillar_mastery")
    fun observeAllPillarMastery(): Flow<List<PillarMasteryEntity>>

    @Query("SELECT * FROM pillar_mastery")
    suspend fun getAllPillarMasteryOnce(): List<PillarMasteryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPillarMastery(entity: PillarMasteryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllPillarMastery(entities: List<PillarMasteryEntity>)

    @Query("SELECT * FROM error_patterns")
    fun observeErrorPatterns(): Flow<List<ErrorPatternEntity>>

    @Query("SELECT * FROM error_patterns")
    suspend fun getErrorPatternsOnce(): List<ErrorPatternEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertErrorPattern(entity: ErrorPatternEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllErrorPatterns(entities: List<ErrorPatternEntity>)

    @Query("SELECT * FROM srs_card_state")
    fun observeSrsStates(): Flow<List<SrsCardStateEntity>>

    @Query("SELECT * FROM srs_card_state")
    suspend fun getAllSrsStatesOnce(): List<SrsCardStateEntity>

    @Query("SELECT * FROM srs_card_state WHERE cardId = :cardId")
    suspend fun getSrsState(cardId: String): SrsCardStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSrsState(entity: SrsCardStateEntity)

    @Query("SELECT * FROM diagnostic_session_logs ORDER BY timestamp DESC LIMIT 60")
    fun observeRecentLogs(): Flow<List<DiagnosticSessionLogEntity>>

    @Query("SELECT * FROM diagnostic_session_logs ORDER BY timestamp DESC LIMIT 60")
    suspend fun getRecentLogsOnce(): List<DiagnosticSessionLogEntity>

    @Insert
    suspend fun insertSessionLog(log: DiagnosticSessionLogEntity)

    @Query("SELECT * FROM controller_config WHERE id = 1")
    fun observeControllerConfig(): Flow<ControllerConfigEntity?>

    @Query("SELECT * FROM controller_config WHERE id = 1")
    suspend fun getControllerConfigOnce(): ControllerConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertControllerConfig(config: ControllerConfigEntity)

    // Mistake Repair Ledger
    @Query("SELECT * FROM mistake_ledger ORDER BY isResolved ASC, occurrences DESC, lastSeenAt DESC")
    fun observeMistakeLedger(): Flow<List<MistakeLedgerEntity>>

    @Query("SELECT * FROM mistake_ledger")
    suspend fun getMistakeLedgerOnce(): List<MistakeLedgerEntity>

    @Query("SELECT * FROM mistake_ledger WHERE mistakeKey = :key")
    suspend fun getMistakeByKey(key: String): MistakeLedgerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMistake(entity: MistakeLedgerEntity)

    // Pruned Low-ROI Content
    @Query("SELECT * FROM pruned_content ORDER BY prunedAt DESC")
    fun observePrunedContent(): Flow<List<PrunedContentEntity>>

    @Query("SELECT * FROM pruned_content")
    suspend fun getPrunedContentOnce(): List<PrunedContentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPrunedContent(entity: PrunedContentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllPrunedContent(entities: List<PrunedContentEntity>)

    @Query("DELETE FROM pruned_content WHERE contentId = :contentId")
    suspend fun restorePrunedContent(contentId: String)

    // Dynamic AI-Generated Curriculum
    @Query("SELECT * FROM ai_generated_slides ORDER BY createdAt DESC")
    fun observeAiSlides(): Flow<List<AiGeneratedSlideEntity>>

    @Query("SELECT * FROM ai_generated_slides")
    suspend fun getAiSlidesOnce(): List<AiGeneratedSlideEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiSlide(entity: AiGeneratedSlideEntity)

    @Query("SELECT * FROM ai_generated_vocab ORDER BY createdAt DESC")
    fun observeAiVocab(): Flow<List<AiGeneratedVocabEntity>>

    @Query("SELECT * FROM ai_generated_vocab")
    suspend fun getAiVocabOnce(): List<AiGeneratedVocabEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiVocab(entity: AiGeneratedVocabEntity)

    @Query("SELECT * FROM ai_generated_quiz ORDER BY createdAt DESC")
    fun observeAiQuiz(): Flow<List<AiGeneratedQuizEntity>>

    @Query("SELECT * FROM ai_generated_quiz")
    suspend fun getAiQuizOnce(): List<AiGeneratedQuizEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAiQuiz(entities: List<AiGeneratedQuizEntity>)

    @Query("SELECT * FROM repo_unit_progress")
    fun observeRepoUnitProgress(): Flow<List<RepoUnitProgressEntity>>

    @Query("SELECT * FROM repo_unit_progress")
    suspend fun getRepoUnitProgressOnce(): List<RepoUnitProgressEntity>

    @Query("SELECT * FROM repo_unit_progress WHERE unitId = :unitId")
    suspend fun getRepoUnitProgressById(unitId: String): RepoUnitProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRepoUnitProgress(entity: RepoUnitProgressEntity)

    @Query("DELETE FROM repo_unit_progress")
    suspend fun clearRepoUnitProgress()

    @Query("DELETE FROM diagnostic_session_logs")
    suspend fun clearSessionLogs()

    @Query("DELETE FROM srs_card_state")
    suspend fun clearSrsStates()

    @Query("DELETE FROM mistake_ledger")
    suspend fun clearMistakes()

    @Query("DELETE FROM pruned_content")
    suspend fun clearPrunedContent()
}
