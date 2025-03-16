package com.horizondev.habitbloom.core.notifications

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Interface for scheduling specific day notifications
 * Used to break circular dependency between manager and delegate
 */
interface NotificationScheduler {
    /**
     * Schedules a notification for a specific date
     *
     * @param habitId Unique identifier for the habit
     * @param habitName Name of the habit to display in notification
     * @param description Description of the habit to display in notification
     * @param time Time of day to show the notification
     * @param date The specific date to schedule for
     * @return Boolean indicating if scheduling was successful
     */
    suspend fun scheduleSpecificDayNotification(
        habitId: Long,
        habitName: String,
        description: String,
        time: LocalTime,
        date: LocalDate
    ): Boolean
} 