package com.horizondev.habitbloom.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Subscribes the current destination state to perform navigation.
 * Typically should be created by a navigation host (e.g Activity)
 *
 * @param navController NavController from the nav host
 * @param navigator Current navigation state holder (must be Singleton)
 * @param startDestination Start destination for the navigation, e.g feature entry point
 * @param buildGraphs to add all graphs for the features
 * */

@Composable
fun NavigationComponent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    navigator: CommonNavigator,
    startDestination: Any,
    buildGraphs: NavGraphBuilder.() -> Unit
) {
    LaunchedEffect(Unit) {
        navigator.destinationsSharedFlow.onEach { destination ->
            navController.navigate(destination) {
                popUpTo(destination) // To make sure there is a single route instance in the stack
            }
        }.launchIn(this)
    }

    // Navigation Directions
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        buildGraphs()
    }
}