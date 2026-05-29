package com.horizondev.habitbloom.auth.domain

data class AuthUser(
    val id: String,
    val email: String?,
    val displayName: String?,
    val provider: AuthProvider
)

data class AuthSession(
    val user: AuthUser?,
    val isAuthenticated: Boolean
) {
    companion object {
        val Guest = AuthSession(user = null, isAuthenticated = false)
    }
}

enum class AuthProvider {
    Guest,
    Email,
    Google,
    Apple,
    Unknown
}

data class ExternalAuthTokens(
    val idToken: String,
    val accessToken: String? = null,
    val provider: AuthProvider
)

sealed interface AuthFailure {
    data object InvalidCredentials : AuthFailure
    data object NetworkUnavailable : AuthFailure
    data object ProviderUnavailable : AuthFailure
    data class Unknown(val message: String) : AuthFailure
}
