package com.horizondev.habitbloom.screens.profile.presentation

import com.horizondev.habitbloom.common.settings.ThemeOption

/**
 * UI state for the Settings screen.
 */
data class SettingsUiState(
    val isLoading: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val themeMode: ThemeOption = ThemeOption.Device,
    val isThemeDialogVisible: Boolean = false
)

/**
 * Events that can be triggered from the Settings screen.
 */
sealed interface SettingsUiEvent {
    data class ToggleNotifications(val enabled: Boolean) : SettingsUiEvent
    data class SetThemeMode(val mode: ThemeOption) : SettingsUiEvent
    data object Logout : SettingsUiEvent
    data object OpenThemeDialog : SettingsUiEvent
    data object CloseThemeDialog : SettingsUiEvent
}

/**
 * Intents that can be emitted from the Settings screen.
 */
sealed interface SettingsUiIntent {
    data object NavigateToLogin : SettingsUiIntent
} 