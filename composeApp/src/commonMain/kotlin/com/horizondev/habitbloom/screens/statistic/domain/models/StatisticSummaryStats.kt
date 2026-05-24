package com.horizondev.habitbloom.screens.statistic.domain.models

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay

data class StatisticSummaryStats(
    val completedHabits: Int = 0,
    val longestStreak: Int = 0,
    val averageCompletionRate: Int = 0,
    val bestHabitName: String = "",
    val bestHabitCompletionRate: Int = 0,
    val timeOfDayCompletionRates: Map<TimeOfDay, Int> = TimeOfDay.entries.associateWith { 0 }
)
