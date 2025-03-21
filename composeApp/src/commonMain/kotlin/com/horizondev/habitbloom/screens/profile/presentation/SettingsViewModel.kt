package com.horizondev.habitbloom.screens.profile.presentation

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.profile.domain.ProfileRepository
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
        repository.getNotificationState().onEach { isEnabled ->
            updateState { it.copy(notificationsEnabled = isEnabled) }
        }.launchIn(viewModelScope)

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
                    repository.updateNotificationState(event.enabled)
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