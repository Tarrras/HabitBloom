package com.horizondev.habitbloom.core.notifications

import io.github.aakira.napier.Napier
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.koin.core.component.KoinComponent
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound

class IOSNotificationScheduler(
    private val notificationManager: IOSNotificationManager
) : NotificationScheduler, KoinComponent {

    companion object {
        private const val CATEGORY_ID = "habit_reminders"
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

        notificationManager.removeNotificationsById(identifiers)
    }

    override suspend fun cancelHabitReminder(habitId: Long) {
        removeNotificationsForHabit(habitId)
    }


    override suspend fun scheduleHabitReminder(
        habitId: Long,
        habitName: String,
        description: String,
        time: LocalTime,
        date: LocalDate
    ): Boolean = runCatching {
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
                    "dayOfWeek" to date.dayOfWeek.ordinal
                )
            )
        }

        // Create date components for the trigger
        val dateComponents = NSDateComponents().apply {
            setHour(time.hour.toLong())
            setMinute(time.minute.toLong())
            setDay(date.day.toLong())

            /*// Convert Kotlin DayOfWeek to iOS weekday (1 = Sunday, 2 = Monday, etc.)
            val iosWeekday = when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> 2
                DayOfWeek.TUESDAY -> 3
                DayOfWeek.WEDNESDAY -> 4
                DayOfWeek.THURSDAY -> 5
                DayOfWeek.FRIDAY -> 6
                DayOfWeek.SATURDAY -> 7
                DayOfWeek.SUNDAY -> 1
            }
            setWeekday(iosWeekday.toLong())*/
        }

        // Create a calendar trigger
        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents,
            true // repeats weekly
        )

        // Generate a unique identifier for this notification
        val identifier = generateNotificationIdentifier(habitId, date.dayOfWeek)

        // Create notification request
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier,
            content,
            trigger
        )

        notificationManager.makeNotificationRequest(request)
    }.getOrElse { throwable ->
        Napier.e(throwable = throwable, message = "scheduleHabitReminder error")
        false
    }

    // Generate a unique identifier for each notification based on habit ID and day of week
    private fun generateNotificationIdentifier(habitId: Long, dayOfWeek: DayOfWeek): String {
        return "habit_${habitId}_${dayOfWeek.name.lowercase()}"
    }
}