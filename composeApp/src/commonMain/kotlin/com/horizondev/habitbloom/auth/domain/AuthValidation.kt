package com.horizondev.habitbloom.auth.domain

sealed interface AuthInputValidation {
    data object Valid : AuthInputValidation
    data class Invalid(val errors: List<AuthInputError>) : AuthInputValidation
}

enum class AuthInputError {
    EmailRequired,
    EmailInvalid,
    PasswordRequired,
    PasswordTooShort
}

fun validateEmailPassword(email: String, password: String): AuthInputValidation {
    val errors = buildList {
        val trimmedEmail = email.trim()
        when {
            trimmedEmail.isEmpty() -> add(AuthInputError.EmailRequired)
            "@" !in trimmedEmail || "." !in trimmedEmail.substringAfter("@", "") -> {
                add(AuthInputError.EmailInvalid)
            }
        }

        when {
            password.isEmpty() -> add(AuthInputError.PasswordRequired)
            password.length < MIN_PASSWORD_LENGTH -> add(AuthInputError.PasswordTooShort)
        }
    }

    return if (errors.isEmpty()) {
        AuthInputValidation.Valid
    } else {
        AuthInputValidation.Invalid(errors)
    }
}

private const val MIN_PASSWORD_LENGTH = 6
