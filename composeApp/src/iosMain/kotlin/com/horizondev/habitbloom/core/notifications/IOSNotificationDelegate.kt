package com.horizondev.habitbloom.core.notifications

import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.plusDays
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
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

        // Delay a bit to avoid potential conflicts
        dispatch_after(
            dispatch_time(DISPATCH_TIME_NOW, 5.seconds.inWholeNanoseconds),
            dispatch_get_main_queue()
        ) {
            // Schedule the next notification in a coroutine
            coroutineScope.launch {
                runCatching {
                    // Get the habit details from repository to verify active days and time
                    habitsRepository.getUserHabitDetails(habitId)?.let { habitDetails ->
                        // Only reschedule if reminders are still enabled for this habit
                        if (habitDetails.reminderEnabled && habitDetails.reminderTime != null) {

                            habitsRepository.getFutureDaysForHabit(
                                habitId,
                                fromDate = getCurrentDate().plusDays(1)
                            ).minOfOrNull { it }?.let { nextDateOfHabit ->
                                // Schedule the next notification for this specific date
                                notificationScheduler.scheduleHabitReminder(
                                    habitId = habitId,
                                    habitName = habitDetails.name,
                                    description = habitDetails.description,
                                    time = habitDetails.reminderTime,
                                    date = nextDateOfHabit
                                )
                            }

                        }
                    }
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }
} 