package com.horizondev.habitbloom.utils

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.core.designSystem.BloomTheme
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
import habitbloom.composeapp.generated.resources.garden_background_evening
import habitbloom.composeapp.generated.resources.garden_background_morning
import habitbloom.composeapp.generated.resources.monday_short
import habitbloom.composeapp.generated.resources.morning
import habitbloom.composeapp.generated.resources.no_habits_today
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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.getString
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
        habitsCount == 0 -> {
            stringResource(Res.string.no_habits_today)
        }

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
    records: List<UserHabitRecord>,
): Int {
    if (records.isNotEmpty()) {
        val today = getCurrentDate()
        val creationWeekStartDate = dayOfCreation.calculateStartOfWeek()
        val startOfCurrentWeek = today.calculateStartOfWeek()

        val isRepeatThisWeekCompleted = records.filter { record ->
            record.date.calculateStartOfWeek() == startOfCurrentWeek
        }.maxBy { it.date }.isCompleted

        val pastWeeklyRecords = records.filter { record ->
            if (isRepeatThisWeekCompleted) {
                record.date.calculateStartOfWeek() <= startOfCurrentWeek
            } else record.date.calculateStartOfWeek() < startOfCurrentWeek
        }

        val weeklyRecords =
            pastWeeklyRecords.groupBy { record ->
                val recordDate = record.date
                val weekStartDate = recordDate.calculateStartOfWeek()

                val weeksBetween =
                    (weekStartDate.toEpochDays() - creationWeekStartDate.toEpochDays()) / 7
                weeksBetween
            }

        val completedWeeks = weeklyRecords.size
        return completedWeeks

    } else return 0
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

suspend fun DayOfWeek.getShortTitleSuspend(): String {
    return when (this) {
        DayOfWeek.MONDAY -> getString(Res.string.monday_short)
        DayOfWeek.TUESDAY -> getString(Res.string.tuesday_short)
        DayOfWeek.WEDNESDAY -> getString(Res.string.wednesday_short)
        DayOfWeek.THURSDAY -> getString(Res.string.thursday_short)
        DayOfWeek.FRIDAY -> getString(Res.string.friday_short)
        DayOfWeek.SATURDAY -> getString(Res.string.saturday_short)
        DayOfWeek.SUNDAY -> getString(Res.string.sunday_short)
        else -> getString(Res.string.sunday_short)
    }
}

fun List<DayOfWeek>.mapToString() = this.toSet()
    .sortedBy { it.ordinal }
    .joinToString(",") { it.name }

fun List<UserHabitRecord>.getLongestCompletionStreak(): Int {
    if (isEmpty()) return 0

    // Sort records by date to ensure chronological order
    val sortedRecords = this.sortedBy { it.date }

    var maxStreak = 0
    var currentStreak = 0
    var lastDate: LocalDate? = null
    
    for (record in sortedRecords) {
        if (record.isCompleted) {
            if (lastDate == null) {
                // Starting a new streak
                currentStreak = 1
            } else if (record.date == lastDate.plus(1, DateTimeUnit.DAY)) {
                // Continuing streak
                currentStreak++
            } else {
                // Gap in dates, start a new streak
                currentStreak = 1
            }

            maxStreak = maxOf(maxStreak, currentStreak)
            lastDate = record.date
        } else {
            // Break in streak
            currentStreak = 0
            lastDate = null
        }
    }

    return maxStreak
}

fun List<UserHabitRecordFullInfo>.getLongestCompletionStreakFromFullRecords(): Int {
    if (isEmpty()) return 0

    // Sort records by date to ensure chronological order
    val sortedRecords = this.sortedBy { it.date }

    var maxStreak = 0
    var currentStreak = 0
    var lastDate: LocalDate? = null

    for (record in sortedRecords) {
        if (record.isCompleted) {
            if (lastDate == null) {
                // Starting a new streak
                currentStreak = 1
            } else if (record.date == lastDate.plus(1, DateTimeUnit.DAY)) {
                // Continuing streak
                currentStreak++
            } else {
                // Gap in dates, start a new streak
                currentStreak = 1
            }

            maxStreak = maxOf(maxStreak, currentStreak)
            lastDate = record.date
        } else {
            // Break in streak
            currentStreak = 0
            lastDate = null
        }
    }

    return maxStreak
}

@Composable
fun TimeOfDay.getChartBorder(): Color {
    return when (this) {
        TimeOfDay.Morning -> BloomTheme.colors.timeOfDay.morning.chartBorder
        TimeOfDay.Afternoon -> BloomTheme.colors.timeOfDay.afternoon.chartBorder
        TimeOfDay.Evening -> BloomTheme.colors.timeOfDay.evening.chartBorder
    }
}

@Composable
fun TimeOfDay.getChartColor(): Color {
    return when (this) {
        TimeOfDay.Morning -> BloomTheme.colors.timeOfDay.morning.chartBackground
        TimeOfDay.Afternoon -> BloomTheme.colors.timeOfDay.afternoon.chartBackground
        TimeOfDay.Evening -> BloomTheme.colors.timeOfDay.evening.chartBackground
    }
}

@Composable
fun TimeOfDay.getBackgroundGradientColors(): List<Color> = when (this) {
    TimeOfDay.Morning -> listOf(
        BloomTheme.colors.timeOfDay.morning.gradientStart,
        BloomTheme.colors.timeOfDay.morning.gradientEnd
    )

    TimeOfDay.Afternoon -> listOf(
        BloomTheme.colors.timeOfDay.afternoon.gradientStart,
        BloomTheme.colors.timeOfDay.afternoon.gradientEnd
    )

    TimeOfDay.Evening -> listOf(
        BloomTheme.colors.timeOfDay.evening.gradientStart,
        BloomTheme.colors.timeOfDay.evening.gradientEnd
    )
}

@Composable
fun Modifier.bloomGradientBackground(
    timeOfDay: TimeOfDay
): Modifier = Modifier.background(
    brush = Brush.verticalGradient(colors = timeOfDay.getBackgroundGradientColors())
)

fun ThemeOption.getGardenBackgroundRes(
    isSystemInDarkTheme: Boolean
): DrawableResource {
    return when (this) {
        ThemeOption.Device -> {
            if (isSystemInDarkTheme) Res.drawable.garden_background_evening
            else Res.drawable.garden_background_morning
        }

        ThemeOption.Dark -> Res.drawable.garden_background_evening
        ThemeOption.Light -> Res.drawable.garden_background_morning
    }
}
