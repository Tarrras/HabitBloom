package com.horizondev.habitbloom.screens.profile.presentation

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.profile.domain.ProfileRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent

/**
 * ViewModel for the Settings screen.
 */
class SettingsViewModel(
    private val repository: ProfileRepository
) : BloomViewModel<SettingsUiState, SettingsUiIntent>(
    SettingsUiState()
), KoinComponent {

    val notificationState = repository.getNotificationState().onEach { isEnabled ->
        updateState { it.copy(notificationsEnabled = isEnabled) }
    }.launchIn(viewModelScope)

    val themeState = repository.getThemeState().onEach { mode ->
        updateState { it.copy(themeMode = mode) }
    }.launchIn(viewModelScope)

    /**
     * Handles UI events from the Settings screen.
     */
    fun handleUiEvent(event: SettingsUiEvent) {
        when (event) {
            is SettingsUiEvent.ToggleNotifications -> {
                repository.updateNotificationState(event.enabled)
            }

            is SettingsUiEvent.SetThemeMode -> {
                repository.updateThemeState(event.mode)
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