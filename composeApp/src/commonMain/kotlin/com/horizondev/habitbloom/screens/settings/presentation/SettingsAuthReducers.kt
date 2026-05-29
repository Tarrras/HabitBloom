package com.horizondev.habitbloom.screens.settings.presentation

import com.horizondev.habitbloom.auth.domain.AuthProvider
import com.horizondev.habitbloom.auth.domain.AuthSession
import com.horizondev.habitbloom.auth.domain.validateEmailPassword

fun reduceAuthSession(
    state: SettingsUiState,
    session: AuthSession
): SettingsUiState {
    val user = session.user
    val profile = if (session.isAuthenticated && user != null) {
        SettingsAuthProfileUiState(
            isAuthenticated = true,
            title = user.displayName ?: user.email ?: "HabitBloom account",
            subtitle = when (user.provider) {
                AuthProvider.Google -> "Signed in with Google"
                AuthProvider.Email -> "Signed in with email"
                AuthProvider.Apple -> "Signed in with Apple"
                AuthProvider.Guest -> "Guest profile"
                AuthProvider.Unknown -> "Signed in"
            },
            provider = user.provider
        )
    } else {
        SettingsAuthProfileUiState(
            isAuthenticated = false,
            title = "Guest profile",
            subtitle = "Sign in to protect your progress",
            provider = AuthProvider.Guest
        )
    }

    return state.copy(authProfile = profile, isAuthLoading = false)
}

fun reduceAuthSheetOpened(
    state: SettingsUiState,
    mode: SettingsAuthMode
): SettingsUiState {
    return state.copy(
        authMode = mode,
        showAuthSheet = true,
        authError = null,
        authPassword = "",
        isAuthLoading = false
    )
}

fun reduceAuthSheetClosed(state: SettingsUiState): SettingsUiState {
    return state.copy(
        showAuthSheet = false,
        authPassword = "",
        authError = null,
        isAuthLoading = false
    )
}

sealed interface SettingsAuthFormValidation {
    data object Valid : SettingsAuthFormValidation
    data class Invalid(val message: String) : SettingsAuthFormValidation
}

fun validateAuthForm(state: SettingsUiState): SettingsAuthFormValidation {
    return when (validateEmailPassword(state.authEmail, state.authPassword)) {
        is com.horizondev.habitbloom.auth.domain.AuthInputValidation.Valid -> {
            SettingsAuthFormValidation.Valid
        }

        is com.horizondev.habitbloom.auth.domain.AuthInputValidation.Invalid -> {
            SettingsAuthFormValidation.Invalid("Enter a valid email and password.")
        }
    }
}
