package com.horizondev.habitbloom.core.notifications

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IOSNotificationManager : NotificationManager {

    companion object {
        private const val CATEGORY_ID = "habit_reminders"
    }

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

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

    override suspend fun cancelHabitReminder(habitId: Long) =
        suspendCoroutine<Unit> { continuation ->
            removeNotificationsForHabit(habitId)
            continuation.resume(Unit)
        }

    override suspend fun scheduleHabitReminder(
        habitId: Long,
        habitName: String,
        description: String,
        time: LocalTime,
        dayOfWeek: DayOfWeek
    ): Boolean = suspendCoroutine { continuation ->
        runCatching {
            // First cancel existing notifications (using the non-suspending helper method)
            removeNotificationsForHabit(habitId)

            // Schedule notifications
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
                if (error != null) {
                    println("Error scheduling notification: ${error.localizedDescription}")
                }

                continuation.resume(error == null)
            }

        }.onFailure {
            it.printStackTrace()
            continuation.resume(false)
        }
    }

    // Generate a unique identifier for each notification based on habit ID and day of week
    private fun generateNotificationIdentifier(habitId: Long, dayOfWeek: DayOfWeek): String {
        return "habit_${habitId}_${dayOfWeek.name.lowercase()}"
    }
}