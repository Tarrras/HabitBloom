package com.horizondev.habitbloom.core.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.horizondev.habitbloom.HabitReminderReceiver
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import java.util.Calendar

class AndroidNotificationScheduler(
    private val context: Context
) : NotificationScheduler {

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
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

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

    // Generate a unique request code for each habit and day combination
    private fun generateRequestCodeForHabit(habitId: Long, dayOfWeek: DayOfWeek): Int {
        return (habitId * 10 + dayOfWeek.ordinal).toInt()
    }
} 