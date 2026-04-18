package com.horizondev.habitbloom.screens.habits.presentation.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomSmallActionButton
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarControlButton
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarLegendItem
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarStatusDot
import com.horizondev.habitbloom.core.designComponents.components.BloomHorizontalDivider
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitRecord
import com.horizondev.habitbloom.utils.getShortTitle
import com.horizondev.habitbloom.utils.getTitle
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.cancel
import habitbloom.composeapp.generated.resources.habit_calendar_sheet_title
import habitbloom.composeapp.generated.resources.habit_day_state_completed
import habitbloom.composeapp.generated.resources.habit_day_state_missed
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HabitCalendarBottomSheet(
    habitName: String,
    habitStartDate: LocalDate,
    habitEndDate: LocalDate,
    recordsByDate: Map<LocalDate, UserHabitRecord>,
    today: LocalDate,
    onDismiss: () -> Unit
) {
    val minMonth = remember(habitStartDate, habitEndDate) {
        val start = YearMonth(habitStartDate.year, habitStartDate.month)
        val end = YearMonth(habitEndDate.year, habitEndDate.month)
        if (start <= end) start else end
    }
    val maxMonth = remember(habitStartDate, habitEndDate) {
        val start = YearMonth(habitStartDate.year, habitStartDate.month)
        val end = YearMonth(habitEndDate.year, habitEndDate.month)
        if (start <= end) end else start
    }
    val initialVisibleMonth = remember(today, minMonth, maxMonth) {
        val thisMonth = YearMonth(today.year, today.month)
        when {
            thisMonth < minMonth -> minMonth
            thisMonth > maxMonth -> maxMonth
            else -> thisMonth
        }
    }

    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    val monthState = rememberCalendarState(
        startMonth = minMonth,
        endMonth = maxMonth,
        firstVisibleMonth = initialVisibleMonth,
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfRow
    )
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentMonth = monthState.firstVisibleMonth.yearMonth
    val canGoPrevious = currentMonth > minMonth
    val canGoNext = currentMonth < maxMonth

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BloomTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(Res.string.habit_calendar_sheet_title),
                style = BloomTheme.typography.headlineMedium,
                color = BloomTheme.colors.textColor.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = habitName,
                style = BloomTheme.typography.bodyMedium,
                color = BloomTheme.colors.textColor.secondary
            )

            Spacer(modifier = Modifier.height(12.dp))
            BloomHorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CalendarControlButton(
                    enabled = canGoPrevious,
                    onClick = {
                        coroutineScope.launch {
                            monthState.animateScrollToMonth(currentMonth.minusMonths(1))
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "previous month",
                        tint = if (canGoPrevious) {
                            BloomTheme.colors.textColor.primary
                        } else {
                            BloomTheme.colors.textColor.disabled
                        }
                    )
                }

                Text(
                    text = "${currentMonth.month.getTitle()} ${currentMonth.year}",
                    style = BloomTheme.typography.headlineSmall,
                    color = BloomTheme.colors.textColor.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                CalendarControlButton(
                    enabled = canGoNext,
                    onClick = {
                        coroutineScope.launch {
                            monthState.animateScrollToMonth(currentMonth.plusMonths(1))
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "next month",
                        tint = if (canGoNext) {
                            BloomTheme.colors.textColor.primary
                        } else {
                            BloomTheme.colors.textColor.disabled
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (dayOfWeek in daysOfWeek(firstDayOfWeek = firstDayOfWeek)) {
                    Text(
                        text = dayOfWeek.getShortTitle(),
                        style = BloomTheme.typography.labelMedium,
                        color = BloomTheme.colors.textColor.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalCalendar(
                state = monthState,
                dayContent = { day ->
                    val date = LocalDate(day.date.year, day.date.month, day.date.day)
                    HabitRecordCalendarDay(
                        day = day,
                        state = resolveHabitRecordState(
                            record = recordsByDate[date],
                            date = date,
                            today = today
                        ),
                        today = today
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
            BloomHorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CalendarLegendItem(
                    color = BloomTheme.colors.success,
                    label = stringResource(Res.string.habit_day_state_completed)
                )
                Spacer(modifier = Modifier.width(28.dp))
                CalendarLegendItem(
                    color = BloomTheme.colors.destructive,
                    label = stringResource(Res.string.habit_day_state_missed)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            BloomHorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            BloomSmallActionButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.cancel),
                onClick = onDismiss
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HabitRecordCalendarDay(
    modifier: Modifier = Modifier,
    day: CalendarDay,
    state: HabitRecordVisualState,
    today: LocalDate
) {
    val date = LocalDate(day.date.year, day.date.month, day.date.day)
    val isInCurrentMonth = day.position == DayPosition.MonthDate
    val isToday = date == today

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    color = if (isToday && isInCurrentMonth) {
                        BloomTheme.colors.primary.copy(alpha = 0.2f)
                    } else {
                        androidx.compose.ui.graphics.Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.date.day.toString(),
                style = BloomTheme.typography.bodyLarge,
                color = when {
                    !isInCurrentMonth -> BloomTheme.colors.textColor.disabled
                    isToday -> BloomTheme.colors.primary
                    else -> BloomTheme.colors.textColor.primary
                }
            )

            if (isInCurrentMonth) {
                CalendarStatusDot(
                    color = state.toStatusColor(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun HabitRecordVisualState.toStatusColor() = when (this) {
    HabitRecordVisualState.Completed -> BloomTheme.colors.success
    HabitRecordVisualState.Missed -> BloomTheme.colors.destructive
    HabitRecordVisualState.Future, HabitRecordVisualState.None -> null
}
