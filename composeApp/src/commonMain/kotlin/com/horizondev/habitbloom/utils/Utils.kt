package com.horizondev.habitbloom.utils

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
import habitbloom.composeapp.generated.resources.daily_progress
import habitbloom.composeapp.generated.resources.daily_progress_alt
import habitbloom.composeapp.generated.resources.due_ago_hours
import habitbloom.composeapp.generated.resources.due_ago_hours_minutes
import habitbloom.composeapp.generated.resources.due_ago_minutes
import habitbloom.composeapp.generated.resources.due_in_hours
import habitbloom.composeapp.generated.resources.due_in_hours_minutes
import habitbloom.composeapp.generated.resources.due_in_minutes
import habitbloom.composeapp.generated.resources.due_now
import habitbloom.composeapp.generated.resources.evening
import habitbloom.composeapp.generated.resources.evening_progress
import habitbloom.composeapp.generated.resources.friday
import habitbloom.composeapp.generated.resources.friday_short
import habitbloom.composeapp.generated.resources.garden_background_evening
import habitbloom.composeapp.generated.resources.garden_background_morning
import habitbloom.composeapp.generated.resources.ic_lucid_moon
import habitbloom.composeapp.generated.resources.ic_lucid_sun
import habitbloom.composeapp.generated.resources.ic_lucid_sunrise
import habitbloom.composeapp.generated.resources.monday
import habitbloom.composeapp.generated.resources.monday_short
import habitbloom.composeapp.generated.resources.morning
import habitbloom.composeapp.generated.resources.morning_progress
import habitbloom.composeapp.generated.resources.no_habits_today
import habitbloom.composeapp.generated.resources.saturday
import habitbloom.composeapp.generated.resources.saturday_short
import habitbloom.composeapp.generated.resources.some_percentage_task_done
import habitbloom.composeapp.generated.resources.sunday
import habitbloom.composeapp.generated.resources.sunday_short
import habitbloom.composeapp.generated.resources.thursday
import habitbloom.composeapp.generated.resources.thursday_short
import habitbloom.composeapp.generated.resources.tuesday
import habitbloom.composeapp.generated.resources.tuesday_short
import habitbloom.composeapp.generated.resources.wednesday
import habitbloom.composeapp.generated.resources.wednesday_short
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
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
fun TimeOfDay?.getProgressTitle(): String {
    return when (this) {
        TimeOfDay.Morning -> stringResource(Res.string.morning_progress)
        TimeOfDay.Afternoon -> stringResource(Res.string.daily_progress)
        TimeOfDay.Evening -> stringResource(Res.string.evening_progress)
        else -> stringResource(Res.string.daily_progress_alt)
    }
}

@Composable
fun TimeOfDay.getIcon(): Painter {
    return when (this) {
        TimeOfDay.Morning -> painterResource(Res.drawable.ic_lucid_sunrise)
        TimeOfDay.Afternoon -> painterResource(Res.drawable.ic_lucid_sun)
        TimeOfDay.Evening -> painterResource(Res.drawable.ic_lucid_moon)
    }
}

@Composable
fun DayOfWeek.getTitle(): String {
    return when (this) {
        DayOfWeek.MONDAY -> stringResource(Res.string.monday)
        DayOfWeek.TUESDAY -> stringResource(Res.string.tuesday)
        DayOfWeek.WEDNESDAY -> stringResource(Res.string.wednesday)
        DayOfWeek.THURSDAY -> stringResource(Res.string.thursday)
        DayOfWeek.FRIDAY -> stringResource(Res.string.friday)
        DayOfWeek.SATURDAY -> stringResource(Res.string.saturday)
        DayOfWeek.SUNDAY -> stringResource(Res.string.sunday)
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

// Removed legacy streak utilities in favor of level/XP model

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

/**
 * Format minutes until due into a localized short string.
 * - <= 0: due now
 * - < 60: in Xm
 * - otherwise: in Hh or in Hh Mm
 */
@Composable
fun formatDueInMinutes(minutes: Int): String {
    return when {
        minutes == 0 -> stringResource(Res.string.due_now)
        minutes > 0 && minutes < 60 -> stringResource(Res.string.due_in_minutes, minutes)
        minutes > 0 -> {
            val h = minutes / 60
            val m = minutes % 60
            if (m == 0) stringResource(Res.string.due_in_hours, h)
            else stringResource(Res.string.due_in_hours_minutes, h, m)
        }
        // minutes < 0 → already due (ago)
        minutes < 0 && minutes > -60 -> stringResource(Res.string.due_ago_minutes, -minutes)
        else -> {
            val total = -minutes
            val h = total / 60
            val m = total % 60
            if (m == 0) stringResource(Res.string.due_ago_hours, h)
            else stringResource(Res.string.due_ago_hours_minutes, h, m)
        }
    }
}
