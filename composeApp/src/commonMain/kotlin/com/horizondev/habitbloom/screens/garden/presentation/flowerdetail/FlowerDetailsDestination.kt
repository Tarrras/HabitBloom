package com.horizondev.habitbloom.screens.garden.presentation.flowerdetail

import com.horizondev.habitbloom.core.navigation.NavTarget
import kotlinx.serialization.Serializable

/**
 * Navigation destination for the Habit Flower Details screen.
 *
 * @property habitId The ID of the habit to display
 */
@Serializable
data class FlowerDetailsDestination(
    val habitId: Long
) : NavTarget
