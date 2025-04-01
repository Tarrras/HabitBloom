package com.horizondev.habitbloom.core.designComponents.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getCurrentDate
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.datetime.LocalDate

/**
 * Data class representing a date range selection
 */
data class DateSelection(
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
) {
    val isValid: Boolean get() = startDate != null && endDate != null
}

/**
 * A calendar component that allows selection of a date range.
 *
 * @param modifier The modifier to be applied to the component
 * @param selection The current date selection state
 * @param onSelectionChanged Callback when the selection changes
 * @param minDate The minimum selectable date (defaults to today)
 * @param monthsToShow The number of months to display in the future
 */
@Composable
fun DateRangeSelectionCalendar(
    modifier: Modifier = Modifier,
    selection: DateSelection,
    onSelectionChanged: (DateSelection) -> Unit,
    minDate: LocalDate = getCurrentDate(),
    monthsToShow: Int = 12
) {
    val coroutineScope = rememberCoroutineScope()
    val today = minDate

    // Convert Kotlin LocalDate to Java LocalDate for the calendar library
    val currentMonth = remember { YearMonth(today.year, today.monthNumber) }
    val startMonth = remember { currentMonth }
    val endMonth = remember { currentMonth.plusMonths(monthsToShow) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    Column(modifier = modifier.fillMaxWidth()) {
        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = firstDayOfWeek,
            outDateStyle = OutDateStyle.EndOfRow
        )

        // Days of week header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            for (dayOfWeek in daysOfWeek()) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = BloomTheme.colors.textColor.secondary,
                    text = dayOfWeek.name.take(1),
                    style = BloomTheme.typography.small
                )
            }
        }

        // Calendar
        VerticalCalendar(
            state = state,
            dayContent = { day ->
                Day(
                    day = day,
                    today = today,
                    selection = selection,
                    onClick = { clickedDay ->
                        if (clickedDay.position == DayPosition.MonthDate) {
                            val date = LocalDate(
                                year = clickedDay.date.year,
                                monthNumber = clickedDay.date.monthNumber,
                                dayOfMonth = clickedDay.date.dayOfMonth
                            )

                            // Only allow selection of today or future dates
                            if (date >= today) {
                                val newSelection = getUpdatedSelection(date, selection)
                                onSelectionChanged(newSelection)
                            }
                        }
                    }
                )
            },
            monthHeader = { month -> MonthHeader(month = month) }
        )
    }
}

@Composable
private fun MonthHeader(month: CalendarMonth) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = "${
                month.yearMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
            } ${month.yearMonth.year}",
            style = BloomTheme.typography.body,
            fontWeight = FontWeight.SemiBold,
            color = BloomTheme.colors.textColor.primary
        )
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    today: LocalDate,
    selection: DateSelection,
    onClick: (CalendarDay) -> Unit
) {
    val date = LocalDate(
        year = day.date.year,
        monthNumber = day.date.monthNumber,
        dayOfMonth = day.date.dayOfMonth
    )

    val isToday = date == today
    val isInCurrentMonth = day.position == DayPosition.MonthDate
    val isSelected = date == selection.startDate || date == selection.endDate
    val isInRange = selection.startDate != null && selection.endDate != null &&
            date >= selection.startDate && date <= selection.endDate

    val isSelectable = isInCurrentMonth && date >= today

    // Background color
    val backgroundColor = when {
        isSelected -> BloomTheme.colors.primary
        isInRange -> BloomTheme.colors.primary.copy(alpha = 0.2f)
        else -> androidx.compose.ui.graphics.Color.Transparent
    }

    // Text color
    val textColor = when {
        isSelected -> BloomTheme.colors.textColor.white
        !isInCurrentMonth -> BloomTheme.colors.disabled
        !isSelectable -> BloomTheme.colors.disabled
        isToday -> BloomTheme.colors.primary
        else -> BloomTheme.colors.textColor.primary
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                enabled = isSelectable,
                onClick = { onClick(day) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            style = BloomTheme.typography.body,
            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Updates the selection state based on a newly clicked date
 */
private fun getUpdatedSelection(
    clickedDate: LocalDate,
    currentSelection: DateSelection
): DateSelection {
    val (startDate, endDate) = currentSelection

    return when {
        // No dates selected yet - set as start date
        startDate == null -> DateSelection(startDate = clickedDate)

        // Start date selected, end date not selected
        endDate == null -> {
            if (clickedDate < startDate) {
                // If clicked date is before start date, make it the new start date
                DateSelection(startDate = clickedDate)
            } else {
                // Otherwise set it as end date
                DateSelection(startDate = startDate, endDate = clickedDate)
            }
        }

        // Both dates already selected - start a new selection
        else -> DateSelection(startDate = clickedDate)
    }
} 