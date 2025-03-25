package com.horizondev.habitbloom.screens.garden

import com.horizondev.habitbloom.core.navigation.NavTarget
import kotlinx.serialization.Serializable

@Serializable
object GardenFlowGlobalNavEntryPoint : NavTarget

@Serializable
sealed class GardenFlowRoute : NavTarget {

    /**
     * Screen with all flowers
     */
    @Serializable
    data object HabitGarden : GardenFlowRoute()

    /**
     * Flower details
     */
    @Serializable
    data class FlowerDetails(val habitId: Long) : GardenFlowRoute()
}