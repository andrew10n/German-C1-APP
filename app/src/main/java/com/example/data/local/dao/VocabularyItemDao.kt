package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.VocabularyItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyItemDao {

    @Query("SELECT * FROM vocabulary_items ORDER BY nextReviewTimestamp ASC")
    fun getAllVocabularyFlow(): Flow<List<VocabularyItemEntity>>

    @Query("SELECT * FROM vocabulary_items WHERE nextReviewTimestamp <= :currentTimestamp ORDER BY nextReviewTimestamp ASC")
    fun getDueVocabularyFlow(currentTimestamp: Long): Flow<List<VocabularyItemEntity>>

    @Query("SELECT * FROM vocabulary_items WHERE nextReviewTimestamp <= :currentTimestamp ORDER BY nextReviewTimestamp ASC")
    suspend fun getDueVocabularyDirect(currentTimestamp: Long): List<VocabularyItemEntity>

    @Query("SELECT * FROM vocabulary_items WHERE id = :id LIMIT 1")
    suspend fun getVocabularyById(id: String): VocabularyItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: VocabularyItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<VocabularyItemEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMissing(items: List<VocabularyItemEntity>): List<Long>

    @Query("DELETE FROM vocabulary_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM vocabulary_items WHERE nextReviewTimestamp <= :currentTimestamp")
    fun getDueCountFlow(currentTimestamp: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM vocabulary_items WHERE isMastered = 1")
    fun getMasteredCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM vocabulary_items")
    suspend fun getTotalCount(): Int
}
