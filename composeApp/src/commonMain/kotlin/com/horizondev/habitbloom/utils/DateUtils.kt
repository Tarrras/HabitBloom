package com.horizondev.habitbloom.utils

import androidx.compose.runtime.Composable
import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.month_april
import habitbloom.composeapp.generated.resources.month_august
import habitbloom.composeapp.generated.resources.month_december
import habitbloom.composeapp.generated.resources.month_february
import habitbloom.composeapp.generated.resources.month_january
import habitbloom.composeapp.generated.resources.month_july
import habitbloom.composeapp.generated.resources.month_june
import habitbloom.composeapp.generated.resources.month_march
import habitbloom.composeapp.generated.resources.month_may
import habitbloom.composeapp.generated.resources.month_november
import habitbloom.composeapp.generated.resources.month_october
import habitbloom.composeapp.generated.resources.month_september
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.stringResource

private val customFormat = LocalDate.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED); char(' '); dayOfMonth(); chars(", "); year()
}


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

fun LocalDate.calculateStartOfWeek(): LocalDate {
    val daysSinceMonday = this.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal
    return this.minusDays(daysSinceMonday.toLong())
}

fun getFirstDateFromDaysList(daysList: List<DayOfWeek>): LocalDate? {
    if (daysList.isEmpty()) return null

    // Get today's date and day of the week
    val today = getCurrentDate()
    val daysSinceMonday = today.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal
    val startOfWeek = today.minusDays(daysSinceMonday.toLong())

    // Map each DayOfWeek to its LocalDate in the current week
    val datesInCurrentWeek = daysList.map { dayOfWeek ->
        val daysDifference = dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal
        startOfWeek.plusDays(daysDifference.toLong())
    }

    // Return the earliest date from the list
    return datesInCurrentWeek.minOrNull()
}

fun getFirstDateAfterStartDateOrNextWeek(
    daysList: List<DayOfWeek>,
    startOption: HabitWeekStartOption = HabitWeekStartOption.THIS_WEEK,
    startDate: LocalDate = getCurrentDate()
): LocalDate? {
    if (daysList.isEmpty()) return null
    val sortedDays = daysList.sortedBy { it.ordinal }

    // Get today's date and day of the week
    val daysSinceMonday = startDate.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal
    val startOfWeek = startDate.minusDays(daysSinceMonday.toLong())

    // Map each DayOfWeek to its LocalDate in the current week
    val datesInCurrentWeek = sortedDays.map { dayOfWeek ->
        val daysDifference = dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal
        startOfWeek.plusDays(daysDifference.toLong())
    }

    val firstDayFromToday = datesInCurrentWeek.firstOrNull { it >= startDate }
    return when (startOption) {
        HabitWeekStartOption.THIS_WEEK -> firstDayFromToday
        HabitWeekStartOption.NEXT_WEEK -> {
            val minDay = datesInCurrentWeek.minOrNull()
            minDay?.plusDays(7)
        }
    }
}

fun LocalDate.formatToMmDdYy(): String {
    return this.format(customFormat)
}

fun String.mmDdYyToDate(): LocalDate {
    return LocalDate.parse(this, customFormat)
}

fun LocalDate.minusDays(days: Long): LocalDate {
    return this.minus(days, DateTimeUnit.DAY)
}

fun LocalDate.plusDays(days: Long): LocalDate {
    return this.plus(days, DateTimeUnit.DAY)
}

@Composable
fun Month.getTitle() = when (this) {
    Month.JANUARY -> stringResource(Res.string.month_january)
    Month.FEBRUARY -> stringResource(Res.string.month_february)
    Month.MARCH -> stringResource(Res.string.month_march)
    Month.APRIL -> stringResource(Res.string.month_april)
    Month.MAY -> stringResource(Res.string.month_may)
    Month.JUNE -> stringResource(Res.string.month_june)
    Month.JULY -> stringResource(Res.string.month_july)
    Month.AUGUST -> stringResource(Res.string.month_august)
    Month.SEPTEMBER -> stringResource(Res.string.month_september)
    Month.OCTOBER -> stringResource(Res.string.month_october)
    Month.NOVEMBER -> stringResource(Res.string.month_november)
    Month.DECEMBER -> stringResource(Res.string.month_december)
    else -> stringResource(Res.string.month_january)
}

fun String.toCommonDate() = LocalDate.parse(this)

fun LocalDate.isOnTheSameWeekWithAnotherDay(anotherDay: LocalDate): Boolean {
    return this.calculateStartOfWeek() == anotherDay.calculateStartOfWeek()
}