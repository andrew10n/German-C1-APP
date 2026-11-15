package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity storing persistent daily streak metrics, continuity history,
 * and freeze protection state for the gamified daily streak system.
 */
@Entity(tableName = "daily_streaks")
data class DailyStreakEntity(
    @PrimaryKey
    val id: String = PRIMARY_STREAK_ID,
    val currentStreak: Int = 1,
    val longestStreak: Int = 1,
    val lastActiveEpochDay: Long = 0L,
    val streakFreezesAvailable: Int = 1,
    val totalSessionsCompleted: Int = 1,
    val activeDaysBitmask: Int = 0, // Bitmask for Mon=1, Tue=2, Wed=4, Thu=8, Fri=16, Sat=32, Sun=64
    val lastActivityTimestamp: Long = System.currentTimeMillis()
) {
    companion object {
        const val PRIMARY_STREAK_ID = "primary_user_streak"
    }
}
