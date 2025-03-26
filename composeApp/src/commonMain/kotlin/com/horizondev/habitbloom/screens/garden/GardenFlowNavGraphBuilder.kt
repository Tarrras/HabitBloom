package com.horizondev.habitbloom.screens.garden

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.horizondev.habitbloom.screens.garden.presentation.HabitGardenScreen
import com.horizondev.habitbloom.screens.garden.presentation.HabitGardenViewModel
import com.horizondev.habitbloom.screens.garden.presentation.flowerdetail.HabitFlowerDetailScreen
import com.horizondev.habitbloom.screens.habits.presentation.habitDetails.HabitDetailsDestination
import com.horizondev.habitbloom.screens.habits.presentation.habitDetails.HabitDetailsScreen
import org.koin.compose.viewmodel.koinViewModel


fun NavGraphBuilder.gardenNestedFlowGraph(
    navController: NavController,
) {
    navigation<GardenFlowGlobalNavEntryPoint>(
        startDestination = GardenFlowRoute.HabitGarden
    ) {
        // Time of Day choice screen
        composable<GardenFlowRoute.HabitGarden> {
            val viewModel = koinViewModel<HabitGardenViewModel>()
            HabitGardenScreen(
                viewModel = viewModel,
                onNavigateToHabitFlower = { habitId ->
                    navController.navigate(GardenFlowRoute.FlowerDetails(habitId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Choose habit screen
        composable<GardenFlowRoute.FlowerDetails> { entry ->
            val habitId = entry.toRoute<GardenFlowRoute.FlowerDetails>().habitId

            HabitFlowerDetailScreen(
                habitId = habitId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHabitDetails = { habitId ->
                    navController.navigate(HabitDetailsDestination(habitId))
                }
            )
        }

        composable<HabitDetailsDestination> { entry ->
            val data = entry.toRoute<HabitDetailsDestination>()

            HabitDetailsScreen(userHabitId = data.habitId, popBackStack = {
                navController.popBackStack()
            })
        }
    }
}