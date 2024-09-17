package com.horizondev.habitbloom.utils

import androidx.compose.runtime.Composable
import com.horizondev.habitbloom.habits.domain.models.UserHabitRecordFullInfo
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources._0_percentage_task_done
import habitbloom.composeapp.generated.resources._100_percentage_task_done
import habitbloom.composeapp.generated.resources._25_percentage_task_done
import habitbloom.composeapp.generated.resources._50_percentage_task_done
import habitbloom.composeapp.generated.resources._75_percentage_task_done
import habitbloom.composeapp.generated.resources._90_percentage_task_done
import habitbloom.composeapp.generated.resources.some_tasks_completed
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.stringResource

fun getCurrentDate(): LocalDate {
    // Obtain the current system time zone
    val timeZone = TimeZone.currentSystemDefault()

    // Get the current date in the system's default time zone
    return Clock.System.todayIn(timeZone)
}

@Composable
fun taskCompleteMessage(tasks: List<UserHabitRecordFullInfo>): String {
    val completedTasks = tasks.filter { it.isCompleted }.size
    return when {
        completedTasks == tasks.size && completedTasks != 0 -> {
            stringResource(Res.string._100_percentage_task_done)
        }

        completedTasks == 0 -> {
            stringResource(Res.string._0_percentage_task_done)
        }

        taskCompletionPercentage(tasks) >= 90 -> {
            stringResource(Res.string._90_percentage_task_done)
        }

        taskCompletionPercentage(tasks) >= 75 -> {
            stringResource(Res.string._75_percentage_task_done)
        }

        taskCompletionPercentage(tasks) >= 50 -> {
            stringResource(Res.string._50_percentage_task_done)
        }

        taskCompletionPercentage(tasks) >= 25 -> {
            stringResource(Res.string._25_percentage_task_done)
        }

        taskCompletionPercentage(tasks) >= 10 -> {
            stringResource(Res.string._90_percentage_task_done)
        }

        else -> {
            stringResource(Res.string.some_tasks_completed, completedTasks.toString())
        }
    }
}

fun taskCompletionPercentage(tasks: List<UserHabitRecordFullInfo>): Int {
    val completedTasks = tasks.filter { it.isCompleted }.size
    return if (completedTasks == 0) {
        0
    } else {
        (completedTasks.toFloat() / tasks.size.toFloat() * 100).toInt()
    }
}