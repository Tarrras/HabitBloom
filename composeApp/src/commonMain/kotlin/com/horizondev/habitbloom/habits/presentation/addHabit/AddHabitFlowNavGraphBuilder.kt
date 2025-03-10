package com.horizondev.habitbloom.habits.presentation.addHabit

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.horizondev.habitbloom.habits.presentation.addHabit.durationChoice.AddHabitDurationChoiceScreen
import com.horizondev.habitbloom.habits.presentation.addHabit.habitChoise.AddHabitChoiceScreen
import com.horizondev.habitbloom.habits.presentation.addHabit.success.AddHabitSuccessScreen
import com.horizondev.habitbloom.habits.presentation.addHabit.summary.AddHabitSummaryScreen
import com.horizondev.habitbloom.habits.presentation.addHabit.timeOfDayChoice.AddHabitTimeOfDayChoiceScreen
import com.horizondev.habitbloom.habits.presentation.createHabit.details.CreatePersonalHabitScreen
import com.horizondev.habitbloom.habits.presentation.createHabit.details.CreatePersonalHabitScreenModel
import com.horizondev.habitbloom.habits.presentation.createHabit.success.CreatePersonalHabitSuccessScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Extension function for NavGraphBuilder to add routes defined in AddHabitFlowRoute
 */
fun NavGraphBuilder.addHabitFlowGraph(
    navController: NavController,
    viewModel: AddHabitFlowViewModel,
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
        composable<AddHabitFlowRoute.HabitChoice> { entry ->
            val timeOfDay = entry.toRoute<AddHabitFlowRoute.HabitChoice>().timeOfDay

            AddHabitChoiceScreen(
                timeOfDay = timeOfDay,
                onHabitSelected = { habit ->
                    viewModel.handleUiEvent(AddHabitFlowUiEvent.UpdateHabit(habit))
                    navController.navigate(AddHabitFlowRoute.DurationChoice)
                },
                onCreateCustomHabit = { selectedTimeOfDay ->
                    navController.navigate(AddHabitFlowRoute.CreatePersonalHabit(selectedTimeOfDay))
                },
                onBack = {
                    navController.popBackStack()
                },
                showSnackbar = {
                    viewModel.handleUiEvent(AddHabitFlowUiEvent.ShowSnackbar(it))
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
                },
                showSnackbar = {
                    viewModel.handleUiEvent(AddHabitFlowUiEvent.ShowSnackbar(it))
                }
            )
        }

        // Summary screen
        composable<AddHabitFlowRoute.Summary> {
            val hostState by viewModel.state.collectAsState()

            AddHabitSummaryScreen(
                hostState = hostState,
                onSuccess = {
                    navController.navigate(AddHabitFlowRoute.Success)
                },
                onBack = {
                    navController.popBackStack()
                },
                showSnackbar = {
                    viewModel.handleUiEvent(AddHabitFlowUiEvent.ShowSnackbar(it))
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

        // Create Personal Habit Screen
        composable<AddHabitFlowRoute.CreatePersonalHabit> { entry ->
            val timeOfDay = entry.toRoute<AddHabitFlowRoute.CreatePersonalHabit>().timeOfDay

            val createPersonalHabitViewModel = koinViewModel<CreatePersonalHabitScreenModel> {
                parametersOf(timeOfDay)
            }

            CreatePersonalHabitScreen(
                viewModel = createPersonalHabitViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOpenSuccessScreen = {
                    navController.navigate(AddHabitFlowRoute.CreatePersonalHabitSuccess)
                }
            )
        }

        // Create Personal Habit Success Screen
        composable<AddHabitFlowRoute.CreatePersonalHabitSuccess> {
            CreatePersonalHabitSuccessScreen(
                onFinish = {
                    navController.popBackStack<AddHabitFlowRoute.HabitChoice>(inclusive = false)
                }
            )
        }
    }
}

/**
 * Extension function to get current route information for analytics
 */
fun NavDestination.getAddHabitRouteInfo(): RouteInfo {
    val route = this.route
    val parsedRoute = AddHabitFlowRoute.fromRoute(route)
    return RouteInfo(
        routeString = route,
        route = parsedRoute,
        step = AddHabitFlowRoute.toScreenStep(parsedRoute)
    )
}

/**
 * Helper class containing route information
 */
data class RouteInfo(
    val routeString: String?,
    val route: AddHabitFlowRoute?,
    val step: AddHabitFlowScreenStep?
)