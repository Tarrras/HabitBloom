package com.horizondev.habitbloom.screens.habits.domain.models

data class HabitStreak(
    val userHabitId: Long,
    val habitName: String,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
)
