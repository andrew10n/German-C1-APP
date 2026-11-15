package com.example.data.model

import java.time.DayOfWeek
import java.time.LocalDate

data class StreakDayStatus(
    val dayOfWeek: DayOfWeek,
    val shortLabel: String, // "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"
    val isToday: Boolean,
    val isCompleted: Boolean
)

data class StreakMilestone(
    val targetDays: Int,
    val titleDe: String,
    val rewardDescriptionDe: String,
    val badgeIconName: String,
    val progressFraction: Float // 0.0 to 1.0
)

data class DailyStreakState(
    val currentStreak: Int = 1,
    val longestStreak: Int = 1,
    val isPracticedToday: Boolean = true,
    val streakFreezesAvailable: Int = 1,
    val totalSessionsCompleted: Int = 1,
    val weeklyDays: List<StreakDayStatus> = emptyList(),
    val nextMilestone: StreakMilestone = defaultMilestone(1),
    val motivationalQuoteDe: String = "Kontinuität schlägt Intensität: 18 Minuten täglich verankern C1-Strukturen dauerhaft im Sprachzentrum."
)

fun defaultMilestone(currentStreak: Int): StreakMilestone {
    return when {
        currentStreak < 3 -> StreakMilestone(
            targetDays = 3,
            titleDe = "3 Tage Berliner Takt",
            rewardDescriptionDe = "+150 XP & Bronze Flammen-Status",
            badgeIconName = "local_fire_department",
            progressFraction = (currentStreak / 3f).coerceIn(0f, 1f)
        )
        currentStreak < 7 -> StreakMilestone(
            targetDays = 7,
            titleDe = "1 Woche U-Bahn Souveränität",
            rewardDescriptionDe = "+1 Streak-Freeze Schutz & Silber-Abzeichen",
            badgeIconName = "military_tech",
            progressFraction = ((currentStreak - 3) / 4f).coerceIn(0f, 1f)
        )
        currentStreak < 14 -> StreakMilestone(
            targetDays = 14,
            titleDe = "2 Wochen C1 Kontinuität",
            rewardDescriptionDe = "Goldenes C1-Sprint Abzeichen & Diplomatie-Bonus",
            badgeIconName = "workspace_premium",
            progressFraction = ((currentStreak - 7) / 7f).coerceIn(0f, 1f)
        )
        currentStreak < 30 -> StreakMilestone(
            targetDays = 30,
            titleDe = "30 Tage C1 Meisterschaft",
            rewardDescriptionDe = "Platin-Status & Unbegrenzte Latenz-Analysen",
            badgeIconName = "emoji_events",
            progressFraction = ((currentStreak - 14) / 16f).coerceIn(0f, 1f)
        )
        else -> StreakMilestone(
            targetDays = 100,
            titleDe = "100 Tage Muttersprachler-Niveau",
            rewardDescriptionDe = "Diamant C1 Legenden-Status",
            badgeIconName = "diamond",
            progressFraction = ((currentStreak - 30) / 70f).coerceIn(0f, 1f)
        )
    }
}
