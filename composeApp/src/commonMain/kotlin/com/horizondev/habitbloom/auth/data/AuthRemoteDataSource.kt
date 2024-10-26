package com.horizondev.habitbloom.auth.data

import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class AuthRemoteDataSource(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun isUserLoggedIn(): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            firebaseAuth.currentUser?.uid != null
        }
    }

    suspend fun signInAnonymously(): Result<String?> = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            firebaseAuth.signInAnonymously().user?.uid
        }
    }
}