package com.horizondev.habitbloom.auth.domain

class AuthRepository(
    private val gateway: AuthGateway
) {
    fun observeSession() = gateway.observeSession()

    suspend fun currentSession(): AuthSession = gateway.currentSession()

    suspend fun initUser(): Result<Boolean> {
        return runCatching {
            gateway.currentSession()
            true
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<AuthSession> {
        return gateway.signInWithEmail(email.trim(), password)
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<AuthSession> {
        return gateway.signUpWithEmail(email.trim(), password)
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return gateway.resetPassword(email.trim())
    }

    suspend fun signInWithGoogle(tokens: ExternalAuthTokens): Result<AuthSession> {
        return gateway.signInWithExternalTokens(tokens.copy(provider = AuthProvider.Google))
    }

    suspend fun signOut(): Result<Unit> = gateway.signOut()
}
