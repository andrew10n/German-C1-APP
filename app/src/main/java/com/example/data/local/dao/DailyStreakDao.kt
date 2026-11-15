package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.DailyStreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStreakDao {

    @Query("SELECT * FROM daily_streaks WHERE id = :id LIMIT 1")
    suspend fun getStreak(id: String = DailyStreakEntity.PRIMARY_STREAK_ID): DailyStreakEntity?

    @Query("SELECT * FROM daily_streaks WHERE id = :id LIMIT 1")
    fun observeStreak(id: String = DailyStreakEntity.PRIMARY_STREAK_ID): Flow<DailyStreakEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(streak: DailyStreakEntity)

    @Query("DELETE FROM daily_streaks WHERE id = :id")
    suspend fun resetStreak(id: String = DailyStreakEntity.PRIMARY_STREAK_ID)
}
