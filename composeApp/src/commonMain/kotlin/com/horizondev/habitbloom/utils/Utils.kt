package com.horizondev.habitbloom.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import cafe.adriel.voyager.navigator.Navigator
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecordFullInfo
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
import habitbloom.composeapp.generated.resources.friday_short
import habitbloom.composeapp.generated.resources.monday_short
import habitbloom.composeapp.generated.resources.morning
import habitbloom.composeapp.generated.resources.saturday_short
import habitbloom.composeapp.generated.resources.some_percentage_task_done
import habitbloom.composeapp.generated.resources.sunday_short
import habitbloom.composeapp.generated.resources.thursday_short
import habitbloom.composeapp.generated.resources.tuesday_short
import habitbloom.composeapp.generated.resources.wednesday_short
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T> Flow<T>.collectAsEffect(
    context: CoroutineContext = EmptyCoroutineContext,
    block: (T) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        onEach(block).flowOn(context).launchIn(this)
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

fun calculateCompletedRepeats(
    dayOfCreation: LocalDate,
    basicRepeats: Int,
    habitDays: List<DayOfWeek>
): Int {
    val today = getCurrentDate()
    val dayOfCreationInWeek = dayOfCreation.dayOfWeek
    val startOfWeekOfCreationDate = dayOfCreation.minusDays(dayOfCreationInWeek.ordinal.toLong())
    val firstDatesOfHabit = habitDays.sortedBy {
        it.ordinal
    }.map { startOfWeekOfCreationDate.plusDays(it.ordinal.toLong()) }

    var completedRepeats = 0
    var repeatDates = firstDatesOfHabit

    while (true) {
        val startDateOfRepeat = repeatDates.first()
        val endDateOfRepeat = repeatDates.last()

        when {
            completedRepeats >= basicRepeats -> break
            today in startDateOfRepeat..endDateOfRepeat -> {
                break
            }

            else -> {
                completedRepeats += 1
                repeatDates = repeatDates.map { it.plusDays(7) } //move to the next week
            }
        }
    }

    return completedRepeats
}

@Composable
fun TimeOfDay.getTitle(): String {
    return when (this) {
        TimeOfDay.Morning -> stringResource(Res.string.morning)
        TimeOfDay.Afternoon -> stringResource(Res.string.afternoon)
        TimeOfDay.Evening -> stringResource(Res.string.evening)
    }
}

@Composable
fun DayOfWeek.getShortTitle(): String {
    return when (this) {
        DayOfWeek.MONDAY -> stringResource(Res.string.monday_short)
        DayOfWeek.TUESDAY -> stringResource(Res.string.tuesday_short)
        DayOfWeek.WEDNESDAY -> stringResource(Res.string.wednesday_short)
        DayOfWeek.THURSDAY -> stringResource(Res.string.thursday_short)
        DayOfWeek.FRIDAY -> stringResource(Res.string.friday_short)
        DayOfWeek.SATURDAY -> stringResource(Res.string.saturday_short)
        DayOfWeek.SUNDAY -> stringResource(Res.string.sunday_short)
        else -> stringResource(Res.string.sunday_short)
    }
}

fun List<DayOfWeek>.mapToString() = this.toSet()
    .sortedBy { it.ordinal }
    .joinToString(",") { it.name }

fun List<UserHabitRecord>.getLongestCompletionStreak(): Int {
    // Step 1: Sort the records by date
    val sortedRecords = this.sortedBy { it.date }

    // Step 2: Initialize variables
    var currentStreak = 0
    var maxStreak = 0

    // Step 3: Iterate through the records
    for (record in sortedRecords) {
        if (record.isCompleted) {
            currentStreak += 1
            if (currentStreak > maxStreak) {
                maxStreak = currentStreak
            }
        } else {
            currentStreak = 0
        }
    }

    return maxStreak
}

val ProvidableCompositionLocal<Navigator?>.parentOrThrow: Navigator
    @Composable
    get() = current?.parent ?: error("CompositionLocal is null")