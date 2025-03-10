package com.horizondev.habitbloom.habits.presentation.createHabit

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.horizondev.habitbloom.habits.presentation.createHabit.details.CreatePersonalHabitScreen
import com.horizondev.habitbloom.habits.presentation.createHabit.details.CreatePersonalHabitScreenModel
import com.horizondev.habitbloom.habits.presentation.createHabit.success.CreatePersonalHabitSuccessScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Extension function for NavGraphBuilder to add routes defined in CreatePersonalHabitFlowRoute
 *
 * @param navController The NavController for navigation
 */
fun NavGraphBuilder.createPersonalHabitFlowGraph(
    navController: NavController,
) {
    navigation<CreatePersonalHabitFlowGlobalNavEntryPoint>(
        startDestination = CreatePersonalHabitFlowRoute.CreateHabit()
    ) {
        // Create Personal Habit Screen
        composable<CreatePersonalHabitFlowRoute.CreateHabit> { entry ->
            val timeOfDay = entry.toRoute<CreatePersonalHabitFlowRoute.CreateHabit>().timeOfDay

            val createPersonalHabitViewModel = koinViewModel<CreatePersonalHabitScreenModel> {
                parametersOf(timeOfDay)
            }

            CreatePersonalHabitScreen(
                viewModel = createPersonalHabitViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOpenSuccessScreen = {
                    navController.navigate(CreatePersonalHabitFlowRoute.Success)
                }
            )
        }

        // Create Personal Habit Success Screen
        composable<CreatePersonalHabitFlowRoute.Success> {
            CreatePersonalHabitSuccessScreen(
                onFinish = {
                    navController.popBackStack<CreatePersonalHabitFlowGlobalNavEntryPoint>(
                        inclusive = true
                    )
                }
            )
        }
    }
}