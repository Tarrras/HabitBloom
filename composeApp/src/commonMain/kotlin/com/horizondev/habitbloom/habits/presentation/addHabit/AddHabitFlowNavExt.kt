package com.horizondev.habitbloom.habits.presentation.addHabit

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice.AddHabitDurationChoiceScreen
import com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise.AddHabitChoiceScreen
import com.horizondev.habitbloom.habits.presentation.addHabit.success.AddHabitSuccessScreen
import com.horizondev.habitbloom.habits.presentation.addHabit.summary.AddHabitSummaryScreen
import com.horizondev.habitbloom.habits.presentation.addHabit.timeOfDayChoice.AddHabitTimeOfDayChoiceScreen

/**
 * Extension function for NavGraphBuilder to add routes defined in AddHabitFlowRoute
 */
fun NavGraphBuilder.addHabitFlowGraph(
    navController: NavController,
    viewModel: AddHabitFlowViewModel,
    onNavigateToCreateCustomHabit: (TimeOfDay?) -> Unit
) {
    navigation<AddHabitFlowGlobalNavEntryPoint>(
        startDestination = AddHabitFlowRoute.TimeOfDayChoice
    ) {
        // Time of Day choice screen
        composable<AddHabitFlowRoute.TimeOfDayChoice> {
            AddHabitTimeOfDayChoiceScreen(
                onTimeOfDaySelected = { selectedTimeOfDay ->
                    viewModel.handleUiEvent(AddHabitFlowUiEvent.UpdateTimeOfDay(selectedTimeOfDay))
                    navController.navigate(AddHabitFlowRoute.HabitChoice(selectedTimeOfDay))
                },
                onBack = {
                    viewModel.handleUiEvent(AddHabitFlowUiEvent.CancelFlow)
                }
            )
        }

        // Choose habit screen
        composable<AddHabitFlowRoute.HabitChoice> { backStackEntry ->
            val timeOfDayArg = backStackEntry.toRoute<TimeOfDay>()

            AddHabitChoiceScreen(
                timeOfDay = timeOfDayArg,
                onHabitSelected = { habit ->
                    viewModel.handleUiEvent(AddHabitFlowUiEvent.UpdateHabit(habit))
                    navController.navigate(AddHabitFlowRoute.DurationChoice)
                },
                onCreateCustomHabit = { selectedTimeOfDay ->
                    onNavigateToCreateCustomHabit(selectedTimeOfDay)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Duration choice screen
        composable<AddHabitFlowRoute.DurationChoice> {
            AddHabitDurationChoiceScreen(
                onDurationSelected = { duration, startDate, selectedDays, weekStartOption ->
                    viewModel.handleUiEvent(
                        AddHabitFlowUiEvent.UpdateDuration(
                            durationInDays = duration,
                            startDate = startDate,
                            selectedDays = selectedDays,
                            weekStartOption = weekStartOption
                        )
                    )
                    navController.navigate(AddHabitFlowRoute.Summary)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Summary screen
        composable<AddHabitFlowRoute.Summary> {
            AddHabitSummaryScreen(
                sharedViewModel = viewModel,
                onSuccess = {
                    navController.navigate(AddHabitFlowRoute.Success)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Success screen
        composable<AddHabitFlowRoute.Success> {
            AddHabitSuccessScreen(
                onFinish = {
                    viewModel.handleUiEvent(AddHabitFlowUiEvent.CancelFlow)
                }
            )
        }
    }
}

/**
 * Extension function to extract screen information from the current destination
 * Useful for analytics or tracking.
 */
fun NavController.getCurrentScreenInfo(): Map<String, String> {
    val destination = currentDestination?.route ?: return emptyMap()
    return when (val parsedRoute = AddHabitFlowRoute.fromRouteString(destination)) {
        is AddHabitFlowRoute.TimeOfDayChoice -> mapOf(
            "screen" to "time_of_day_choice",
            "step" to "1"
        )

        is AddHabitFlowRoute.HabitChoice -> mapOf(
            "screen" to "habit_choice",
            "step" to "2",
            "timeOfDay" to (parsedRoute.timeOfDay.name)
        )

        is AddHabitFlowRoute.DurationChoice -> mapOf(
            "screen" to "duration_choice",
            "step" to "3"
        )

        is AddHabitFlowRoute.Summary -> mapOf(
            "screen" to "summary",
            "step" to "4"
        )

        is AddHabitFlowRoute.Success -> mapOf(
            "screen" to "success",
            "step" to "5"
        )

        else -> mapOf(
            "screen" to "unknown",
            "route" to destination
        )
    }
}