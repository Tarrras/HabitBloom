package com.horizondev.habitbloom.auth.presentation.signin

import com.horizondev.habitbloom.auth.domain.AuthInputValidation
import com.horizondev.habitbloom.auth.domain.validateEmailPassword

enum class SettingsAuthMode {
    SignIn,
    SignUp
}

data class SettingsAuthUiState(
    val authMode: SettingsAuthMode = SettingsAuthMode.SignIn,
    val authName: String = "",
    val authEmail: String = "",
    val authPassword: String = "",
    val authError: String? = null,
    val showEmailVerificationSheet: Boolean = false,
    val isAuthLoading: Boolean = false,
    val isAuthenticated: Boolean = false
)

sealed interface SettingsAuthUiEvent {
    data class SetMode(val mode: SettingsAuthMode) : SettingsAuthUiEvent
    data class UpdateName(val name: String) : SettingsAuthUiEvent
    data class UpdateEmail(val email: String) : SettingsAuthUiEvent
    data class UpdatePassword(val password: String) : SettingsAuthUiEvent
    data object SubmitEmailAuth : SettingsAuthUiEvent
    data object ResetPassword : SettingsAuthUiEvent
    data object DismissEmailVerificationSheet : SettingsAuthUiEvent
}

sealed interface SettingsAuthFormValidation {
    data object Valid : SettingsAuthFormValidation
    data class Invalid(val reason: SettingsAuthValidationError) : SettingsAuthFormValidation
}

enum class SettingsAuthValidationError {
    NameRequired,
    InvalidEmailOrPassword
}

fun reduceAuthModeSelected(
    state: SettingsAuthUiState,
    mode: SettingsAuthMode
): SettingsAuthUiState {
    return state.copy(
        authMode = mode,
        authError = null,
        showEmailVerificationSheet = false,
        authName = if (mode == state.authMode) state.authName else "",
        authPassword = "",
        isAuthLoading = false
    )
}

fun reduceEmailVerificationSheetDismissed(state: SettingsAuthUiState): SettingsAuthUiState {
    return state.copy(showEmailVerificationSheet = false)
}

fun validateAuthForm(state: SettingsAuthUiState): SettingsAuthFormValidation {
    if (state.authMode == SettingsAuthMode.SignUp && state.authName.isBlank()) {
        return SettingsAuthFormValidation.Invalid(SettingsAuthValidationError.NameRequired)
    }

    return when (validateEmailPassword(state.authEmail, state.authPassword)) {
        AuthInputValidation.Valid -> SettingsAuthFormValidation.Valid
        is AuthInputValidation.Invalid -> {
            SettingsAuthFormValidation.Invalid(SettingsAuthValidationError.InvalidEmailOrPassword)
        }
    }
}
