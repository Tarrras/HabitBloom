package com.horizondev.habitbloom.auth.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.horizondev.habitbloom.auth.presentation.resetpassword.SettingsResetPasswordDestination
import com.horizondev.habitbloom.auth.presentation.resetpassword.SettingsResetPasswordScreen
import com.horizondev.habitbloom.auth.presentation.resetpassword.SettingsResetPasswordViewModel
import com.horizondev.habitbloom.auth.presentation.signin.SettingsAuthDestination
import com.horizondev.habitbloom.auth.presentation.signin.SettingsAuthMode
import com.horizondev.habitbloom.auth.presentation.signin.SettingsAuthScreen
import com.horizondev.habitbloom.auth.presentation.signin.SettingsAuthViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.authFlowGraph(
    navController: NavController
) {
    navigation<AuthFlowGlobalNavEntryPoint>(
        startDestination = SettingsAuthDestination.from(SettingsAuthMode.SignIn)
    ) {
        composable<SettingsAuthDestination> { entry ->
            val destination = entry.toRoute<SettingsAuthDestination>()
            val viewModel = koinViewModel<SettingsAuthViewModel>()

            SettingsAuthScreen(
                viewModel = viewModel,
                initialMode = destination.toAuthMode(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<SettingsResetPasswordDestination> {
            val viewModel = koinViewModel<SettingsResetPasswordViewModel>()

            SettingsResetPasswordScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
