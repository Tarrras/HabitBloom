package com.horizondev.habitbloom.screens.garden.domain

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Data model for habit flower detail information.
 * Contains all data related to displaying a habit's growth visualization.
 */
data class HabitFlowerDetail(
    val habitId: Long,
    val name: String,
    val description: String,
    val iconUrl: String?,
    val timeOfDay: TimeOfDay,
    val currentStreak: Int,
    val longestStreak: Int,
    val startDate: LocalDate,
    val repeats: Int,
    val reminderTime: LocalTime?,
    val lastSevenDaysCompletions: List<DailyCompletion>,
    val isCompletedToday: Boolean,
    val flowerGrowthStage: FlowerGrowthStage,
    val flowerType: FlowerType,
    val streaksToNextStage: Int,
    val flowerHealth: FlowerHealth = FlowerHealth()
) {
    /**
     * Calculate if the flower is in a wilting state (showing visual signs of declining health).
     */
    val isWilting: Boolean
        get() = flowerHealth.isWilting

    /**
     * Calculate if the flower is in a critical health state (close to regression).
     */
    val isCriticalHealth: Boolean
        get() = flowerHealth.isCritical
        
    /**
     * Model for daily completion status tracking.
     */
    data class DailyCompletion(
        val date: LocalDate,
        val isCompleted: Boolean
    )
} 