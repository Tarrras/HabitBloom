package com.horizondev.habitbloom.screens.habits.presentation.addHabit

import com.horizondev.habitbloom.core.navigation.NavTarget
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import kotlinx.serialization.Serializable

@Serializable
object AddHabitFlowGlobalNavEntryPoint : NavTarget

/**
 * Sealed class representing all possible navigation destinations in the Add Habit flow.
 */
@Serializable
sealed class AddHabitFlowRoute : NavTarget {

    /**
     * Category selection screen.
     */
    @Serializable
    data object CategoryChoice : AddHabitFlowRoute()

    /**
     * Habit choice screen with time of day parameter.
     */
    @Serializable
    data class HabitChoice(val timeOfDay: TimeOfDay) : AddHabitFlowRoute()

    /**
     * Duration choice screen with initial duration parameter.
     */
    @Serializable
    data object DurationChoice : AddHabitFlowRoute()

    /**
     * Summary screen with habit ID, duration, and start date parameters.
     */
    @Serializable
    data object Summary : AddHabitFlowRoute()

    /**
     * Success screen showing habit added successfully.
     */
    @Serializable
    data object Success : AddHabitFlowRoute()

    companion object {

        /**
         * Simplified approach to parse route strings
         */
        fun fromRoute(routeString: String?): AddHabitFlowRoute? {
            if (routeString.isNullOrBlank()) return null

            return when {
                routeString.contains(CategoryChoice::class.qualifiedName.toString()) -> CategoryChoice

                //in this case we don't need exact time of day
                routeString.contains(HabitChoice::class.qualifiedName.toString()) -> HabitChoice(
                    timeOfDay = TimeOfDay.Morning
                )

                routeString.contains(DurationChoice::class.qualifiedName.toString()) -> DurationChoice
                routeString.contains(Summary::class.qualifiedName.toString()) -> Summary
                routeString.contains(Success::class.qualifiedName.toString()) -> Success
                else -> null
            }
        }

        /**
         * Maps route to screen step - with defaults for typical flow order
         */
        fun toScreenStep(route: AddHabitFlowRoute?): AddHabitFlowScreenStep? {
            return when (route) {
                is CategoryChoice -> AddHabitFlowScreenStep.CHOOSE_CATEGORY
                is HabitChoice -> AddHabitFlowScreenStep.CHOOSE_HABIT
                is DurationChoice -> AddHabitFlowScreenStep.CHOOSE_DURATION
                is Summary -> AddHabitFlowScreenStep.SUMMARY
                is Success -> AddHabitFlowScreenStep.SUCCESS
                null -> null
            }
        }
    }
}