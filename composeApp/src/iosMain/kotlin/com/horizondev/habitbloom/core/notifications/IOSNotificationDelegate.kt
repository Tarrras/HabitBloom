package com.horizondev.habitbloom.core.notifications

import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptionAlert
import platform.UserNotifications.UNNotificationPresentationOptionBadge
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.NSObject
import platform.darwin.dispatch_after
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

class IOSNotificationDelegate(
    private val notificationScheduler: NotificationScheduler,
    private val habitsRepository: HabitsRepository
) : NSObject(), UNUserNotificationCenterDelegateProtocol {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // Called when a notification is delivered to a foreground app
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit
    ) {
        // Show the notification with sound, alert, and badge
        withCompletionHandler(
            UNNotificationPresentationOptionAlert or
                    UNNotificationPresentationOptionBadge or
                    UNNotificationPresentationOptionSound
        )

        // Schedule the next notification
        handleNotificationDelivery(willPresentNotification)
    }

    // Called to handle the user's response to a delivered notification
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        didReceiveNotificationResponse: UNNotificationResponse,
        withCompletionHandler: () -> Unit
    ) {
        // Complete the handler
        withCompletionHandler()

        // Schedule the next notification
        handleNotificationDelivery(didReceiveNotificationResponse.notification)
    }

    private fun handleNotificationDelivery(notification: UNNotification) {
        val userInfo = notification.request.content.userInfo

        // Extract the habit ID and date information
        val habitId = userInfo["habitId"] as? Long ?: return
        val dayOfWeekOrdinal = userInfo["dayOfWeek"] as? Int ?: return
        val dayOfWeek = DayOfWeek.entries.getOrNull(dayOfWeekOrdinal) ?: return

        // Extract date components if available (for newer notifications)
        val year = userInfo["year"] as? Int
        val month = userInfo["month"] as? Int
        val day = userInfo["day"] as? Int

        // Delay a bit to avoid potential conflicts
        dispatch_after(
            dispatch_time(DISPATCH_TIME_NOW, 5.seconds.inWholeNanoseconds),
            dispatch_get_main_queue()
        ) {
            // Schedule the next notification in a coroutine
            coroutineScope.launch {
                try {
                    // Get the habit details from repository
                    habitsRepository.getUserHabitDetails(habitId)?.let { habitDetails ->
                        // Only reschedule if reminders are still enabled for this habit
                        if (habitDetails.reminderEnabled && habitDetails.reminderTime != null) {
                            val activeDays = habitDetails.activeDays
                            if (activeDays.isEmpty()) return@let

                            // Calculate the next date for the notification
                            // Start from today or the notified date
                            val today = Clock.System.now()
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date

                            // If we have specific date information, use it to calculate the next date
                            val baseDate = if (year != null && month != null && day != null) {
                                try {
                                    LocalDate(year, month, day)
                                } catch (e: Exception) {
                                    today
                                }
                            } else {
                                today
                            }

                            // Find the next occurrence of an active day after the base date
                            val sortedActiveDays = activeDays.sortedBy { it.ordinal }
                            val nextDayOfWeek =
                                sortedActiveDays.find { it.ordinal > dayOfWeek.ordinal }
                                    ?: sortedActiveDays.first()

                            // Calculate days to add to get to the next occurrence
                            var daysToAdd = (nextDayOfWeek.ordinal - baseDate.dayOfWeek.ordinal)
                            if (daysToAdd <= 0) daysToAdd += 7 // If it's today or earlier in the week, add 7 days

                            // Calculate the next notification date
                            val nextDate = baseDate.plus(daysToAdd.days)

                            withContext(Dispatchers.Main) {
                                notificationScheduler.scheduleSpecificDayNotification(
                                    habitId = habitId,
                                    habitName = habitDetails.name,
                                    description = habitDetails.description,
                                    time = habitDetails.reminderTime,
                                    date = nextDate
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
} 