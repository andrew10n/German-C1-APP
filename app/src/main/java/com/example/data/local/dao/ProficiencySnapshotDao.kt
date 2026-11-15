package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ProficiencySnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProficiencySnapshotDao {

    @Query("SELECT * FROM proficiency_snapshots ORDER BY timestamp ASC")
    fun getAllSnapshotsFlow(): Flow<List<ProficiencySnapshotEntity>>

    @Query("SELECT * FROM proficiency_snapshots ORDER BY timestamp ASC")
    suspend fun getAllSnapshotsDirect(): List<ProficiencySnapshotEntity>

    @Query("SELECT * FROM proficiency_snapshots ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestSnapshot(): ProficiencySnapshotEntity?

    @Query("SELECT COUNT(*) FROM proficiency_snapshots")
    suspend fun getSnapshotCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: ProficiencySnapshotEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(snapshots: List<ProficiencySnapshotEntity>)

    @Query("DELETE FROM proficiency_snapshots")
    suspend fun clearAll()
}
