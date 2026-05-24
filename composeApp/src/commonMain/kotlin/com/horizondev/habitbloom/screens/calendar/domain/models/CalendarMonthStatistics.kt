package com.horizondev.habitbloom.screens.calendar.domain.models

data class CalendarMonthStatistics(
    val totalHabits: Int = 0,
    val completedHabits: Int = 0,
    val completionRate: Float = 0f,
    val longestStreak: Int = 0
)
