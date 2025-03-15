package com.horizondev.habitbloom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.horizondev.habitbloom.core.notifications.AndroidNotificationManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HabitReminderReceiver : BroadcastReceiver(), KoinComponent {
    companion object {
        private const val CHANNEL_ID = "habit_reminders"
    }

    private val notificationManager: AndroidNotificationManager by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra("HABIT_ID", -1)
        if (habitId == -1L) return

        val habitName = intent.getStringExtra("HABIT_NAME") ?: "Time for your habit!"
        val habitDescription =
            intent.getStringExtra("HABIT_DESCRIPTION") ?: "Don't forget to complete your habit"

        // Use the notification manager to show the notification
        notificationManager.showNotification(habitId, habitName, habitDescription)

        // Reschedule for next week if needed
        // This is already handled by the weekly recurring alarms
    }
} 