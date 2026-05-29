package com.horizondev.habitbloom.auth.platform

import com.horizondev.habitbloom.auth.domain.ExternalAuthTokens

interface GoogleAuthProvider {
    suspend fun requestTokens(): Result<ExternalAuthTokens>
}
