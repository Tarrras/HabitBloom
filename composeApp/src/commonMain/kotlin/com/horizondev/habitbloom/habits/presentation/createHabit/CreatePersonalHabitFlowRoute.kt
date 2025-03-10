package com.horizondev.habitbloom.habits.presentation.createHabit

import com.horizondev.habitbloom.core.navigation.NavTarget
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import kotlinx.serialization.Serializable

/**
 * Global entry point for the Create Personal Habit flow.
 */
@Serializable
object CreatePersonalHabitFlowGlobalNavEntryPoint : NavTarget

/**
 * Sealed class representing all possible navigation destinations in the Create Personal Habit flow.
 */
@Serializable
sealed class CreatePersonalHabitFlowRoute : NavTarget {

    /**
     * Create personal habit screen with optional time of day parameter.
     */
    @Serializable
    data class CreateHabit(val timeOfDay: TimeOfDay? = null) : CreatePersonalHabitFlowRoute()

    /**
     * Success screen showing habit created successfully.
     */
    @Serializable
    data object Success : CreatePersonalHabitFlowRoute()

    companion object {
        /**
         * Parse route string to corresponding route object
         */
        fun fromRoute(routeString: String?): CreatePersonalHabitFlowRoute? {
            if (routeString.isNullOrBlank()) return null

            return when {
                routeString.contains(CreateHabit::class.qualifiedName.toString()) -> CreateHabit()
                routeString.contains(Success::class.qualifiedName.toString()) -> Success
                else -> null
            }
        }
    }
} 