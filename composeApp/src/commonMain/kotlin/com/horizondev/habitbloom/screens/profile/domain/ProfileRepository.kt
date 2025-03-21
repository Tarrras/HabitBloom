package com.horizondev.habitbloom.screens.profile.domain

import com.horizondev.habitbloom.common.settings.NotificationState
import com.horizondev.habitbloom.common.settings.SETTINGS_NOTIFICATIONS_KEY
import com.horizondev.habitbloom.common.settings.SETTINGS_NOTIFICATION_STATE_KEY
import com.horizondev.habitbloom.common.settings.SETTINGS_THEME_KEY
import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.core.permissions.PermissionsManager
import com.horizondev.habitbloom.screens.profile.data.ProfileRemoteDataSource
import com.horizondev.habitbloom.screens.profile.data.model.toDomainModel
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getStringFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val settings: ObservableSettings,
    private val permissionsManager: PermissionsManager
) {
    suspend fun getUserInfo() = remoteDataSource.getUser().mapCatching { it.toDomainModel() }

    /**
     * Legacy method - use getNotificationStateFlow() instead
     */
    fun getNotificationState() = settings.getBooleanFlow(SETTINGS_NOTIFICATIONS_KEY, false)

    /**
     * Get the current notification state
     */
    fun getNotificationStateEnum(): NotificationState {
        val savedState = settings.getStringOrNull(SETTINGS_NOTIFICATION_STATE_KEY)
        return if (savedState != null) {
            try {
                NotificationState.valueOf(savedState)
            } catch (e: Exception) {
                // Fallback to legacy boolean value if enum parse fails
                val legacyEnabled = settings.getBoolean(SETTINGS_NOTIFICATIONS_KEY, false)
                NotificationState.fromBoolean(legacyEnabled)
            }
        } else {
            // Check legacy setting if new setting is not present
            val legacyEnabled = settings.getBoolean(SETTINGS_NOTIFICATIONS_KEY, false)
            if (settings.hasKey(SETTINGS_NOTIFICATIONS_KEY)) {
                NotificationState.fromBoolean(legacyEnabled)
            } else {
                NotificationState.NOT_DETERMINED
            }
        }
    }

    /**
     * Get a flow of notification state changes
     */
    fun getNotificationStateFlow(): Flow<NotificationState> {
        return settings.getStringFlow(
            SETTINGS_NOTIFICATION_STATE_KEY,
            NotificationState.NOT_DETERMINED.toString()
        )
            .map {
                try {
                    NotificationState.valueOf(it)
                } catch (e: Exception) {
                    NotificationState.NOT_DETERMINED
                }
            }
    }

    /**
     * Update the notification state
     */
    suspend fun updateNotificationState(
        isEnabled: Boolean
    ): Result<Unit> {
        val newState = NotificationState.fromBoolean(isEnabled)
        return updateNotificationState(newState)
    }

    /**
     * Update the notification state with enum value
     */
    suspend fun updateNotificationState(
        state: NotificationState
    ): Result<Unit> {
        // For ENABLED state, check and request permission if needed
        if (state == NotificationState.ENABLED) {
            if (!permissionsManager.hasNotificationPermission()) {
                val permissionGranted = permissionsManager.requestNotificationPermission()
                if (!permissionGranted) {
                    return Result.failure(Exception("Notification permission denied"))
                }
            }
        }

        return runCatching {
            // Update both the new enum state and legacy boolean for backward compatibility
            settings[SETTINGS_NOTIFICATION_STATE_KEY] = state.toString()
            settings[SETTINGS_NOTIFICATIONS_KEY] = state.isEnabled()
        }
    }

    /**
     * Enable notifications if they have not been explicitly disabled by the user
     * Returns true if notifications were enabled, false otherwise
     */
    suspend fun enableNotificationsIfNotDetermined(): Boolean {
        val currentState = getNotificationStateEnum()

        // Only enable if state is NOT_DETERMINED
        if (currentState.isNotDetermined()) {
            updateNotificationState(NotificationState.ENABLED)
            return true
        }

        return currentState.isEnabled()
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