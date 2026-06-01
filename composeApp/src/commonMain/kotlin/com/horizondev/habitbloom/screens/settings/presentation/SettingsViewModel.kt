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
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.settings_auth_account_title
import habitbloom.composeapp.generated.resources.settings_auth_provider_apple
import habitbloom.composeapp.generated.resources.settings_auth_provider_email
import habitbloom.composeapp.generated.resources.settings_auth_provider_google
import habitbloom.composeapp.generated.resources.settings_auth_provider_unknown
import habitbloom.composeapp.generated.resources.settings_guest_profile
import habitbloom.composeapp.generated.resources.settings_guest_profile_subtitle
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

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
) {
    private val TAG = "SettingsViewModel"
    private var authProfileLabels: SettingsAuthProfileLabels? = null

    init {
        repository.getNotificationStateFlow().onEach { state ->
            updateState { it.copy(notificationState = state) }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            val currentState = repository.getNotificationStateEnum()
            updateState { it.copy(notificationState = currentState) }
        }

        themeUseCase.themeModeFlow.onEach { mode ->
            updateState { it.copy(themeMode = mode) }
        }.launchIn(viewModelScope)

        timeFormatUseCase.timeFormatFlow.onEach { option ->
            updateState { it.copy(timeFormat = option) }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            val labels = getAuthProfileLabels()
            val session = authRepository.currentSession()
            updateState { reduceAuthSession(it, session, labels) }
        }

        authRepository.observeSession().onEach { session ->
            val labels = authProfileLabels ?: loadAuthProfileLabels().also {
                authProfileLabels = it
            }
            updateState { reduceAuthSession(it, session, labels) }
        }.launchIn(viewModelScope)
    }

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
                    val labels = getAuthProfileLabels()
                    updateState {
                        reduceAuthSession(
                            state = it,
                            session = AuthSession.Guest,
                            labels = labels
                        )
                    }
                }
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
                            updateState { it.copy(isLoading = false) }
                        }
                }
            }
        }
    }

    private fun signInWithGoogle() {
        launch {
            authRepository.signInWithProvider(AuthProvider.Google)
                .onSuccess {
                    val session = authRepository.currentSession()
                    val labels = getAuthProfileLabels()
                    updateState {
                        reduceAuthSession(
                            state = it,
                            session = session,
                            labels = labels
                        )
                    }
                }
                .onFailure { error ->
                    Napier.e("Google sign in failed.", error, tag = TAG)
                }
        }
    }

    private suspend fun getAuthProfileLabels(): SettingsAuthProfileLabels {
        return authProfileLabels ?: loadAuthProfileLabels().also {
            authProfileLabels = it
        }
    }

    private suspend fun loadAuthProfileLabels(): SettingsAuthProfileLabels {
        return SettingsAuthProfileLabels(
            accountTitle = getString(Res.string.settings_auth_account_title),
            guestTitle = getString(Res.string.settings_guest_profile),
            guestSubtitle = getString(Res.string.settings_guest_profile_subtitle),
            googleSubtitle = getString(Res.string.settings_auth_provider_google),
            emailSubtitle = getString(Res.string.settings_auth_provider_email),
            appleSubtitle = getString(Res.string.settings_auth_provider_apple),
            unknownSubtitle = getString(Res.string.settings_auth_provider_unknown)
        )
    }
}
