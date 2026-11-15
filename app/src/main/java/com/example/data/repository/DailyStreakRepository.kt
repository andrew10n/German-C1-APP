package com.example.data.repository

import android.util.Log
import com.example.data.local.dao.DailyStreakDao
import com.example.data.local.entity.DailyStreakEntity
import com.example.data.model.DailyStreakState
import com.example.data.model.StreakDayStatus
import com.example.data.model.defaultMilestone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class DailyStreakRepository(
    private val streakDao: DailyStreakDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private val _streakState = MutableStateFlow(mapEntityToState(emptyStreak()))
    val streakState: StateFlow<DailyStreakState> = _streakState.asStateFlow()

    init {
        scope.launch {
            try {
                streakDao.observeStreak().collect { entity ->
                    _streakState.value = mapEntityToState(entity ?: emptyStreak())
                }
            } catch (e: Exception) {
                Log.e("DailyStreakRepo", "Error observing streak from Room", e)
            }
        }
    }

    // Override legacy entity defaults without creating a fictitious activity record.
    private fun emptyStreak() = DailyStreakEntity(
        currentStreak = 0,
        longestStreak = 0,
        lastActiveEpochDay = 0L,
        streakFreezesAvailable = 0,
        totalSessionsCompleted = 0,
        activeDaysBitmask = 0,
        lastActivityTimestamp = 0L
    )

    suspend fun recordActivity(activityName: String = "C1 Training"): DailyStreakState {
        val today = LocalDate.now()
        val todayEpoch = today.toEpochDay()
        val currentDayBit = 1 shl (today.dayOfWeek.value - 1)

        val existing = streakDao.getStreak() ?: emptyStreak()

        val daysDifference = todayEpoch - existing.lastActiveEpochDay

        val (newStreak, newLongest, newFreezes, wasFreezeUsed) = when {
            existing.totalSessionsCompleted == 0 -> {
                Quad(1, 1, existing.streakFreezesAvailable, false)
            }
            daysDifference == 0L -> {
                // Same day activity: streak maintained, session count increments
                Quad(existing.currentStreak, existing.longestStreak, existing.streakFreezesAvailable, false)
            }
            daysDifference == 1L -> {
                // Perfect consecutive day!
                val next = existing.currentStreak + 1
                val longest = maxOf(next, existing.longestStreak)
                // Bonus freeze awarded every 7-day milestone
                val bonusFreeze = if (next % 7 == 0) existing.streakFreezesAvailable + 1 else existing.streakFreezesAvailable
                Quad(next, longest, bonusFreeze, false)
            }
            daysDifference == 2L && existing.streakFreezesAvailable > 0 -> {
                // Freeze used! Preserves streak
                val next = existing.currentStreak + 1
                val longest = maxOf(next, existing.longestStreak)
                Quad(next, longest, existing.streakFreezesAvailable - 1, true)
            }
            else -> {
                // Inactivity gap > 1 day without freeze: resets to 1
                Quad(1, maxOf(1, existing.longestStreak), existing.streakFreezesAvailable, false)
            }
        }

        // Reset bitmask on Monday (new week) or append today
        val updatedBitmask = if (today.dayOfWeek == DayOfWeek.MONDAY && daysDifference > 0) {
            currentDayBit
        } else {
            existing.activeDaysBitmask or currentDayBit
        }

        val updatedEntity = existing.copy(
            currentStreak = newStreak,
            longestStreak = newLongest,
            lastActiveEpochDay = todayEpoch,
            streakFreezesAvailable = newFreezes,
            totalSessionsCompleted = existing.totalSessionsCompleted + 1,
            activeDaysBitmask = updatedBitmask,
            lastActivityTimestamp = System.currentTimeMillis()
        )

        streakDao.insertOrUpdate(updatedEntity)
        val state = mapEntityToState(updatedEntity)
        _streakState.value = state
        return state
    }

    private fun mapEntityToState(entity: DailyStreakEntity): DailyStreakState {
        val today = LocalDate.now()
        val todayEpoch = today.toEpochDay()
        val isPracticedToday = entity.totalSessionsCompleted > 0 && entity.lastActiveEpochDay == todayEpoch
        val todayDayOfWeek = today.dayOfWeek

        val weekDays = DayOfWeek.values().map { day ->
            val dayIndex = day.value // 1 (Mon) to 7 (Sun)
            val isCompleted = (entity.activeDaysBitmask and (1 shl (dayIndex - 1))) != 0
            val isToday = day == todayDayOfWeek
            val shortLabel = when (day) {
                DayOfWeek.MONDAY -> "Mo"
                DayOfWeek.TUESDAY -> "Di"
                DayOfWeek.WEDNESDAY -> "Mi"
                DayOfWeek.THURSDAY -> "Do"
                DayOfWeek.FRIDAY -> "Fr"
                DayOfWeek.SATURDAY -> "Sa"
                DayOfWeek.SUNDAY -> "So"
            }
            StreakDayStatus(
                dayOfWeek = day,
                shortLabel = shortLabel,
                isToday = isToday,
                isCompleted = isCompleted
            )
        }

        return DailyStreakState(
            currentStreak = entity.currentStreak,
            longestStreak = entity.longestStreak,
            isPracticedToday = isPracticedToday,
            streakFreezesAvailable = entity.streakFreezesAvailable,
            totalSessionsCompleted = entity.totalSessionsCompleted,
            weeklyDays = weekDays,
            nextMilestone = defaultMilestone(entity.currentStreak)
        )
    }

    private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
