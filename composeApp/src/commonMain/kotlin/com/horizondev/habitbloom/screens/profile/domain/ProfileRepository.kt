package com.horizondev.habitbloom.screens.profile.domain

import com.horizondev.habitbloom.screens.profile.data.ProfileRemoteDataSource
import com.horizondev.habitbloom.screens.profile.data.model.toDomainModel

class ProfileRepository(
    private val remoteDataSource: ProfileRemoteDataSource
) {
    suspend fun getUserInfo() = remoteDataSource.getUser().mapCatching { it.toDomainModel() }
}