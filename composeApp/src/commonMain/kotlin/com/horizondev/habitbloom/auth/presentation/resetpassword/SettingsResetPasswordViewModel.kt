package com.horizondev.habitbloom.auth.presentation.resetpassword

import com.horizondev.habitbloom.auth.domain.AuthRepository
import com.horizondev.habitbloom.core.viewmodel.BloomViewModel
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.settings_reset_password_error_failed
import habitbloom.composeapp.generated.resources.settings_reset_password_error_mismatch
import habitbloom.composeapp.generated.resources.settings_reset_password_error_short
import org.jetbrains.compose.resources.getString

class SettingsResetPasswordViewModel(
    private val authRepository: AuthRepository
) : BloomViewModel<SettingsResetPasswordUiState, Nothing>(SettingsResetPasswordUiState()) {

    fun handleUiEvent(event: SettingsResetPasswordUiEvent) {
        when (event) {
            is SettingsResetPasswordUiEvent.UpdatePassword -> {
                updateState {
                    it.copy(
                        password = event.password,
                        error = null
                    )
                }
            }

            is SettingsResetPasswordUiEvent.UpdateConfirmPassword -> {
                updateState {
                    it.copy(
                        confirmPassword = event.password,
                        error = null
                    )
                }
            }

            SettingsResetPasswordUiEvent.Submit -> updatePassword()
        }
    }

    private fun updatePassword() {
        launch {
            val current = state.value
            when (val validation = validateResetPasswordForm(current)) {
                SettingsResetPasswordValidation.Valid -> Unit
                SettingsResetPasswordValidation.PasswordTooShort -> {
                    val message = getString(Res.string.settings_reset_password_error_short)
                    updateState {
                        it.copy(error = message)
                    }
                    return@launch
                }

                SettingsResetPasswordValidation.PasswordsDoNotMatch -> {
                    val message = getString(Res.string.settings_reset_password_error_mismatch)
                    updateState {
                        it.copy(error = message)
                    }
                    return@launch
                }
            }

            updateState {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            authRepository.updatePassword(current.password)
                .onSuccess {
                    updateState {
                        it.copy(
                            isLoading = false,
                            isCompleted = true,
                            password = "",
                            confirmPassword = ""
                        )
                    }
                }
                .onFailure { error ->
                    val message = error.message
                        ?: getString(Res.string.settings_reset_password_error_failed)
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = message
                        )
                    }
                }
        }
    }
}
