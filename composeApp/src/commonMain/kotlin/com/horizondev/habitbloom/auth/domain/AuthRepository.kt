package com.horizondev.habitbloom.auth.domain

class AuthRepository(
    private val gateway: AuthGateway
) {
    fun observeSession() = gateway.observeSession()

    suspend fun currentSession(): AuthSession = gateway.currentSession()

    suspend fun signInWithEmail(email: String, password: String): Result<AuthSession> {
        return gateway.signInWithEmail(email.trim(), password)
    }

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String? = null
    ): Result<AuthSession> {
        return gateway.signUpWithEmail(email.trim(), password, displayName?.trim())
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return gateway.resetPassword(email.trim())
    }

    suspend fun updatePassword(password: String): Result<AuthSession> {
        return gateway.updatePassword(password)
    }

    suspend fun signInWithProvider(provider: AuthProvider): Result<Unit> {
        return gateway.signInWithProvider(provider)
    }

    suspend fun signInWithGoogle(tokens: ExternalAuthTokens): Result<AuthSession> {
        return gateway.signInWithExternalTokens(tokens.copy(provider = AuthProvider.Google))
    }

    suspend fun signOut(): Result<Unit> = gateway.signOut()
}
