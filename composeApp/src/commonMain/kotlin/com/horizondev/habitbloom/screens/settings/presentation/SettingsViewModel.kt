package com.horizondev.habitbloom.screens.settings.presentation

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.auth.domain.AuthProvider
import com.horizondev.habitbloom.auth.domain.AuthRepository
import com.horizondev.habitbloom.auth.domain.AuthSession
import com.horizondev.habitbloom.common.settings.NotificationState
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.core.time.TimeFormatUseCase
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import com.horizondev.habitbloom.screens.settings.domain.ProfileRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

/**
 * ViewModel for the Settings screen.
 */
class SettingsViewModel(
    private val repository: ProfileRepository,
    private val themeUseCase: ThemeUseCase,
    private val timeFormatUseCase: TimeFormatUseCase,
    private val authRepository: AuthRepository
) : BloomViewModel<SettingsUiState, SettingsUiIntent>(
    SettingsUiState()
), KoinComponent {
    private val TAG = "SettingsViewModel"

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
        themeUseCase.themeModeFlow.onEach { mode ->
            updateState { it.copy(themeMode = mode) }
        }.launchIn(viewModelScope)

        timeFormatUseCase.timeFormatFlow.onEach { option ->
            updateState { it.copy(timeFormat = option) }
        }.launchIn(viewModelScope)

        authRepository.observeSession().onEach { session ->
            updateState { reduceAuthSession(it, session) }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            val session = authRepository.currentSession()
            updateState { reduceAuthSession(it, session) }
        }
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

            is SettingsUiEvent.SetTimeFormat -> {
                viewModelScope.launch {
                    timeFormatUseCase.updateTimeFormat(event.option)
                }
            }

            is SettingsUiEvent.Logout -> {
                launch {
                    authRepository.signOut()
                    updateState { reduceAuthSession(it, AuthSession.Guest) }
                }
            }

            SettingsUiEvent.OpenSignIn -> {
                updateState { reduceAuthSheetOpened(it, SettingsAuthMode.SignIn) }
            }

            SettingsUiEvent.OpenSignUp -> {
                updateState { reduceAuthSheetOpened(it, SettingsAuthMode.SignUp) }
            }

            SettingsUiEvent.CloseAuthSheet -> {
                updateState(::reduceAuthSheetClosed)
            }

            is SettingsUiEvent.UpdateAuthEmail -> {
                updateState { it.copy(authEmail = event.email, authError = null) }
            }

            is SettingsUiEvent.UpdateAuthPassword -> {
                updateState { it.copy(authPassword = event.password, authError = null) }
            }

            SettingsUiEvent.SubmitEmailAuth -> {
                submitEmailAuth()
            }

            SettingsUiEvent.ResetPassword -> {
                resetPassword()
            }

            SettingsUiEvent.SignInWithGoogle -> {
                signInWithGoogle()
            }

            SettingsUiEvent.OpenThemeDialog -> {
                updateState { it.copy(isThemeDialogVisible = true) }
            }

            SettingsUiEvent.CloseThemeDialog -> {
                updateState { it.copy(isThemeDialogVisible = false) }
            }

            SettingsUiEvent.ShowDeleteDataDialog -> {
                updateState { it.copy(showDeleteDataDialog = true) }
            }

            SettingsUiEvent.DismissDeleteDataDialog -> {
                updateState { it.copy(showDeleteDataDialog = false) }
            }

            SettingsUiEvent.ConfirmDeleteData -> {
                launch {
                    updateState { it.copy(isLoading = true, showDeleteDataDialog = false) }

                    repository.resetAllAppData()
                        .onSuccess {
                            Napier.d("App data reset successfully", tag = TAG)
                            // Navigate to the onboarding flow
                            emitUiIntent(SettingsUiIntent.NavigateToOnboarding)
                        }
                        .onFailure { error ->
                            Napier.e("Failed to reset app data", error, tag = TAG)
                            // Stay on the current screen but update loading state
                            updateState { it.copy(isLoading = false) }
                        }
                }
            }
        }
    }

    private fun submitEmailAuth() {
        launch {
            val current = state.value
            when (val validation = validateAuthForm(current)) {
                SettingsAuthFormValidation.Valid -> Unit
                is SettingsAuthFormValidation.Invalid -> {
                    updateState { it.copy(authError = validation.message) }
                    return@launch
                }
            }

            updateState { it.copy(isAuthLoading = true, authError = null) }

            val result = when (current.authMode) {
                SettingsAuthMode.SignIn -> {
                    authRepository.signInWithEmail(current.authEmail, current.authPassword)
                }

                SettingsAuthMode.SignUp -> {
                    authRepository.signUpWithEmail(current.authEmail, current.authPassword)
                }
            }

            result
                .onSuccess { session ->
                    updateState {
                        reduceAuthSession(it, session).copy(
                            showAuthSheet = false,
                            authPassword = ""
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        it.copy(
                            isAuthLoading = false,
                            authError = error.message ?: "Authentication failed."
                        )
                    }
                }
        }
    }

    private fun resetPassword() {
        launch {
            val email = state.value.authEmail.trim()
            if ("@" !in email) {
                updateState { it.copy(authError = "Enter your email first.") }
                return@launch
            }

            updateState { it.copy(isAuthLoading = true, authError = null) }
            authRepository.resetPassword(email)
                .onSuccess {
                    updateState {
                        it.copy(
                            isAuthLoading = false,
                            authError = "Password reset email sent."
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        it.copy(
                            isAuthLoading = false,
                            authError = error.message ?: "Password reset failed."
                        )
                    }
                }
        }
    }

    private fun signInWithGoogle() {
        launch {
            updateState { it.copy(isAuthLoading = true, authError = null) }
            authRepository.signInWithProvider(AuthProvider.Google)
                .onSuccess {
                    updateState { it.copy(isAuthLoading = false) }
                }
                .onFailure { error ->
                    updateState {
                        it.copy(
                            isAuthLoading = false,
                            authError = error.message ?: "Google sign in failed."
                        )
                    }
                }
        }
    }
}
