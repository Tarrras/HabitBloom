package com.horizondev.habitbloom.auth.presentation.signin

import androidx.lifecycle.viewModelScope
import com.horizondev.habitbloom.auth.domain.AuthRepository
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.settings_auth_error_authentication_failed
import habitbloom.composeapp.generated.resources.settings_auth_error_enter_email_first
import habitbloom.composeapp.generated.resources.settings_auth_error_invalid_credentials
import habitbloom.composeapp.generated.resources.settings_auth_error_name_required
import habitbloom.composeapp.generated.resources.settings_auth_error_password_reset_failed
import habitbloom.composeapp.generated.resources.settings_auth_password_reset_sent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.getString

class SettingsAuthViewModel(
    private val authRepository: AuthRepository
) : BloomViewModel<SettingsAuthUiState, Nothing>(SettingsAuthUiState()) {

    init {
        authRepository.observeSession().onEach { session ->
            updateState {
                it.copy(
                    isAuthenticated = session.isAuthenticated,
                    isAuthLoading = false
                )
            }
        }.launchIn(viewModelScope)

        launch {
            val session = authRepository.currentSession()
            updateState { it.copy(isAuthenticated = session.isAuthenticated) }
        }
    }

    fun handleUiEvent(event: SettingsAuthUiEvent) {
        when (event) {
            is SettingsAuthUiEvent.SetMode -> {
                updateState { reduceAuthModeSelected(it, event.mode) }
            }

            is SettingsAuthUiEvent.UpdateName -> {
                updateState {
                    it.copy(
                        authName = event.name,
                        authError = null,
                        showEmailVerificationSheet = false
                    )
                }
            }

            is SettingsAuthUiEvent.UpdateEmail -> {
                updateState {
                    it.copy(
                        authEmail = event.email,
                        authError = null,
                        showEmailVerificationSheet = false
                    )
                }
            }

            is SettingsAuthUiEvent.UpdatePassword -> {
                updateState {
                    it.copy(
                        authPassword = event.password,
                        authError = null,
                        showEmailVerificationSheet = false
                    )
                }
            }

            SettingsAuthUiEvent.SubmitEmailAuth -> {
                submitEmailAuth()
            }

            SettingsAuthUiEvent.ResetPassword -> {
                resetPassword()
            }

            SettingsAuthUiEvent.DismissEmailVerificationSheet -> {
                updateState { reduceEmailVerificationSheetDismissed(it) }
            }
        }
    }

    private fun submitEmailAuth() {
        launch {
            val current = state.value
            when (val validation = validateAuthForm(current)) {
                SettingsAuthFormValidation.Valid -> Unit
                is SettingsAuthFormValidation.Invalid -> {
                    val message = validation.reason.localizedMessage()
                    updateState { it.copy(authError = message) }
                    return@launch
                }
            }

            updateState {
                it.copy(
                    isAuthLoading = true,
                    authError = null,
                    showEmailVerificationSheet = false
                )
            }

            val result = when (current.authMode) {
                SettingsAuthMode.SignIn -> {
                    authRepository.signInWithEmail(current.authEmail, current.authPassword)
                }

                SettingsAuthMode.SignUp -> {
                    authRepository.signUpWithEmail(
                        email = current.authEmail,
                        password = current.authPassword,
                        displayName = current.authName
                    )
                }
            }

            result
                .onSuccess { session ->
                    updateState {
                        it.copy(
                            isAuthenticated = session.isAuthenticated,
                            isAuthLoading = false,
                            authName = "",
                            authPassword = "",
                            showEmailVerificationSheet = current.authMode == SettingsAuthMode.SignUp &&
                                    !session.isAuthenticated
                        )
                    }
                }
                .onFailure { error ->
                    val message = error.message
                        ?: getString(Res.string.settings_auth_error_authentication_failed)
                    updateState {
                        it.copy(
                            isAuthLoading = false,
                            authError = message
                        )
                    }
                }
        }
    }

    private fun resetPassword() {
        launch {
            val email = state.value.authEmail.trim()
            if ("@" !in email) {
                val message = getString(Res.string.settings_auth_error_enter_email_first)
                updateState {
                    it.copy(authError = message)
                }
                return@launch
            }

            updateState {
                it.copy(
                    isAuthLoading = true,
                    authError = null,
                    showEmailVerificationSheet = false
                )
            }
            authRepository.resetPassword(email)
                .onSuccess {
                    val message = getString(Res.string.settings_auth_password_reset_sent)
                    updateState {
                        it.copy(
                            isAuthLoading = false,
                            authError = message
                        )
                    }
                }
                .onFailure { error ->
                    val message = error.message
                        ?: getString(Res.string.settings_auth_error_password_reset_failed)
                    updateState {
                        it.copy(
                            isAuthLoading = false,
                            authError = message
                        )
                    }
                }
        }
    }

    private suspend fun SettingsAuthValidationError.localizedMessage(): String {
        return when (this) {
            SettingsAuthValidationError.NameRequired -> {
                getString(Res.string.settings_auth_error_name_required)
            }

            SettingsAuthValidationError.InvalidEmailOrPassword -> {
                getString(Res.string.settings_auth_error_invalid_credentials)
            }
        }
    }
}
