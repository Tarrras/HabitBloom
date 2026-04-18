package com.horizondev.habitbloom.screens.habits.presentation.components.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.buttons.BloomPrimaryFilledButton
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarControlButton
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarLegendItem
import com.horizondev.habitbloom.core.designComponents.calendar.CalendarStatusDot
import com.horizondev.habitbloom.core.designComponents.containers.BloomSurface
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.UserHabitFullInfo
import com.horizondev.habitbloom.utils.calculateStartOfWeek
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getShortTitle
import com.horizondev.habitbloom.utils.plusDays
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.habit_day_state_completed
import habitbloom.composeapp.generated.resources.habit_day_state_missed
import habitbloom.composeapp.generated.resources.ic_lucid_calendar
import habitbloom.composeapp.generated.resources.this_week_short_label
import habitbloom.composeapp.generated.resources.view_full_calendar
import habitbloom.composeapp.generated.resources.weekly_progress_title
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserHabitScheduleCard(
    modifier: Modifier = Modifier,
    habitInfo: UserHabitFullInfo
) {
    val today = remember { getCurrentDate() }
    val recordsByDate = remember(habitInfo.records) {
        habitInfo.records.associateBy { it.date }
    }
    var weekOffset by remember { mutableStateOf(0) }
    var showCalendarSheet by remember { mutableStateOf(false) }

    val currentWeekStart = remember(today) { today.calculateStartOfWeek() }
    val weekStart = currentWeekStart.plusDays(weekOffset * 7L)
    val weekDates = remember(weekStart) {
        (0..6).map { offset -> weekStart.plusDays(offset.toLong()) }
    }
    val scheduledCount = weekDates.count { date -> recordsByDate[date] != null }
    val completedCount = weekDates.count { date -> recordsByDate[date]?.isCompleted == true }
    val progress =
        if (scheduledCount > 0) completedCount.toFloat() / scheduledCount.toFloat() else 0f

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.weekly_progress_title),
                style = BloomTheme.typography.headlineMedium,
                color = BloomTheme.colors.textColor.primary,
                modifier = Modifier.weight(1f)
            )

            CalendarControlButton(onClick = { showCalendarSheet = true }) {
                Icon(
                    painter = painterResource(Res.drawable.ic_lucid_calendar),
                    contentDescription = null,
                    tint = BloomTheme.colors.primary
                )
            }

            CalendarControlButton(onClick = { weekOffset -= 1 }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = BloomTheme.colors.textColor.secondary
                )
            }

            Text(
                text = stringResource(Res.string.this_week_short_label),
                style = BloomTheme.typography.bodyMedium,
                color = BloomTheme.colors.textColor.secondary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { weekOffset = 0 }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            CalendarControlButton(onClick = { weekOffset += 1 }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = BloomTheme.colors.textColor.secondary
                )
            }
        }

        BloomSurface(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, BloomTheme.colors.glassBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.weekly_progress_title),
                        style = BloomTheme.typography.bodyMedium,
                        color = BloomTheme.colors.textColor.secondary
                    )
                    Text(
                        text = "$completedCount/$scheduledCount",
                        style = BloomTheme.typography.bodyMedium,
                        color = BloomTheme.colors.textColor.primary
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(
                            color = BloomTheme.colors.border.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(12.dp)
                            .background(
                                color = BloomTheme.colors.primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    weekDates.forEach { date ->
                        WeeklyProgressDay(
                            modifier = Modifier.weight(1f),
                            dayOfWeek = date.dayOfWeek,
                            dayOfMonth = date.day,
                            state = resolveHabitRecordState(
                                record = recordsByDate[date],
                                date = date,
                                today = today
                            ),
                            isSelected = date == today
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarLegendItem(
                        color = BloomTheme.colors.success,
                        label = stringResource(Res.string.habit_day_state_completed)
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    CalendarLegendItem(
                        color = BloomTheme.colors.destructive,
                        label = stringResource(Res.string.habit_day_state_missed)
                    )
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BloomTheme.colors.border.copy(alpha = 0.35f))
                )

                BloomPrimaryFilledButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.view_full_calendar),
                    onClick = { showCalendarSheet = true },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_lucid_calendar),
                            contentDescription = null,
                            tint = BloomTheme.colors.textColor.white
                        )
                    }
                )
            }
        }
    }

    if (showCalendarSheet) {
        HabitCalendarBottomSheet(
            habitName = habitInfo.name,
            habitStartDate = habitInfo.startDate,
            habitEndDate = habitInfo.endDate,
            recordsByDate = recordsByDate,
            today = today,
            onDismiss = { showCalendarSheet = false }
        )
    }
}

@Composable
private fun WeeklyProgressDay(
    modifier: Modifier = Modifier,
    dayOfWeek: DayOfWeek,
    dayOfMonth: Int,
    state: HabitRecordVisualState,
    isSelected: Boolean
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = dayOfWeek.getShortTitle(),
            style = BloomTheme.typography.bodyLarge,
            color = BloomTheme.colors.textColor.secondary
        )

        BloomSurface(
            modifier = Modifier.fillMaxWidth(),
            color = if (isSelected) {
                BloomTheme.colors.primary
            } else {
                BloomTheme.colors.inputBackground.copy(alpha = 0.7f)
            },
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayOfMonth.toString(),
                    style = BloomTheme.typography.headlineSmall,
                    color = if (isSelected) {
                        BloomTheme.colors.primaryForeground
                    } else {
                        BloomTheme.colors.textColor.primary
                    }
                )

                CalendarStatusDot(
                    color = state.toStatusColor(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
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
