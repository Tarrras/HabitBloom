package com.horizondev.habitbloom.core.designComponents.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getShortTitle
import com.horizondev.habitbloom.utils.getTitle
import com.horizondev.habitbloom.utils.plusDays
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

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
    maxDurationDays: Int = 7,
) {
    val today = minDate

    val currentMonth = remember { YearMonth(today.year, today.month) }
    val startMonth = remember { currentMonth }
    val endMonth = remember(minDate) {
        minDate.plusDays(maxDurationDays.toLong()).let {
            YearMonth(
                year = it.year,
                month = it.month
            )
        }
    }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfRow
    )

    Column(modifier = modifier.fillMaxWidth()) {
        val coroutineScope = rememberCoroutineScope()

        // Month title with arrows (compact header)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "prev",
                tint = BloomTheme.colors.textColor.primary,
                modifier = Modifier.clip(CircleShape).clickable {
                    coroutineScope.launch {
                        state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.minusMonths(1))
                    }
                }
            )
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = "${state.firstVisibleMonth.yearMonth.month.getTitle()} ${state.firstVisibleMonth.yearMonth.year}",
                style = BloomTheme.typography.headlineSmall,
                color = BloomTheme.colors.textColor.primary
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "next",
                tint = BloomTheme.colors.textColor.primary,
                modifier = Modifier.clip(CircleShape).clickable {
                    coroutineScope.launch {
                        state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.plusMonths(1))
                    }
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        // Days of week header (compact)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        ) {
            for (dayOfWeek in daysOfWeek()) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = BloomTheme.colors.textColor.secondary,
                    text = dayOfWeek.getShortTitle(),
                    style = BloomTheme.typography.labelMedium
                )
            }
        }

        HorizontalCalendar(
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
                                month = clickedDay.date.month,
                                day = clickedDay.date.day
                            )

                            if (date >= today) {
                                val newSelection = getUpdatedSelection(date, selection)
                                onSelectionChanged(newSelection)
                            }
                        }
                    }
                )
            },
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
        month = day.date.month,
        day = day.date.day
    )

    val isToday = date == today
    val isInCurrentMonth = day.position == DayPosition.MonthDate
    val isSelected = date == selection.startDate || date == selection.endDate
    val isInRange = selection.startDate != null && selection.endDate != null &&
            date >= selection.startDate && date <= selection.endDate

    val isSelectable = isInCurrentMonth && date >= today

    val backgroundShape = when {
        date == selection.startDate && selection.endDate == null -> CircleShape
        date == selection.startDate -> RoundedCornerShape(
            topStart = 16.dp,
            bottomStart = 16.dp
        )

        date == selection.endDate -> {
            RoundedCornerShape(
                topEnd = 16.dp,
                bottomEnd = 16.dp
            )
        }

        isInRange -> RectangleShape
        else -> RectangleShape
    }

    // Background color
    val backgroundColor = when {
        isSelected -> BloomTheme.colors.primary
        isInRange -> BloomTheme.colors.primary
        else -> Color.Transparent
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
            .padding(vertical = 4.dp)
            .clip(backgroundShape)
            .background(backgroundColor)
            .clickable(
                enabled = isSelectable,
                onClick = { onClick(day) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.day.toString(),
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
                DateSelection(startDate = clickedDate, endDate = startDate)
            } else {
                // Otherwise set it as end date
                DateSelection(startDate = startDate, endDate = clickedDate)
            }
        }

        else -> {
            when {
                clickedDate < startDate -> {
                    // If clicked date is before start date, make it the new start date
                    DateSelection(startDate = clickedDate, endDate = endDate)
                }

                clickedDate > endDate -> {
                    // If clicked date is after end date, make it the new end date
                    DateSelection(startDate = startDate, endDate = clickedDate)
                }

                clickedDate in startDate..endDate -> {
                    // If clicked date is within the range, reset the selection
                    DateSelection(startDate = clickedDate)
                }

                else -> {
                    // Otherwise set it as end date
                    DateSelection(startDate = clickedDate)
                }
            }
        }
    }
} 