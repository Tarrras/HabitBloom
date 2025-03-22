package com.horizondev.habitbloom.utils

import androidx.compose.runtime.Composable
import com.horizondev.habitbloom.core.designComponents.pickers.HabitWeekStartOption
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.month_april
import habitbloom.composeapp.generated.resources.month_april_short
import habitbloom.composeapp.generated.resources.month_august
import habitbloom.composeapp.generated.resources.month_august_short
import habitbloom.composeapp.generated.resources.month_december
import habitbloom.composeapp.generated.resources.month_december_short
import habitbloom.composeapp.generated.resources.month_february
import habitbloom.composeapp.generated.resources.month_february_short
import habitbloom.composeapp.generated.resources.month_january
import habitbloom.composeapp.generated.resources.month_january_short
import habitbloom.composeapp.generated.resources.month_july
import habitbloom.composeapp.generated.resources.month_july_short
import habitbloom.composeapp.generated.resources.month_june
import habitbloom.composeapp.generated.resources.month_june_short
import habitbloom.composeapp.generated.resources.month_march
import habitbloom.composeapp.generated.resources.month_march_short
import habitbloom.composeapp.generated.resources.month_may
import habitbloom.composeapp.generated.resources.month_may_short
import habitbloom.composeapp.generated.resources.month_november
import habitbloom.composeapp.generated.resources.month_november_short
import habitbloom.composeapp.generated.resources.month_october
import habitbloom.composeapp.generated.resources.month_october_short
import habitbloom.composeapp.generated.resources.month_september
import habitbloom.composeapp.generated.resources.month_september_short
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

private fun customFormat(
    monthNames: MonthNames = MonthNames.ENGLISH_ABBREVIATED
) = LocalDate.Format {
    monthName(monthNames); char(' '); dayOfMonth(); chars(", "); year()
}


fun getCurrentDate(): LocalDate {
    // Obtain the current system time zone
    val timeZone = TimeZone.currentSystemDefault()

    // Get the current date in the system's default time zone
    return Clock.System.todayIn(timeZone)
}

fun getCurrentDateTime(): LocalDateTime {
    val currentMoment = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault()
    return currentMoment.toLocalDateTime(timeZone)
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

suspend fun LocalDate.formatToMmDdYyWithLocale(): String {
    val names = MonthNames(Month.entries.map { it.getShortTitleSuspend() })
    return this.format(customFormat(monthNames = names))
}

fun LocalDate.formatToMmDdYy(): String {
    return this.format(customFormat())
}

fun String.mmDdYyToDate(): LocalDate {
    return LocalDate.parse(this, customFormat())
}

fun LocalDate.minusDays(days: Long): LocalDate {
    return this.minus(days, DateTimeUnit.DAY)
}

fun LocalDate.plusDays(days: Long): LocalDate {
    return this.plus(days, DateTimeUnit.DAY)
}

/**
 * Subtracts the specified number of months from this date.
 */
fun LocalDate.minusMonths(months: Long): LocalDate {
    if (months == 0L) return this

    // Calculate new year and month values
    var newYear = year
    var newMonth = month.ordinal + 1 - months.toInt()

    // Adjust year if needed
    while (newMonth <= 0) {
        newYear--
        newMonth += 12
    }
    while (newMonth > 12) {
        newYear++
        newMonth -= 12
    }

    // Get the right month enum
    val newMonthEnum = Month(newMonth)

    // Calculate the correct day value, accounting for shorter months
    var newDay = dayOfMonth
    val maxDaysInNewMonth = when (newMonthEnum) {
        Month.FEBRUARY -> if (isLeapYear(newYear)) 29 else 28
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        else -> 31
    }

    if (newDay > maxDaysInNewMonth) {
        newDay = maxDaysInNewMonth
    }

    return LocalDate(newYear, newMonthEnum, newDay)
}

/**
 * Adds the specified number of months to this date.
 */
fun LocalDate.plusMonths(months: Long): LocalDate {
    return minusMonths(-months)
}

/**
 * Checks if the given year is a leap year.
 */
private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

/**
 * Subtracts the specified number of years from this date.
 */
fun LocalDate.minusYears(years: Long): LocalDate {
    if (years == 0L) return this

    val newYear = year - years.toInt()

    // Handle Feb 29 in leap years
    if (month == Month.FEBRUARY && dayOfMonth == 29 && !isLeapYear(newYear)) {
        return LocalDate(newYear, Month.FEBRUARY, 28)
    }

    return LocalDate(newYear, month, dayOfMonth)
}

/**
 * Adds the specified number of years to this date.
 */
fun LocalDate.plusYears(years: Long): LocalDate {
    return minusYears(-years)
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

@Composable
fun Month.getShortTitle() = when (this) {
    Month.JANUARY -> stringResource(Res.string.month_january_short)
    Month.FEBRUARY -> stringResource(Res.string.month_february_short)
    Month.MARCH -> stringResource(Res.string.month_march_short)
    Month.APRIL -> stringResource(Res.string.month_april_short)
    Month.MAY -> stringResource(Res.string.month_may_short)
    Month.JUNE -> stringResource(Res.string.month_june_short)
    Month.JULY -> stringResource(Res.string.month_july_short)
    Month.AUGUST -> stringResource(Res.string.month_august_short)
    Month.SEPTEMBER -> stringResource(Res.string.month_september_short)
    Month.OCTOBER -> stringResource(Res.string.month_october_short)
    Month.NOVEMBER -> stringResource(Res.string.month_november_short)
    Month.DECEMBER -> stringResource(Res.string.month_december_short)
    else -> stringResource(Res.string.month_january_short)
}

suspend fun Month.getShortTitleSuspend() = when (this) {
    Month.JANUARY -> getString(Res.string.month_january_short)
    Month.FEBRUARY -> getString(Res.string.month_february_short)
    Month.MARCH -> getString(Res.string.month_march_short)
    Month.APRIL -> getString(Res.string.month_april_short)
    Month.MAY -> getString(Res.string.month_may_short)
    Month.JUNE -> getString(Res.string.month_june_short)
    Month.JULY -> getString(Res.string.month_july_short)
    Month.AUGUST -> getString(Res.string.month_august_short)
    Month.SEPTEMBER -> getString(Res.string.month_september_short)
    Month.OCTOBER -> getString(Res.string.month_october_short)
    Month.NOVEMBER -> getString(Res.string.month_november_short)
    Month.DECEMBER -> getString(Res.string.month_december_short)
    else -> getString(Res.string.month_january_short)
}

fun String.toCommonDate() = LocalDate.parse(this)

fun LocalDate.isOnTheSameWeekWithAnotherDay(anotherDay: LocalDate): Boolean {
    return this.calculateStartOfWeek() == anotherDay.calculateStartOfWeek()
}

fun getNearestDateForNotification(
    dates: List<LocalDate>,
    notificationTime: LocalTime
): LocalDate? {
    val currentTime = getCurrentDateTime()
    val isTodayDatePresent = dates.any { it == currentTime.date }

    return when {
        isTodayDatePresent.not() -> dates.firstOrNull()
        currentTime.time > notificationTime -> dates.getOrNull(1) //Next date
        else -> dates.firstOrNull()
    }
}