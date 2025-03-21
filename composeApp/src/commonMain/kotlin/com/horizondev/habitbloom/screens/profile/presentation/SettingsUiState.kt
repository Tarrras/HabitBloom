package com.horizondev.habitbloom.screens.profile.presentation

import com.horizondev.habitbloom.common.settings.ThemeOption

/**
 * Represents the UI state for the Settings screen.
 */
data class SettingsUiState(
    val isLoading: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val themeMode: ThemeOption = ThemeOption.Device
)

/**
 * UI events that can be triggered from the Settings screen.
 */
sealed class SettingsUiEvent {
    data class ToggleNotifications(val enabled: Boolean) : SettingsUiEvent()
    data class SetThemeMode(val mode: ThemeOption) : SettingsUiEvent()
    data object Logout : SettingsUiEvent()
}

/**
 * Intent actions that can be triggered by the ViewModel.
 */
sealed class SettingsUiIntent {
    data object NavigateToLogin : SettingsUiIntent()
} 