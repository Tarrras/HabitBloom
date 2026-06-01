package com.horizondev.habitbloom.auth.presentation.resetpassword

data class SettingsResetPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false
)

sealed interface SettingsResetPasswordUiEvent {
    data class UpdatePassword(val password: String) : SettingsResetPasswordUiEvent
    data class UpdateConfirmPassword(val password: String) : SettingsResetPasswordUiEvent
    data object Submit : SettingsResetPasswordUiEvent
}

sealed interface SettingsResetPasswordValidation {
    data object Valid : SettingsResetPasswordValidation
    data object PasswordTooShort : SettingsResetPasswordValidation
    data object PasswordsDoNotMatch : SettingsResetPasswordValidation
}

fun validateResetPasswordForm(
    state: SettingsResetPasswordUiState
): SettingsResetPasswordValidation {
    return when {
        state.password.length < MinResetPasswordLength -> SettingsResetPasswordValidation.PasswordTooShort
        state.password != state.confirmPassword -> SettingsResetPasswordValidation.PasswordsDoNotMatch
        else -> SettingsResetPasswordValidation.Valid
    }
}

private const val MinResetPasswordLength = 6
