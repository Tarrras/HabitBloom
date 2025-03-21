package com.horizondev.habitbloom.screens.settings.data.model

import com.horizondev.habitbloom.screens.settings.domain.model.UserProfileInfo

data class UserProfileResponse(
    val id: String,
    val username: String?
)

fun UserProfileResponse.toDomainModel() = UserProfileInfo(
    id = id, username = username
)
