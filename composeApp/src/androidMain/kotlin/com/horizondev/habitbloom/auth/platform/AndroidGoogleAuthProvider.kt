package com.horizondev.habitbloom.auth.platform

import com.horizondev.habitbloom.auth.domain.AuthFailure
import com.horizondev.habitbloom.auth.domain.ExternalAuthTokens

class AndroidGoogleAuthProvider : GoogleAuthProvider {
    override suspend fun requestTokens(): Result<ExternalAuthTokens> {
        return Result.failure(
            IllegalStateException(AuthFailure.ProviderUnavailable.toString())
        )
    }
}
