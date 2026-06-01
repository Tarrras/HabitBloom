package com.horizondev.habitbloom.auth.presentation.signin

import com.horizondev.habitbloom.auth.domain.AuthProvider
import com.horizondev.habitbloom.auth.domain.AuthSession
import com.horizondev.habitbloom.auth.domain.AuthUser
import com.horizondev.habitbloom.auth.presentation.resetpassword.SettingsResetPasswordUiState
import com.horizondev.habitbloom.auth.presentation.resetpassword.SettingsResetPasswordValidation
import com.horizondev.habitbloom.auth.presentation.resetpassword.validateResetPasswordForm
import com.horizondev.habitbloom.screens.settings.presentation.SettingsAuthProfileLabels
import com.horizondev.habitbloom.screens.settings.presentation.SettingsUiState
import com.horizondev.habitbloom.screens.settings.presentation.reduceAuthSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsAuthReducersTest {

    @Test
    fun guestSessionCreatesGuestProfile() {
        val state = reduceAuthSession(SettingsUiState(), AuthSession.Guest, authProfileLabels)

        assertFalse(state.authProfile.isAuthenticated)
        assertEquals("Guest", state.authProfile.title)
        assertEquals("Sign in to protect your progress", state.authProfile.subtitle)
        assertEquals(AuthProvider.Guest, state.authProfile.provider)
    }

    @Test
    fun authenticatedSessionCreatesEmailProfile() {
        val session = AuthSession(
            user = AuthUser("id-1", "user@example.com", null, AuthProvider.Email),
            isAuthenticated = true
        )

        val state = reduceAuthSession(SettingsUiState(), session, authProfileLabels)

        assertTrue(state.authProfile.isAuthenticated)
        assertEquals("user@example.com", state.authProfile.title)
        assertEquals("user@example.com", state.authProfile.subtitle)
        assertEquals(AuthProvider.Email, state.authProfile.provider)
    }

    @Test
    fun selectingSignInClearsAuthFormState() {
        val state = reduceAuthModeSelected(
            SettingsAuthUiState(
                authMode = SettingsAuthMode.SignUp,
                authName = "User",
                authPassword = "secret-password",
                authError = "Error",
                showEmailVerificationSheet = true,
                isAuthLoading = true
            ),
            SettingsAuthMode.SignIn
        )

        assertEquals(SettingsAuthMode.SignIn, state.authMode)
        assertEquals(null, state.authError)
        assertEquals("", state.authName)
        assertEquals("", state.authPassword)
        assertFalse(state.showEmailVerificationSheet)
        assertFalse(state.isAuthLoading)
    }

    @Test
    fun dismissingEmailVerificationSheetClearsVisibilityOnly() {
        val state = reduceEmailVerificationSheetDismissed(
            SettingsAuthUiState(
                authEmail = "user@example.com",
                showEmailVerificationSheet = true
            )
        )

        assertEquals("user@example.com", state.authEmail)
        assertFalse(state.showEmailVerificationSheet)
    }

    @Test
    fun invalidAuthFormSetsReadableError() {
        val state = SettingsAuthUiState(authEmail = "bad", authPassword = "12345")

        val result = validateAuthForm(state)

        assertTrue(result is SettingsAuthFormValidation.Invalid)
        assertEquals(SettingsAuthValidationError.InvalidEmailOrPassword, result.reason)
    }

    @Test
    fun signUpAuthFormRequiresName() {
        val state = SettingsAuthUiState(
            authMode = SettingsAuthMode.SignUp,
            authEmail = "user@example.com",
            authPassword = "strong-password"
        )

        val result = validateAuthForm(state)

        assertTrue(result is SettingsAuthFormValidation.Invalid)
        assertEquals(SettingsAuthValidationError.NameRequired, result.reason)
    }

    @Test
    fun resetPasswordFormRequiresMatchingPasswords() {
        val result = validateResetPasswordForm(
            SettingsResetPasswordUiState(
                password = "strong-password",
                confirmPassword = "other-password"
            )
        )

        assertEquals(SettingsResetPasswordValidation.PasswordsDoNotMatch, result)
    }

    @Test
    fun resetPasswordFormAcceptsStrongMatchingPasswords() {
        val result = validateResetPasswordForm(
            SettingsResetPasswordUiState(
                password = "strong-password",
                confirmPassword = "strong-password"
            )
        )

        assertEquals(SettingsResetPasswordValidation.Valid, result)
    }

    private companion object {
        val authProfileLabels = SettingsAuthProfileLabels(
            accountTitle = "HabitBloom account",
            guestTitle = "Guest",
            guestSubtitle = "Sign in to protect your progress",
            googleSubtitle = "Signed in with Google",
            emailSubtitle = "Signed in with email",
            appleSubtitle = "Signed in with Apple",
            unknownSubtitle = "Signed in"
        )
    }
}
