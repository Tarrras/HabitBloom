package com.horizondev.habitbloom.profile.data.model

import com.horizondev.habitbloom.profile.domain.model.UserProfileInfo

data class UserProfileResponse(
    val id: String,
    val username: String?
)

fun UserProfileResponse.toDomainModel() = UserProfileInfo(
    id = id, username = username
)
