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
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import java.util.Calendar

class AndroidNotificationManager(private val context: Context) :
    com.horizondev.habitbloom.core.notifications.NotificationManager, NotificationScheduler {

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

    override suspend fun scheduleHabitReminder(
        habitId: Long,
        habitName: String,
        description: String,
        time: LocalTime,
        date: LocalDate
    ): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Cancel any existing alarms for this habit
            cancelHabitReminder(habitId)

            val intent = Intent(context, HabitReminderReceiver::class.java).apply {
                putExtra("HABIT_ID", habitId)
                putExtra("HABIT_NAME", habitName)
                putExtra("HABIT_DESCRIPTION", description)
                putExtra("DAY_OF_WEEK", date.dayOfWeek.ordinal)
                putExtra("YEAR", date.year)
                putExtra("MONTH", date.monthNumber)
                putExtra("DAY", date.dayOfMonth)
            }

            val requestCode = generateRequestCodeForHabit(habitId, date.dayOfWeek)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Set up the alarm time
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, date.year)
                set(Calendar.MONTH, date.monthNumber - 1) // Java Calendar months are 0-based
                set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
                set(Calendar.HOUR_OF_DAY, time.hour)
                set(Calendar.MINUTE, time.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // If the time has already passed, do not schedule
            val currentTimeMillis = System.currentTimeMillis()
            if (calendar.timeInMillis <= currentTimeMillis) {
                return@runCatching false
            }

            // Schedule the alarm
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

            Napier.d(
                tag = this.javaClass.canonicalName,
                message = "Notification is set at ${calendar.time}"
            )
            true
        }.getOrElse {
            it.printStackTrace()
            false
        }
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
     * Implementation of NotificationScheduler interface
     * This is used by HabitReminderReceiver to schedule the next notification
     *
     * @param habitId Unique identifier for the habit
     * @param habitName Name of the habit to display in notification
     * @param description Description of the habit to display in notification
     * @param time Time of day to show the notification
     * @param date The specific date to schedule for
     * @return Boolean indicating if scheduling was successful
     */
    override suspend fun scheduleSpecificDayNotification(
        habitId: Long,
        habitName: String,
        description: String,
        time: LocalTime,
        date: LocalDate
    ): Boolean {
        // Reuse the same implementation as scheduleHabitReminder
        return scheduleHabitReminder(habitId, habitName, description, time, date)
    }

    // Generate a unique request code for each habit and day combination
    private fun generateRequestCodeForHabit(habitId: Long, dayOfWeek: DayOfWeek): Int {
        return (habitId * 10 + dayOfWeek.ordinal).toInt()
    }
} 