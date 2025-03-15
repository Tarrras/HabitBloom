package com.horizondev.habitbloom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.horizondev.habitbloom.core.notifications.AndroidNotificationManager
import com.horizondev.habitbloom.screens.habits.domain.HabitsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HabitReminderReceiver : BroadcastReceiver(), KoinComponent {
    companion object {
        private const val CHANNEL_ID = "habit_reminders"
    }

    private val notificationManager: AndroidNotificationManager by inject()
    private val habitsRepository: HabitsRepository by inject()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra("HABIT_ID", -1)
        if (habitId == -1L) return

        val habitName = intent.getStringExtra("HABIT_NAME") ?: "Time for your habit!"
        val habitDescription =
            intent.getStringExtra("HABIT_DESCRIPTION") ?: "Don't forget to complete your habit"
        val dayOfWeekOrdinal = intent.getIntExtra("DAY_OF_WEEK", -1)

        // Show the current notification
        notificationManager.showNotification(habitId, habitName, habitDescription)

        // Schedule the next occurrence if we have enough information
        if (dayOfWeekOrdinal >= 0) {
            // Convert the ordinal back to DayOfWeek
            val dayOfWeek = DayOfWeek.entries.getOrNull(dayOfWeekOrdinal)

            // Schedule the next notification in a coroutine
            coroutineScope.launch {
                runCatching {
                    // Get the habit details from repository to verify active days and time
                    habitsRepository.getUserHabitDetails(habitId)?.let { habitDetails ->
                        val activeDays = habitDetails.activeDays.sortedBy { it.value }
                        val currentDateIndex = activeDays.indexOf(dayOfWeek)
                        val nextDate = activeDays.getOrNull(currentDateIndex + 1)
                            ?: activeDays.first()

                        // Only reschedule if reminders are still enabled for this habit
                        if (habitDetails.reminderEnabled && habitDetails.reminderTime != null) {
                            // Schedule the next notification for this specific day
                            notificationManager.scheduleHabitReminder(
                                habitId = habitId,
                                habitName = habitName,
                                description = habitDescription,
                                time = habitDetails.reminderTime,
                                dayOfWeek = nextDate
                            )
                        }
                    }
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }
} 