package com.horizondev.habitbloom.core.notifications

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

/**
 * Interface for platform-specific notification management
 * This interface provides methods for scheduling and managing habit reminders
 * across different platforms (Android, iOS).
 */
interface NotificationManager {
    /**
     * Request permission to show notifications
     * @return Boolean indicating if permission was granted
     */
    suspend fun requestNotificationPermission(): Boolean

    /**
     * Check if notifications are permitted
     * @return Boolean indicating if notifications are permitted
     */
    suspend fun areNotificationsPermitted(): Boolean

    /**
     * Schedule a reminder for a habit
     * @param habitId Unique identifier for the habit
     * @param habitName Name of the habit to display in notification
     * @param description Description of the habit to display in notification
     * @param time Time of day to show the notification
     * @param activeDays List of days on which to show the notification
     * @return Boolean indicating if scheduling was successful
     */
    suspend fun scheduleHabitReminder(
        habitId: Long,
        habitName: String,
        description: String,
        time: LocalTime,
        activeDays: List<DayOfWeek>
    ): Boolean

    /**
     * Cancel all reminders for a specific habit
     * @param habitId Unique identifier for the habit
     */
    suspend fun cancelHabitReminder(habitId: Long)

    /**
     * Update existing reminders for a habit
     * @param habitId Unique identifier for the habit
     * @param habitName Updated name of the habit
     * @param description Updated description of the habit
     * @param time Updated time to show the notification
     * @param activeDays Updated list of days on which to show the notification
     * @return Boolean indicating if update was successful
     */
    suspend fun updateHabitReminder(
        habitId: Long,
        habitName: String,
        description: String,
        time: LocalTime,
        activeDays: List<DayOfWeek>
    ): Boolean
} 