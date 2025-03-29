package com.horizondev.habitbloom.screens.garden.domain

import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay

/**
 * Domain model for displaying a habit as a flower in the garden.
 *
 * @property habitId The unique identifier of the habit
 * @property name The name of the habit
 * @property iconUrl The URL of the habit's icon
 * @property streak The current streak of the habit
 * @property timeOfDay The time of day this habit belongs to
 * @property bloomingStage The current blooming stage based on the streak
 * @property maxStage Stage based on longest streak ever achieved
 * @property health The health status of the flower
 */
data class HabitFlower(
    val habitId: Long,
    val name: String,
    val iconUrl: String,
    val streak: Int,
    val timeOfDay: TimeOfDay,
    val bloomingStage: FlowerGrowthStage,
    val maxStage: FlowerGrowthStage,
    val health: FlowerHealth = FlowerHealth()
) 