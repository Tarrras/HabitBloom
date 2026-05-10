package com.horizondev.habitbloom.core.notifications

import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.plusDays
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import platform.UserNotifications.UNNotification
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.dispatch_after
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time
import kotlin.time.Duration.Companion.seconds

class IOSNotificationEventHandler(
    private val notificationScheduler: NotificationScheduler,
    private val habitsRepository: HabitsRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    fun handleNotificationDelivery(notification: UNNotification) {
        val userInfo = notification.request.content.userInfo
        val habitId = userInfo["habitId"] as? Long ?: return

        dispatch_after(
            dispatch_time(DISPATCH_TIME_NOW, 5.seconds.inWholeNanoseconds),
            dispatch_get_main_queue()
        ) {
            coroutineScope.launch {
                runCatching {
                    rescheduleNextReminder(habitId)
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }

    private suspend fun rescheduleNextReminder(habitId: Long) {
        val habitDetails = habitsRepository.getUserHabitDetails(habitId) ?: return
        val reminderTime = habitDetails.reminderTime ?: return

        if (!habitDetails.reminderEnabled) {
            return
        }

        val nextDateOfHabit = habitsRepository.getFutureDaysForHabit(
            habitId,
            fromDate = getCurrentDate().plusDays(1)
        ).minOfOrNull { it } ?: return

        notificationScheduler.scheduleHabitReminder(
            habitId = habitId,
            habitName = habitDetails.name,
            description = habitDetails.description,
            time = reminderTime,
            date = nextDateOfHabit
        )
    }
}
