package com.horizondev.habitbloom.core.notifications

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusEphemeral
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalForeignApi::class)
class IOSNotificationManager : NotificationManager {

    companion object {
        private const val CATEGORY_ID = "habit_reminders"
    }

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun requestNotificationPermission(): Boolean =
        suspendCoroutine { continuation ->
            notificationCenter.requestAuthorizationWithOptions(
                UNAuthorizationOptionAlert or UNAuthorizationOptionBadge or UNAuthorizationOptionSound
            ) { granted, error ->
                if (error != null) {
                    println("Error requesting notification permission: ${error.localizedDescription}")
                    continuation.resume(false)
                } else {
                    continuation.resume(granted)
                }
            }
        }

    override suspend fun areNotificationsPermitted(): Boolean = suspendCoroutine { continuation ->
        notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
            val isAuthorized = when (settings?.authorizationStatus) {
                UNAuthorizationStatusAuthorized,
                UNAuthorizationStatusProvisional,
                UNAuthorizationStatusEphemeral -> true

                UNAuthorizationStatusDenied,
                UNAuthorizationStatusNotDetermined,
                null -> false

                else -> false
            }
            continuation.resume(isAuthorized)
        }
    }

    /**
     * Removes notification identifiers for a habit ID.
     * This is a private helper method that directly performs the operation without suspending.
     */
    private fun removeNotificationsForHabit(habitId: Long) {
        // Generate identifiers for all possible days
        val identifiers = DayOfWeek.entries.map { dayOfWeek ->
            generateNotificationIdentifier(habitId, dayOfWeek)
        }

        // Remove both pending and delivered notifications
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(identifiers)
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(identifiers)
    }

    override suspend fun scheduleHabitReminder(
        habitId: Long,
        habitName: String,
        description: String,
        time: LocalTime,
        activeDays: List<DayOfWeek>
    ): Boolean = suspendCoroutine { continuation ->
        try {
            // First cancel existing notifications (using the non-suspending helper method)
            removeNotificationsForHabit(habitId)

            // If there are no active days, just return success
            if (activeDays.isEmpty()) {
                continuation.resume(true)
                return@suspendCoroutine
            }

            var scheduledCount = 0
            val totalToSchedule = activeDays.size

            // Schedule notifications for each active day
            for (dayOfWeek in activeDays) {
                // Create notification content
                val content = UNMutableNotificationContent().apply {
                    setTitle(habitName)
                    setBody(description)
                    setSound(UNNotificationSound.defaultSound)
                    setCategoryIdentifier(CATEGORY_ID)
                    setUserInfo(
                        mapOf(
                            "habitId" to habitId,
                            "dayOfWeek" to dayOfWeek.ordinal
                        )
                    )
                }

                // Create date components for the trigger
                val dateComponents = NSDateComponents().apply {
                    setHour(time.hour.toLong())
                    setMinute(time.minute.toLong())

                    // Convert Kotlin DayOfWeek to iOS weekday (1 = Sunday, 2 = Monday, etc.)
                    val iosWeekday = when (dayOfWeek) {
                        DayOfWeek.MONDAY -> 2
                        DayOfWeek.TUESDAY -> 3
                        DayOfWeek.WEDNESDAY -> 4
                        DayOfWeek.THURSDAY -> 5
                        DayOfWeek.FRIDAY -> 6
                        DayOfWeek.SATURDAY -> 7
                        DayOfWeek.SUNDAY -> 1
                    }
                    setWeekday(iosWeekday.toLong())
                }

                // Create a calendar trigger
                val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                    dateComponents,
                    true // repeats weekly
                )

                // Generate a unique identifier for this notification
                val identifier = generateNotificationIdentifier(habitId, dayOfWeek)

                // Create notification request
                val request = UNNotificationRequest.requestWithIdentifier(
                    identifier,
                    content,
                    trigger
                )

                // Add notification request to notification center
                notificationCenter.addNotificationRequest(request) { error ->
                    scheduledCount++

                    if (error != null) {
                        println("Error scheduling notification: ${error.localizedDescription}")
                    }

                    // When all notifications have been attempted, resume the coroutine
                    if (scheduledCount >= totalToSchedule) {
                        continuation.resume(error == null)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            continuation.resume(false)
        }
    }

    override suspend fun cancelHabitReminder(habitId: Long) =
        suspendCoroutine<Unit> { continuation ->
            removeNotificationsForHabit(habitId)
            continuation.resume(Unit)
        }

    override suspend fun updateHabitReminder(
        habitId: Long,
        habitName: String,
        description: String,
        time: LocalTime,
        activeDays: List<DayOfWeek>
    ): Boolean = suspendCoroutine { continuation ->
        try {
            // First cancel existing notifications (using the non-suspending helper method)
            removeNotificationsForHabit(habitId)

            // If there are no active days, just return success
            if (activeDays.isEmpty()) {
                continuation.resume(true)
                return@suspendCoroutine
            }

            var scheduledCount = 0
            val totalToSchedule = activeDays.size

            // Schedule notifications for each active day
            for (dayOfWeek in activeDays) {
                // Create notification content
                val content = UNMutableNotificationContent().apply {
                    setTitle(habitName)
                    setBody(description)
                    setSound(UNNotificationSound.defaultSound)
                    setCategoryIdentifier(CATEGORY_ID)
                    setUserInfo(
                        mapOf(
                            "habitId" to habitId,
                            "dayOfWeek" to dayOfWeek.ordinal
                        )
                    )
                }

                // Create date components for the trigger
                val dateComponents = NSDateComponents().apply {
                    setHour(time.hour.toLong())
                    setMinute(time.minute.toLong())

                    // Convert Kotlin DayOfWeek to iOS weekday (1 = Sunday, 2 = Monday, etc.)
                    val iosWeekday = when (dayOfWeek) {
                        DayOfWeek.MONDAY -> 2
                        DayOfWeek.TUESDAY -> 3
                        DayOfWeek.WEDNESDAY -> 4
                        DayOfWeek.THURSDAY -> 5
                        DayOfWeek.FRIDAY -> 6
                        DayOfWeek.SATURDAY -> 7
                        DayOfWeek.SUNDAY -> 1
                    }
                    setWeekday(iosWeekday.toLong())
                }

                // Create a calendar trigger
                val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                    dateComponents,
                    true // repeats weekly
                )

                // Generate a unique identifier for this notification
                val identifier = generateNotificationIdentifier(habitId, dayOfWeek)

                // Create notification request
                val request = UNNotificationRequest.requestWithIdentifier(
                    identifier,
                    content,
                    trigger
                )

                // Add notification request to notification center
                notificationCenter.addNotificationRequest(request) { error ->
                    scheduledCount++

                    if (error != null) {
                        println("Error scheduling notification: ${error.localizedDescription}")
                    }

                    // When all notifications have been attempted, resume the coroutine
                    if (scheduledCount >= totalToSchedule) {
                        continuation.resume(error == null)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            continuation.resume(false)
        }
    }

    // Generate a unique identifier for each notification based on habit ID and day of week
    private fun generateNotificationIdentifier(habitId: Long, dayOfWeek: DayOfWeek): String {
        return "habit_${habitId}_${dayOfWeek.name.lowercase()}"
    }
}