package com.horizondev.habitbloom.core.designComponents.pickers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.getCurrentDate
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.plus

/**
 * A composable for selecting a date range.
 *
 * @param startDate The currently selected start date
 * @param endDate The currently selected end date
 * @param onStartDateSelected Callback when a new start date is selected
 * @param onEndDateSelected Callback when a new end date is selected
 * @param modifier The modifier to be applied to the composable
 * @param minDate The minimum selectable date (defaults to today)
 * @param maxDate The maximum selectable date (defaults to 1 year from today)
 */
@Composable
fun DateRangePicker(
    startDate: LocalDate?,
    endDate: LocalDate?,
    onStartDateSelected: (LocalDate) -> Unit,
    onEndDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    minDate: LocalDate = getCurrentDate(),
    maxDate: LocalDate = minDate.plus(1, DateTimeUnit.YEAR)
) {
    val coroutineScope = rememberCoroutineScope()

    // Convert kotlinx.datetime.LocalDate to java.time.LocalDate for the calendar library
    val currentMonth = remember {
        YearMonth(minDate.year, minDate.monthNumber)
    }

    // Set up calendar state
    val startMonth = remember { currentMonth.minusMonths(0) }
    val endMonth = remember { currentMonth.plusMonths(12) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    var selectedStartDate by remember(startDate) { mutableStateOf(startDate) }
    var selectedEndDate by remember(endDate) { mutableStateOf(endDate) }
    var inSelectionMode by remember { mutableStateOf(false) }

    // Track which date we're currently editing (start or end)
    var selectingStartDate by remember { mutableStateOf(true) }

    var currentVisibleMonth by remember { mutableStateOf(currentMonth) }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfRow,
    )

    // Update visible month
    LaunchedEffect(state.firstVisibleMonth) {
        currentVisibleMonth = state.firstVisibleMonth.yearMonth
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = BloomTheme.colors.background,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = BloomTheme.colors.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        // Calendar header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(
                            state.firstVisibleMonth.yearMonth.minusMonths(
                                1
                            )
                        )
                    }
                },
                enabled = currentVisibleMonth > startMonth
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous month",
                    tint = if (currentVisibleMonth > startMonth)
                        BloomTheme.colors.textColor.primary
                    else BloomTheme.colors.disabled
                )
            }

            Text(
                text = "${
                    currentVisibleMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
                } ${currentVisibleMonth.year}",
                style = BloomTheme.typography.subheading,
                fontWeight = FontWeight.SemiBold,
                color = BloomTheme.colors.textColor.primary
            )

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        state.animateScrollToMonth(
                            state.firstVisibleMonth.yearMonth.plusMonths(
                                1
                            )
                        )
                    }
                },
                enabled = currentVisibleMonth < endMonth
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next month",
                    tint = if (currentVisibleMonth < endMonth)
                        BloomTheme.colors.textColor.primary
                    else BloomTheme.colors.disabled
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Selection mode indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SelectionIndicator(
                text = "Start Date",
                isSelected = selectingStartDate,
                onClick = { selectingStartDate = true }
            )

            Spacer(modifier = Modifier.size(16.dp))

            SelectionIndicator(
                text = "End Date",
                isSelected = !selectingStartDate,
                onClick = { selectingStartDate = false }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days of week header
        DaysOfWeekHeader(daysOfWeek = daysOfWeek())

        // Calendar
        HorizontalCalendar(
            state = state,
            dayContent = { calendarDay ->
                val date = LocalDate(
                    year = calendarDay.date.year,
                    monthNumber = calendarDay.date.monthNumber,
                    dayOfMonth = calendarDay.date.dayOfMonth
                )

                val isInRange = selectedStartDate != null && selectedEndDate != null &&
                        date >= selectedStartDate!! && date <= selectedEndDate!!

                val isSelected = date == selectedStartDate || date == selectedEndDate

                val isSelectable = date >= minDate && date <= maxDate &&
                        calendarDay.position == DayPosition.MonthDate

                DateCell(
                    date = date,
                    isSelected = isSelected,
                    isInRange = isInRange,
                    isStartDate = date == selectedStartDate,
                    isEndDate = date == selectedEndDate,
                    isSelectable = isSelectable,
                    onClick = {
                        if (isSelectable) {
                            if (selectingStartDate) {
                                if (selectedEndDate != null && date > selectedEndDate!!) {
                                    // If the new start date is after the current end date,
                                    // reset the end date
                                    selectedEndDate = null
                                }
                                selectedStartDate = date
                                onStartDateSelected(date)
                                // Automatically switch to end date selection
                                selectingStartDate = false
                            } else {
                                if (selectedStartDate != null && date < selectedStartDate!!) {
                                    // If the end date is before the start date,
                                    // use it as the start date and keep the end date empty
                                    selectedStartDate = date
                                    selectedEndDate = null
                                    onStartDateSelected(date)
                                } else {
                                    selectedEndDate = date
                                    onEndDateSelected(date)
                                }
                            }
                        }
                    }
                )
            }
        )
    }
}

@Composable
private fun DateCell(
    date: LocalDate,
    isSelected: Boolean,
    isInRange: Boolean,
    isStartDate: Boolean,
    isEndDate: Boolean,
    isSelectable: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> BloomTheme.colors.primary
        isInRange -> BloomTheme.colors.primary.copy(alpha = 0.2f)
        else -> BloomTheme.colors.background
    }

    val textColor = when {
        isSelected -> BloomTheme.colors.textColor.white
        !isSelectable -> BloomTheme.colors.disabled
        else -> BloomTheme.colors.textColor.primary
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(enabled = isSelectable, onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = BloomTheme.typography.body,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DaysOfWeekHeader(daysOfWeek: List<kotlinx.datetime.DayOfWeek>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                text = dayOfWeek.name.take(1),
                style = BloomTheme.typography.small,
                color = BloomTheme.colors.textColor.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SelectionIndicator(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = BloomTheme.typography.small,
            color = if (isSelected) BloomTheme.colors.primary else BloomTheme.colors.textColor.secondary
        )

        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(width = 24.dp, height = 2.dp)
                    .background(BloomTheme.colors.primary)
            )
        }
    }
} 