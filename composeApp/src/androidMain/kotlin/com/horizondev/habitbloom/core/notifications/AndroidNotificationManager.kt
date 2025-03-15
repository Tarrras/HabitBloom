package com.horizondev.habitbloom.core.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.horizondev.habitbloom.HabitReminderReceiver
import com.horizondev.habitbloom.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import java.util.Calendar

class AndroidNotificationManager(private val context: Context) :
    com.horizondev.habitbloom.core.notifications.NotificationManager {

    companion object {
        private const val CHANNEL_ID = "habit_reminders"
        private const val NOTIFICATION_REQUEST_CODE = 100
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "Habit Reminders"
        val descriptionText = "Notifications for habit reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override suspend fun cancelHabitReminder(habitId: Long): Unit = withContext(Dispatchers.IO) {
        runCatching {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Cancel for all possible days
            for (dayOfWeek in DayOfWeek.entries) {
                val intent = Intent(context, HabitReminderReceiver::class.java)
                val requestCode = generateRequestCodeForHabit(habitId, dayOfWeek)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
                )

                pendingIntent?.let {
                    alarmManager.cancel(it)
                    it.cancel()
                }
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    // Generate a unique request code for each habit and day combination
    private fun generateRequestCodeForHabit(habitId: Long, dayOfWeek: DayOfWeek): Int {
        return (habitId * 10 + dayOfWeek.ordinal).toInt()
    }

    // Show a notification immediately (for testing or manual triggers)
    fun showNotification(habitId: Long, title: String, content: String) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(habitId.toInt(), builder.build())
    }

    /**
     * Schedules a reminder for a habit for a specific day
     * This is used for rescheduling notifications after they've been shown
     *
     * @param habitId Unique identifier for the habit
     * @param habitName Name of the habit to display in notification
     * @param description Description of the habit to display in notification
     * @param time Time of day to show the notification
     * @param dayOfWeek The specific day of week to schedule for
     * @return Boolean indicating if scheduling was successful
     */
    override suspend fun scheduleHabitReminder(
        habitId: Long,
        habitName: String,
        description: String,
        time: LocalTime,
        dayOfWeek: DayOfWeek
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext runCatching {
            // First cancel any existing reminders
            cancelHabitReminder(habitId)

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Create intent with the habit information
            val intent = Intent(context, HabitReminderReceiver::class.java).apply {
                putExtra("HABIT_ID", habitId)
                putExtra("HABIT_NAME", habitName)
                putExtra("HABIT_DESCRIPTION", description)
                putExtra("DAY_OF_WEEK", dayOfWeek.ordinal)
            }

            val requestCode = generateRequestCodeForHabit(habitId, dayOfWeek)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Set up the alarm time for next week
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, time.hour)
                set(Calendar.MINUTE, time.minute)
                set(Calendar.SECOND, 0)

                // Adjust to the correct day of week
                val calendarDayOfWeek = when (dayOfWeek) {
                    DayOfWeek.MONDAY -> Calendar.MONDAY
                    DayOfWeek.TUESDAY -> Calendar.TUESDAY
                    DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
                    DayOfWeek.THURSDAY -> Calendar.THURSDAY
                    DayOfWeek.FRIDAY -> Calendar.FRIDAY
                    DayOfWeek.SATURDAY -> Calendar.SATURDAY
                    DayOfWeek.SUNDAY -> Calendar.SUNDAY
                }

                // Add 7 days to schedule for next week (same day)
                // First get to the right day of week
                while (get(Calendar.DAY_OF_WEEK) != calendarDayOfWeek) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
                // Then add 7 days to get to next week
                add(Calendar.DAY_OF_MONTH, 7)
            }

            // Schedule the alarm
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            true
        }.getOrElse {
            it.printStackTrace()
            false
        }
    }
} 