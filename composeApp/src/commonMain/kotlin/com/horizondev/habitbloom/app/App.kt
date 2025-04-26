package com.horizondev.habitbloom.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.horizondev.habitbloom.common.AppViewModel
import com.horizondev.habitbloom.common.MainScreen
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.core.navigation.CommonNavigator
import com.horizondev.habitbloom.core.navigation.NavTarget
import com.horizondev.habitbloom.core.navigation.NavigationComponent
import com.horizondev.habitbloom.screens.onboarding.OnboardingScreen
import com.horizondev.habitbloom.screens.onboarding.OnboardingViewModel
import com.horizondev.habitbloom.screens.splash.SplashScreen
import kotlinx.serialization.Serializable
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinContext {
        val viewModel = koinViewModel<AppViewModel>()
        val uiState by viewModel.state.collectAsState()

        val navController = rememberNavController()
        val commonNavigator: CommonNavigator = koinInject()

        LaunchedEffect(Unit) {
            viewModel.initApp()
        }

        LaunchedEffect(uiState) {
            when (uiState.isOnboardingNeeded) {
                true -> navController.navigate(AppRoute.OnboardingRoute)
                false -> navController.navigate(AppRoute.MainRoute)
                else -> {}
            }
        }

        BloomTheme {
            NavigationComponent(
                modifier = Modifier,
                navController = navController,
                navigator = commonNavigator,
                startDestination = AppRoute.SplashRoute
            ) {
                composable<AppRoute.SplashRoute> {
                    SplashScreen()
                }

                composable<AppRoute.OnboardingRoute> {
                    val viewModel = koinViewModel<OnboardingViewModel>()
                    OnboardingScreen(
                        viewModel = viewModel,
                        navigateToMainScreen = {
                            navController.navigate(AppRoute.MainRoute)
                        }
                    )
                }

                composable<AppRoute.MainRoute> {
                    MainScreen()
                }
            }
        }
    }
}

@Serializable
sealed class AppRoute : NavTarget {
    @Serializable
    data object SplashRoute : AppRoute()

    @Serializable
    data object OnboardingRoute : AppRoute()

    @Serializable
    data object MainRoute : AppRoute()
}

