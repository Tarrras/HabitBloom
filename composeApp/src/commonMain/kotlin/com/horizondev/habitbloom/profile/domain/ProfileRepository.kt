package com.horizondev.habitbloom.profile.domain

import com.horizondev.habitbloom.profile.data.ProfileRemoteDataSource
import com.horizondev.habitbloom.profile.data.model.toDomainModel

class ProfileRepository(
    private val remoteDataSource: ProfileRemoteDataSource
) {
    suspend fun getUserInfo() = remoteDataSource.getUser().mapCatching { it.toDomainModel() }
}