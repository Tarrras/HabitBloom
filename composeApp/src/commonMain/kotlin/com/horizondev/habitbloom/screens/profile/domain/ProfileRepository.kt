package com.horizondev.habitbloom.screens.profile.domain

import com.horizondev.habitbloom.common.settings.SETTINGS_NOTIFICATIONS_KEY
import com.horizondev.habitbloom.common.settings.SETTINGS_THEME_KEY
import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.screens.profile.data.ProfileRemoteDataSource
import com.horizondev.habitbloom.screens.profile.data.model.toDomainModel
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.map

class ProfileRepository(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val settings: ObservableSettings
) {
    suspend fun getUserInfo() = remoteDataSource.getUser().mapCatching { it.toDomainModel() }

    fun getNotificationState() = settings.getBooleanFlow(SETTINGS_NOTIFICATIONS_KEY, false)

    fun updateNotificationState(
        isEnabled: Boolean
    ): Result<Unit> {
        return runCatching { settings[SETTINGS_NOTIFICATIONS_KEY] = isEnabled }
    }

    fun getThemeState() = settings
        .getStringFlow(SETTINGS_THEME_KEY, ThemeOption.Device.toString())
        .map { ThemeOption.valueOf(it) }

    fun updateThemeState(
        option: ThemeOption
    ): Result<Unit> {
        return runCatching { settings[SETTINGS_THEME_KEY] = option.toString() }
    }
}