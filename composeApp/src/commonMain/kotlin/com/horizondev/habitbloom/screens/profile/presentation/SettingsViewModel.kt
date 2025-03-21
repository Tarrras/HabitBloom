package com.horizondev.habitbloom.screens.profile.presentation

import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.profile.domain.ProfileRepository

/**
 * ViewModel for the Settings screen.
 */
class SettingsViewModel(
    private val repository: ProfileRepository
) : BloomViewModel<SettingsUiState, SettingsUiIntent>(
    SettingsUiState()
) {
    /**
     * Handles UI events from the Settings screen.
     */
    fun handleUiEvent(event: SettingsUiEvent) {
        when (event) {
            is SettingsUiEvent.ToggleNotifications -> {
                updateState { it.copy(notificationsEnabled = event.enabled) }
                // TODO: Save to repository
            }

            is SettingsUiEvent.ToggleDarkMode -> {
                updateState { it.copy(darkModeEnabled = event.enabled) }
                // TODO: Save to repository
            }

            is SettingsUiEvent.Logout -> {
                launch {
                    //repository.logout()
                    emitUiIntent(SettingsUiIntent.NavigateToLogin)
                }
            }
        }
    }
} 