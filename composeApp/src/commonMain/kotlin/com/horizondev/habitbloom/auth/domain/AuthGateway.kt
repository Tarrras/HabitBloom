package com.horizondev.habitbloom.auth.domain

import kotlinx.coroutines.flow.Flow

interface AuthGateway {
    fun observeSession(): Flow<AuthSession>
    suspend fun currentSession(): AuthSession
    suspend fun signInWithEmail(email: String, password: String): Result<AuthSession>
    suspend fun signUpWithEmail(email: String, password: String): Result<AuthSession>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun signInWithExternalTokens(tokens: ExternalAuthTokens): Result<AuthSession>
    suspend fun signOut(): Result<Unit>
}
