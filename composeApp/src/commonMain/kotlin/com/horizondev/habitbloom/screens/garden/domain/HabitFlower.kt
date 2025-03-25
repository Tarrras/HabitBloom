package com.horizondev.habitbloom.screens.garden.domain

import com.horizondev.habitbloom.screens.flowerdetail.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay

/**
 * Represents a habit with its blooming stage information for the garden view.
 *
 * @property habitId The unique identifier of the habit
 * @property name The name of the habit
 * @property iconUrl The URL of the habit's icon
 * @property streak The current streak of the habit
 * @property timeOfDay The time of day this habit belongs to
 * @property bloomingStage The current blooming stage based on the streak
 */
data class HabitFlower(
    val habitId: Long,
    val name: String,
    val iconUrl: String,
    val streak: Int,
    val timeOfDay: TimeOfDay,
    val bloomingStage: FlowerGrowthStage
) 