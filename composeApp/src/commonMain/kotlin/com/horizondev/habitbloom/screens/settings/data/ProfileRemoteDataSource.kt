package com.horizondev.habitbloom.screens.settings.data

import com.horizondev.habitbloom.screens.settings.data.model.UserProfileResponse
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ProfileRemoteDataSource(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun getUser(): Result<UserProfileResponse> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            firebaseAuth.currentUser?.let { user ->
                UserProfileResponse(
                    id = user.uid,
                    username = user.displayName
                )
            } ?: throw IllegalStateException("User is null")
        }
    }

    /**
     * Checks if the user is currently authenticated
     * @return Result containing a boolean indicating authentication status
     */
    suspend fun isUserAuthenticated(): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            firebaseAuth.currentUser != null
        }
    }

    /**
     * Authenticates the user anonymously if not already authenticated
     * @return Result containing a boolean indicating success
     */
    suspend fun authenticateUser(): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            val currentUser = firebaseAuth.currentUser

            if (currentUser != null) {
                // Already authenticated
                true
            } else {
                // Perform anonymous authentication
                val result = firebaseAuth.signInAnonymously()
                result.user != null
            }
        }
    }
}