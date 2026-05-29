package com.horizondev.habitbloom.auth.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthValidationTest {

    @Test
    fun validEmailAndPasswordPasses() {
        val result = validateEmailPassword("user@example.com", "strong-password")

        assertEquals(AuthInputValidation.Valid, result)
    }

    @Test
    fun blankEmailFails() {
        val result = validateEmailPassword("", "strong-password")

        assertTrue(result is AuthInputValidation.Invalid)
        assertEquals(AuthInputError.EmailRequired, result.errors.first())
    }

    @Test
    fun shortPasswordFails() {
        val result = validateEmailPassword("user@example.com", "12345")

        assertTrue(result is AuthInputValidation.Invalid)
        assertEquals(AuthInputError.PasswordTooShort, result.errors.first())
    }
}
