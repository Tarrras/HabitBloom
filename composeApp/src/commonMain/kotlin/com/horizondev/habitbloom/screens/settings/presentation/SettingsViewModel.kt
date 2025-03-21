package com.horizondev.habitbloom.screens.settings.presentation

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.common.settings.NotificationState
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.settings.domain.ProfileRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

/**
 * ViewModel for the Settings screen.
 */
class SettingsViewModel(
    private val repository: ProfileRepository,
    private val themeUseCase: ThemeUseCase
) : BloomViewModel<SettingsUiState, SettingsUiIntent>(
    SettingsUiState()
), KoinComponent {

    init {
        // Listen for notification state changes
        repository.getNotificationStateFlow().onEach { state ->
            updateState { it.copy(notificationState = state) }
        }.launchIn(viewModelScope)

        // If notification state flow is not available, get the current state
        viewModelScope.launch {
            val currentState = repository.getNotificationStateEnum()
            updateState { it.copy(notificationState = currentState) }
        }

        // Listen for theme changes
        themeUseCase.themeMode.onEach { mode ->
            updateState { it.copy(themeMode = mode) }
        }.launchIn(viewModelScope)
    }

    /**
     * Handles UI events from the Settings screen.
     */
    fun handleUiEvent(event: SettingsUiEvent) {
        when (event) {
            is SettingsUiEvent.ToggleNotifications -> {
                viewModelScope.launch {
                    val newState =
                        if (event.enabled) NotificationState.ENABLED else NotificationState.DISABLED
                    repository.updateNotificationState(newState)
                }
            }

            is SettingsUiEvent.SetThemeMode -> {
                viewModelScope.launch {
                    themeUseCase.updateThemeMode(event.mode)
                    updateState { it.copy(isThemeDialogVisible = false) }
                }
            }

            is SettingsUiEvent.Logout -> {
                launch {
                    //repository.logout()
                    emitUiIntent(SettingsUiIntent.NavigateToLogin)
                }
            }

            SettingsUiEvent.OpenThemeDialog -> {
                updateState { it.copy(isThemeDialogVisible = true) }
            }

            SettingsUiEvent.CloseThemeDialog -> {
                updateState { it.copy(isThemeDialogVisible = false) }
            }
        }
    }
} 