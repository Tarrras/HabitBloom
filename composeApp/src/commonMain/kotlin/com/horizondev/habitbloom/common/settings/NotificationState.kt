package com.horizondev.habitbloom.common.settings

/**
 * Represents the state of notification permissions in the app.
 */
enum class NotificationState {
    /**
     * Initial state - user hasn't made a choice yet.
     */
    NOT_DETERMINED,

    /**
     * Notifications are explicitly enabled by the user.
     */
    ENABLED,

    /**
     * Notifications are explicitly disabled by the user.
     */
    DISABLED;

    /**
     * Returns true if notifications are enabled.
     */
    fun isEnabled(): Boolean = this == ENABLED

    /**
     * Returns true if this is the initial state (no user choice made yet).
     */
    fun isNotDetermined(): Boolean = this == NOT_DETERMINED

    companion object {
        /**
         * Convert a boolean to NotificationState.
         * This is primarily used for legacy boolean conversion.
         */
        fun fromBoolean(enabled: Boolean): NotificationState {
            return if (enabled) ENABLED else DISABLED
        }
    }
} 