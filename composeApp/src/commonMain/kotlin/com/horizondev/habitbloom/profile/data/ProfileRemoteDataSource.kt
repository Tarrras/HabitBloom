package com.horizondev.habitbloom.profile.data

import com.horizondev.habitbloom.profile.data.model.UserProfileResponse
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
}