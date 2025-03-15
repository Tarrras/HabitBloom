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
     * Schedule a reminder for a habit for a specific date
     * @param habitId Unique identifier for the habit
     * @param habitName Name of the habit to display in notification
     * @param description Description of the habit to display in notification
     * @param time Time of day to show the notification
     * @param dayOfWeek Day on which to show the notification
     * @return Boolean indicating if scheduling was successful
     */
    suspend fun scheduleHabitReminder(
        habitId: Long,
        habitName: String,
        description: String,
        time: LocalTime,
        dayOfWeek: DayOfWeek
    ): Boolean

    /**
     * Cancel all reminders for a specific habit
     * @param habitId Unique identifier for the habit
     */
    suspend fun cancelHabitReminder(habitId: Long)
} 