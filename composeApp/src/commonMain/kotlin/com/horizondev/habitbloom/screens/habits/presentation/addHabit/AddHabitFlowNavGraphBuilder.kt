package com.horizondev.habitbloom.screens.habits.presentation.addHabit

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.categoryChoice.AddHabitCategoryChoiceScreen
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.durationChoice.AddHabitDurationChoiceScreen
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.habitChoise.AddHabitChoiceScreen
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.success.AddHabitSuccessScreen
import com.horizondev.habitbloom.screens.habits.presentation.addHabit.summary.AddHabitSummaryScreen

/**
 * Extension function for NavGraphBuilder to add routes defined in AddHabitFlowRoute
 */
fun NavGraphBuilder.addHabitFlowGraph(
    navController: NavController,
    onNavigateToCreateCustomHabit: (String?) -> Unit
) {
    composable<AddHabitFlowGlobalNavEntryPoint> {
        AddHabitFlowNavHost(
            onFinishFlow = { navController.popBackStack() },
            onNavigateToCreateCustomHabit = onNavigateToCreateCustomHabit
        )
    }
}


/**
 * Extension function for NavGraphBuilder to add routes defined in AddHabitFlowRoute
 */
fun NavGraphBuilder.createHabitNestedFlowGraph(
    navController: NavController,
    viewModel: AddHabitFlowViewModel,
    onNavigateToCreateCustomHabit: (String?) -> Unit,
    onFinishFlow: () -> Unit
) {
    navigation<AddHabitFlowGlobalNavEntryPoint>(
        startDestination = AddHabitFlowRoute.CategoryChoice
    ) {
        // Category choice screen
        composable<AddHabitFlowRoute.CategoryChoice> {
            AddHabitCategoryChoiceScreen(
                onCategorySelected = {
                    navController.navigate(AddHabitFlowRoute.HabitChoice)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Choose habit screen
        composable<AddHabitFlowRoute.HabitChoice> {
            AddHabitChoiceScreen(
                onNext = {
                    navController.navigate(AddHabitFlowRoute.DurationChoice)
                },
                onCreateCustomHabit = { categoryId ->
                    onNavigateToCreateCustomHabit(categoryId)
                },
                onBack = {
                    navController.popBackStack()
                },
                showSnackbar = { snackbar ->
                    viewModel.showSnackbar(snackbar)
                }
            )
        }

        // Duration choice screen
        composable<AddHabitFlowRoute.DurationChoice> {
            AddHabitDurationChoiceScreen(
                onNext = { navController.navigate(AddHabitFlowRoute.Summary) },
                onBack = {
                    navController.popBackStack()
                },
                showSnackbar = { snackbar ->
                    viewModel.showSnackbar(snackbar)
                }
            )
        }

        // Summary screen
        composable<AddHabitFlowRoute.Summary> {
            AddHabitSummaryScreen(
                onSuccess = {
                    navController.navigate(AddHabitFlowRoute.Success)
                },
                onBack = {
                    navController.popBackStack()
                },
                showSnackbar = { snackbar ->
                    viewModel.showSnackbar(snackbar)
                }
            )
        }

        // Success screen
        composable<AddHabitFlowRoute.Success> {
            AddHabitSuccessScreen(
                onFinish = {
                    onFinishFlow()
                },
                onAddAnother = {
                    navController.navigate(AddHabitFlowRoute.CategoryChoice) {
                        popUpTo(AddHabitFlowRoute.CategoryChoice) { inclusive = true }
                    }
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