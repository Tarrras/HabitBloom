package com.horizondev.habitbloom.screens.habits.presentation.habitDetails

import com.horizondev.habitbloom.core.navigation.NavTarget
import kotlinx.serialization.Serializable

@Serializable
data class HabitDetailsDestination(
    val habitId: Long
) : NavTarget
