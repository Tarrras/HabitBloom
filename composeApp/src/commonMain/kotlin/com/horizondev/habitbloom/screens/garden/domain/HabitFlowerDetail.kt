package com.horizondev.habitbloom.screens.garden.domain

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class HabitFlowerDetail(
    val habitId: Long,
    val name: String,
    val description: String,
    val iconUrl: String?,
    val timeOfDay: TimeOfDay,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val reminderTime: LocalTime?,
    val lastSevenDaysCompletions: List<DailyCompletion>,
    val flowerGrowthStage: FlowerGrowthStage,
    val flowerType: FlowerType,
    val flowerHealth: FlowerHealth = FlowerHealth(),
    val level: Int = 1,
    val totalXp: Int = 0,
    val xpInLevel: Int = 0,
    val xpForCurrentLevel: Int = 0,
    val xpToNextLevel: Int = 0,
) {
    data class DailyCompletion(
        val date: LocalDate,
        val isCompleted: Boolean
    )
} 
