package com.horizondev.habitbloom.auth.domain

import com.horizondev.habitbloom.auth.data.AuthRemoteDataSource
import io.github.aakira.napier.Napier

class AuthRepository(
    private val remoteDataSource: AuthRemoteDataSource
) {
    suspend fun initUser(): Result<Boolean> {
        return remoteDataSource.isUserLoggedIn().mapCatching { isUserLoggedIn ->
            when (isUserLoggedIn) {
                true -> {
                    Napier.d("User logged in")
                    true
                }

                false -> {
                    Napier.d("User not logged in")
                    remoteDataSource.signInAnonymously().map { uid ->
                        Napier.d("User logged in anonymously with $uid")
                        uid != null
                    }.getOrThrow()
                }
            }
        }
    }

}