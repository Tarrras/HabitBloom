package com.horizondev.habitbloom.auth.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthRepositoryTest {

    @Test
    fun currentSessionDefaultsToGuestWhenGatewayHasNoUser() = runBlocking {
        val gateway = FakeAuthGateway(AuthSession.Guest)
        val repository = AuthRepository(gateway)

        assertEquals(AuthSession.Guest, repository.currentSession())
    }

    @Test
    fun signInWithEmailDelegatesToGateway() = runBlocking {
        val user = AuthUser("user-1", "user@example.com", "User", AuthProvider.Email)
        val gateway = FakeAuthGateway(AuthSession(user, isAuthenticated = true))
        val repository = AuthRepository(gateway)

        val result = repository.signInWithEmail("user@example.com", "strong-password")

        assertTrue(result.isSuccess)
        assertEquals(AuthSession(user, true), result.getOrThrow())
        assertEquals("user@example.com", gateway.lastEmail)
    }

    private class FakeAuthGateway(initialSession: AuthSession) : AuthGateway {
        private val session = MutableStateFlow(initialSession)
        var lastEmail: String? = null

        override fun observeSession() = session

        override suspend fun currentSession() = session.value

        override suspend fun signInWithEmail(email: String, password: String): Result<AuthSession> {
            lastEmail = email
            return Result.success(session.value)
        }

        override suspend fun signUpWithEmail(email: String, password: String) =
            Result.success(session.value)

        override suspend fun resetPassword(email: String) = Result.success(Unit)

        override suspend fun signInWithProvider(provider: AuthProvider) = Result.success(Unit)

        override suspend fun signInWithExternalTokens(tokens: ExternalAuthTokens) =
            Result.success(session.value)

        override suspend fun signOut() = Result.success(Unit)
    }
}
