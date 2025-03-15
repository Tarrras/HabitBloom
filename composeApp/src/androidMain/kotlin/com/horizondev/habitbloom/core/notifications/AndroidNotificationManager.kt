package com.horizondev.habitbloom.core.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }

    override suspend fun requestNotificationPermission(): Boolean = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return@withContext ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
        return@withContext true
    }

    override suspend fun areNotificationsPermitted(): Boolean = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return@withContext ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
        return@withContext true
    }

    override suspend fun scheduleHabitReminder(
        habitId: Long,
        habitName: String,
        description: String,
        time: LocalTime,
        activeDays: List<DayOfWeek>
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Cancel any existing alarms for this habit
            cancelHabitReminder(habitId)

            // Schedule new alarms for each active day
            for (dayOfWeek in activeDays) {
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

                // Set up the alarm time
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, time.hour)
                    set(Calendar.MINUTE, time.minute)
                    set(Calendar.SECOND, 0)

                    // If the time today has already passed, schedule for next occurrence
                    val currentTimeMillis = System.currentTimeMillis()
                    if (timeInMillis <= currentTimeMillis) {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }

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

                    // Set to the next occurrence of this day of week
                    while (get(Calendar.DAY_OF_WEEK) != calendarDayOfWeek) {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                }

                // Schedule a repeating alarm
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            }

            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    override suspend fun cancelHabitReminder(habitId: Long): Unit = withContext(Dispatchers.IO) {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateHabitReminder(
        habitId: Long,
        habitName: String,
        description: String,
        time: LocalTime,
        activeDays: List<DayOfWeek>
    ): Boolean = withContext(Dispatchers.IO) {
        // First cancel any existing reminders
        cancelHabitReminder(habitId)

        // Then schedule new ones
        return@withContext scheduleHabitReminder(
            habitId = habitId,
            habitName = habitName,
            description = description,
            time = time,
            activeDays = activeDays
        )
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
} 