package com.horizondev.habitbloom.screens.profile.presentation

/**
 * Represents the UI state for the Settings screen.
 */
data class SettingsUiState(
    val isLoading: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false
)

/**
 * UI events that can be triggered from the Settings screen.
 */
sealed class SettingsUiEvent {
    data class ToggleNotifications(val enabled: Boolean) : SettingsUiEvent()
    data class ToggleDarkMode(val enabled: Boolean) : SettingsUiEvent()
    data object Logout : SettingsUiEvent()
}

/**
 * Intent actions that can be triggered by the ViewModel.
 */
sealed class SettingsUiIntent {
    data object NavigateToLogin : SettingsUiIntent()
} 