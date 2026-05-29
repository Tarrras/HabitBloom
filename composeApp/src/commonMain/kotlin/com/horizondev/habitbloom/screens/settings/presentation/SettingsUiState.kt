package com.horizondev.habitbloom.screens.settings.presentation

import com.horizondev.habitbloom.auth.domain.AuthProvider
import com.horizondev.habitbloom.common.settings.NotificationState
import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.common.settings.TimeFormatOption

/**
 * UI state for the Settings screen.
 */
data class SettingsUiState(
    val isLoading: Boolean = false,
    val notificationState: NotificationState = NotificationState.NOT_DETERMINED,
    val themeMode: ThemeOption = ThemeOption.Device,
    val timeFormat: TimeFormatOption = TimeFormatOption.System,
    val isThemeDialogVisible: Boolean = false,
    val showDeleteDataDialog: Boolean = false,
    val authProfile: SettingsAuthProfileUiState = SettingsAuthProfileUiState(),
    val authMode: SettingsAuthMode = SettingsAuthMode.SignIn,
    val showAuthSheet: Boolean = false,
    val authEmail: String = "",
    val authPassword: String = "",
    val authError: String? = null,
    val isAuthLoading: Boolean = false
) {
    val notificationsEnabled: Boolean
        get() = notificationState.isEnabled()
}

data class SettingsAuthProfileUiState(
    val isAuthenticated: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    val provider: AuthProvider = AuthProvider.Guest
)

enum class SettingsAuthMode {
    SignIn,
    SignUp
}

/**
 * Events that can be triggered from the Settings screen.
 */
sealed interface SettingsUiEvent {
    data class ToggleNotifications(val enabled: Boolean) : SettingsUiEvent
    data class SetThemeMode(val mode: ThemeOption) : SettingsUiEvent
    data class SetTimeFormat(val option: TimeFormatOption) : SettingsUiEvent
    data object Logout : SettingsUiEvent
    data object OpenThemeDialog : SettingsUiEvent
    data object CloseThemeDialog : SettingsUiEvent
    data object ShowDeleteDataDialog : SettingsUiEvent
    data object DismissDeleteDataDialog : SettingsUiEvent
    data object ConfirmDeleteData : SettingsUiEvent
}

/**
 * Intents that can be emitted from the Settings screen.
 */
sealed interface SettingsUiIntent {
    data object NavigateToLogin : SettingsUiIntent
    data object NavigateToOnboarding : SettingsUiIntent
}
