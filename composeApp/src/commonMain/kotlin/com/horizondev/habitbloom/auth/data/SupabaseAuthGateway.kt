package com.horizondev.habitbloom.auth.data

import com.horizondev.habitbloom.auth.domain.AuthGateway
import com.horizondev.habitbloom.auth.domain.AuthProvider
import com.horizondev.habitbloom.auth.domain.AuthSession
import com.horizondev.habitbloom.auth.domain.AuthUser
import com.horizondev.habitbloom.auth.domain.ExternalAuthTokens
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.deepLink
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class SupabaseAuthGateway(
    private val supabaseClient: SupabaseClient
) : AuthGateway {
    private val auth = supabaseClient.auth

    override fun observeSession(): Flow<AuthSession> {
        return auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> status.session.toDomainSession()
                else -> AuthSession.Guest
            }
        }
    }

    override suspend fun currentSession(): AuthSession {
        auth.awaitInitialization()
        return auth.currentSessionOrNull()?.toDomainSession() ?: AuthSession.Guest
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthSession> {
        return runCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            currentSession()
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String?
    ): Result<AuthSession> {
        return runCatching {
            auth.signUpWith(Email, redirectUrl = authRedirectUrl) {
                this.email = email
                this.password = password
                val trimmedName = displayName?.trim().orEmpty()
                if (trimmedName.isNotBlank()) {
                    data = buildJsonObject {
                        put("name", trimmedName)
                        put("full_name", trimmedName)
                    }
                }
            }
            currentSession()
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return runCatching {
            auth.resetPasswordForEmail(email, redirectUrl = passwordRecoveryRedirectUrl)
        }
    }

    override suspend fun updatePassword(password: String): Result<AuthSession> {
        return runCatching {
            auth.updateUser {
                this.password = password
            }
            currentSession()
        }
    }

    override suspend fun signInWithProvider(provider: AuthProvider): Result<Unit> {
        return runCatching {
            when (provider) {
                AuthProvider.Google -> auth.signInWith(Google, redirectUrl = authRedirectUrl)
                AuthProvider.Email,
                AuthProvider.Apple,
                AuthProvider.Guest,
                AuthProvider.Unknown -> error("Auth provider is not supported yet.")
            }
        }
    }

    private val authRedirectUrl: String
        get() = auth.config.deepLink

    private val passwordRecoveryRedirectUrl: String
        get() = "${authRedirectUrl.trimEnd('/')}/reset-password"

    override suspend fun signInWithExternalTokens(tokens: ExternalAuthTokens): Result<AuthSession> {
        return runCatching {
            auth.signInWith(IDToken) {
                provider = Google
                idToken = tokens.idToken
                accessToken = tokens.accessToken
            }
            currentSession()
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return runCatching {
            auth.signOut()
        }
    }

    private fun UserSession.toDomainSession(): AuthSession {
        val userInfo = user ?: return AuthSession.Guest
        return AuthSession(
            user = userInfo.toDomainUser(),
            isAuthenticated = true
        )
    }

    private fun UserInfo.toDomainUser(): AuthUser {
        val providerName = identities?.firstOrNull()?.provider
        return AuthUser(
            id = id,
            email = email,
            displayName = userMetadata.stringValue("name")
                ?: userMetadata.stringValue("full_name"),
            provider = providerName.toAuthProvider()
        )
    }

    private fun String?.toAuthProvider(): AuthProvider {
        return when (this?.lowercase()) {
            "email" -> AuthProvider.Email
            "google" -> AuthProvider.Google
            "apple" -> AuthProvider.Apple
            null -> AuthProvider.Unknown
            else -> AuthProvider.Unknown
        }
    }

    private fun kotlinx.serialization.json.JsonObject?.stringValue(key: String): String? {
        val primitive = this?.get(key) as? JsonPrimitive ?: this?.get(key)?.jsonPrimitive
        return primitive?.contentOrNull
    }
}
