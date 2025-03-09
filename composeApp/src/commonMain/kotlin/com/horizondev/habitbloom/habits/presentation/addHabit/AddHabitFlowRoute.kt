package com.horizondev.habitbloom.habits.presentation.addHabit

import com.horizondev.habitbloom.core.navigation.NavTarget
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import kotlinx.serialization.Serializable

@Serializable
object AddHabitFlowGlobalNavEntryPoint : NavTarget

/**
 * Sealed class representing all possible navigation destinations in the Add Habit flow.
 */
@Serializable
sealed class AddHabitFlowRoute : NavTarget {

    /**
     * Time of day selection screen.
     */
    @Serializable
    data object TimeOfDayChoice : AddHabitFlowRoute()

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
        private const val PACKAGE_PREFIX =
            "com.horizondev.habitbloom.habits.presentation.addHabit.AddHabitFlowRoute."

        /**
         * Converts a route string back to the appropriate AddHabitFlowRoute instance.
         * Handles both fully qualified class names and simple route names.
         */
        fun fromRouteString(routeString: String?): AddHabitFlowRoute? {
            if (routeString == null) return null

            // Strip the package prefix if present
            val simplifiedRoute = if (routeString.startsWith(PACKAGE_PREFIX)) {
                routeString.removePrefix(PACKAGE_PREFIX)
            } else {
                routeString
            }

            // Extract the base route and any parameters
            val baseParts = simplifiedRoute.split("(", limit = 2)
            val baseRoute = baseParts[0]

            return when (baseRoute) {
                "TimeOfDayChoice" -> TimeOfDayChoice
                "HabitChoice" -> {
                    // Try to extract the timeOfDay parameter
                    if (baseParts.size > 1 && baseParts[1].contains("timeOfDay=")) {
                        val timeOfDayPart = baseParts[1]
                            .split(",")
                            .firstOrNull { it.contains("timeOfDay=") }
                            ?.trim()

                        // Extract the TimeOfDay enum value
                        val timeOfDayValue = timeOfDayPart
                            ?.substringAfter("timeOfDay=")
                            ?.substringBefore(")")
                            ?.trim()

                        when (timeOfDayValue) {
                            "Morning" -> HabitChoice(TimeOfDay.Morning)
                            "Afternoon" -> HabitChoice(TimeOfDay.Afternoon)
                            "Evening" -> HabitChoice(TimeOfDay.Evening)
                            else -> HabitChoice(TimeOfDay.Morning) // Default if parsing fails
                        }
                    } else {
                        // If parameter extraction fails, return with default value
                        HabitChoice(TimeOfDay.Morning)
                    }
                }

                "DurationChoice" -> DurationChoice
                "Summary" -> Summary
                "Success" -> Success
                else -> null // Unknown route
            }
        }

        /**
         * Maps a route string to the corresponding AddHabitFlowScreenStep.
         */
        fun getScreenStepFromRoute(routeString: String?): AddHabitFlowScreenStep {
            val route = fromRouteString(routeString)
            return when (route) {
                is TimeOfDayChoice -> AddHabitFlowScreenStep.CHOOSE_CATEGORY
                is HabitChoice -> AddHabitFlowScreenStep.CHOOSE_HABIT
                is DurationChoice -> AddHabitFlowScreenStep.CHOOSE_DURATION
                is Summary -> AddHabitFlowScreenStep.SUMMARY
                is Success, null -> AddHabitFlowScreenStep.CHOOSE_HABIT
            }
        }
    }
} 