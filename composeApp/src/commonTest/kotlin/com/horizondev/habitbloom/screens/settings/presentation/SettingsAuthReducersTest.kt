package com.horizondev.habitbloom.screens.settings.presentation

import com.horizondev.habitbloom.auth.domain.AuthProvider
import com.horizondev.habitbloom.auth.domain.AuthSession
import com.horizondev.habitbloom.auth.domain.AuthUser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsAuthReducersTest {

    @Test
    fun guestSessionCreatesGuestProfile() {
        val state = reduceAuthSession(SettingsUiState(), AuthSession.Guest)

        assertFalse(state.authProfile.isAuthenticated)
        assertEquals(AuthProvider.Guest, state.authProfile.provider)
    }

    @Test
    fun authenticatedSessionCreatesEmailProfile() {
        val session = AuthSession(
            user = AuthUser("id-1", "user@example.com", null, AuthProvider.Email),
            isAuthenticated = true
        )

        val state = reduceAuthSession(SettingsUiState(), session)

        assertTrue(state.authProfile.isAuthenticated)
        assertEquals("user@example.com", state.authProfile.title)
        assertEquals(AuthProvider.Email, state.authProfile.provider)
    }

    @Test
    fun openSignInShowsAuthSheetInSignInMode() {
        val state = reduceAuthSheetOpened(SettingsUiState(), SettingsAuthMode.SignIn)

        assertTrue(state.showAuthSheet)
        assertEquals(SettingsAuthMode.SignIn, state.authMode)
        assertEquals(null, state.authError)
    }

    @Test
    fun invalidAuthFormSetsReadableError() {
        val state = SettingsUiState(authEmail = "bad", authPassword = "12345")

        val result = validateAuthForm(state)

        assertTrue(result is SettingsAuthFormValidation.Invalid)
        assertEquals("Enter a valid email and password.", result.message)
    }
}
