package com.horizondev.habitbloom.screens.garden.domain

import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import kotlinx.datetime.LocalDate

/**
 * Encapsulates XP/Level progress together with a computed vitality snapshot.
 */
data class LevelProgress(
    val level: Int,
    val totalXp: Int,
    val xpInLevel: Int,
    val xpForCurrentLevel: Int,
    val xpToNextLevel: Int,
    val vitality: Float,
)

/**
 * Thresholds for reaching levels 2-5. Level 1 threshold is 0 by definition.
 */
private val LEVEL_THRESHOLDS = intArrayOf(0, 50, 150, 350, 700)

/**
 * Map level (1..5) to a UI growth stage.
 */
fun levelToGrowthStage(level: Int): FlowerGrowthStage = when (level.coerceIn(1, 5)) {
    1 -> FlowerGrowthStage.SEED
    2 -> FlowerGrowthStage.SPROUT
    3 -> FlowerGrowthStage.BUSH
    4 -> FlowerGrowthStage.BUD
    else -> FlowerGrowthStage.BLOOM
}

/**
 * Compute exponential moving average vitality and XP-based level progress from records.
 *
 * @param records Habit records for scheduled days only. Any date gaps are non-scheduled days.
 * @param daysPerWeek Number of scheduled days per week (1..7) used to select EMA alpha.
 * @return LevelProgress snapshot at the end of the provided history.
 */
fun calculateLevelProgress(
    records: List<UserHabitRecord>,
    daysPerWeek: Int,
    baseXp: Int = 10,
): LevelProgress {
    val sorted = records.sortedBy { it.date }

    // Brand-new habits: show full vitality at the start
    if (sorted.isEmpty()) {
        return LevelProgress(
            level = 1,
            totalXp = 0,
            xpInLevel = 0,
            xpForCurrentLevel = (LEVEL_THRESHOLDS[1] - LEVEL_THRESHOLDS[0]),
            xpToNextLevel = LEVEL_THRESHOLDS[1],
            vitality = 1.0f
        )
    }

    val alpha = selectAlpha(daysPerWeek)

    var vitality = 0.6f // forgiving default starting vitality
    var currentStreak = 0
    var totalXp = 0.0

    var lastDate: LocalDate? = null

    for (record in sorted) {
        // Only consider scheduled records provided by caller (should be up to today)
        val x = if (record.isCompleted) 1f else 0f

        // Update EMA vitality for this scheduled day
        vitality = alpha * x + (1 - alpha) * vitality

        // Streak across scheduled days
        currentStreak = if (record.isCompleted) currentStreak + 1 else 0

        if (record.isCompleted) {
            val streakMultiplier = 1.0 + (currentStreak - 1).coerceAtLeast(0) * 0.05
            val vitalityBonus = 0.75 + (vitality.toDouble() * 0.5) // 0.75..1.25 multiplier
            totalXp += baseXp * streakMultiplier * vitalityBonus
        }

        lastDate = record.date
    }

    val roundedXp = totalXp.toInt()
    val level = when {
        roundedXp >= LEVEL_THRESHOLDS[4] -> 5
        roundedXp >= LEVEL_THRESHOLDS[3] -> 4
        roundedXp >= LEVEL_THRESHOLDS[2] -> 3
        roundedXp >= LEVEL_THRESHOLDS[1] -> 2
        else -> 1
    }

    val floor = LEVEL_THRESHOLDS[level - 1]
    val ceil = if (level < 5) LEVEL_THRESHOLDS[level] else LEVEL_THRESHOLDS[4]
    val xpInLevel = (roundedXp - floor).coerceAtLeast(0)
    val xpForCurrentLevel = (ceil - floor).coerceAtLeast(0)
    val xpToNext = if (level < 5) (ceil - roundedXp).coerceAtLeast(0) else 0

    // If all records so far are completed, treat vitality as perfect
    if (sorted.all { it.isCompleted }) {
        vitality = 1.0f
    }

    return LevelProgress(
        level = level,
        totalXp = roundedXp,
        xpInLevel = xpInLevel,
        xpForCurrentLevel = xpForCurrentLevel,
        xpToNextLevel = xpToNext,
        vitality = (kotlin.math.round(vitality * 100) / 100f).coerceIn(0f, 1f)
    )
}

private fun selectAlpha(daysPerWeek: Int): Float {
    return when {
        daysPerWeek >= 5 -> 0.15f // daily-ish
        daysPerWeek >= 3 -> 0.10f // about 3-4x per week
        else -> 0.08f // light habits
    }
}


