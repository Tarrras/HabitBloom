package com.horizondev.habitbloom.utils

import androidx.compose.runtime.Composable
import com.horizondev.habitbloom.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.habits.domain.models.UserHabitRecordFullInfo
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources._0_percentage_task_done
import habitbloom.composeapp.generated.resources._100_percentage_task_done
import habitbloom.composeapp.generated.resources._10_percentage_task_done
import habitbloom.composeapp.generated.resources._25_percentage_task_done
import habitbloom.composeapp.generated.resources._50_percentage_task_done
import habitbloom.composeapp.generated.resources._75_percentage_task_done
import habitbloom.composeapp.generated.resources._90_percentage_task_done
import habitbloom.composeapp.generated.resources.afternoon
import habitbloom.composeapp.generated.resources.evening
import habitbloom.composeapp.generated.resources.morning
import habitbloom.composeapp.generated.resources.some_percentage_task_done
import habitbloom.composeapp.generated.resources.some_tasks_completed
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.stringResource

fun getCurrentDate(): LocalDate {
    // Obtain the current system time zone
    val timeZone = TimeZone.currentSystemDefault()

    // Get the current date in the system's default time zone
    return Clock.System.todayIn(timeZone)
}

fun getTimeOfDay(): TimeOfDay {
    val currentMoment = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault()
    val currentDateTime = currentMoment.toLocalDateTime(timeZone)
    val currentHour = currentDateTime.hour

    return when (currentHour) {
        in 5..11 -> TimeOfDay.Morning
        in 12..16 -> TimeOfDay.Afternoon
        in 17..20 -> TimeOfDay.Evening
        else -> TimeOfDay.Evening
    }
}

@Composable
fun habitsCompleteMessage(habitsCount: Int, completedHabits: Int): String {
    val taskCompletionPercentage = taskCompletionPercentage(habitsCount, completedHabits)
    return when {
        completedHabits == habitsCount && completedHabits != 0 -> {
            stringResource(Res.string._100_percentage_task_done)
        }

        completedHabits == 0 -> {
            stringResource(Res.string._0_percentage_task_done)
        }

        taskCompletionPercentage >= 0.9 -> {
            stringResource(Res.string._90_percentage_task_done)
        }

        taskCompletionPercentage >= 0.75 -> {
            stringResource(Res.string._75_percentage_task_done)
        }

        taskCompletionPercentage >= 0.5 -> {
            stringResource(Res.string._50_percentage_task_done)
        }

        taskCompletionPercentage >= 0.25 -> {
            stringResource(Res.string._25_percentage_task_done)
        }

        taskCompletionPercentage >= 0.1 -> {
            stringResource(Res.string._10_percentage_task_done)
        }

        else -> {
            stringResource(Res.string.some_percentage_task_done, completedHabits.toString())
        }
    }
}

fun taskCompletionPercentage(habits: List<UserHabitRecordFullInfo>): Int {
    val completedHabits = habits.filter { it.isCompleted }.size
    return if (completedHabits == 0) {
        0
    } else {
        (completedHabits.toFloat() / habits.size.toFloat() * 100).toInt()
    }
}

fun taskCompletionPercentage(habitsCount: Int, completedHabits: Int): Float {
    return if (completedHabits == 0) {
        0f
    } else {
        (completedHabits.toFloat() / habitsCount.toFloat()).coerceAtMost(1f)
    }
}

@Composable
fun TimeOfDay.getTitle(): String {
    return when(this) {
        TimeOfDay.Morning -> stringResource(Res.string.morning)
        TimeOfDay.Afternoon -> stringResource(Res.string.afternoon)
        TimeOfDay.Evening -> stringResource(Res.string.evening)
    }
}