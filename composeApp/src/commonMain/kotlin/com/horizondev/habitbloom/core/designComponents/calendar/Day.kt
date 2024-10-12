package com.horizondev.habitbloom.core.designComponents.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.habit_day_state_completed
import habitbloom.composeapp.generated.resources.habit_day_state_future
import habitbloom.composeapp.generated.resources.habit_day_state_missed
import org.jetbrains.compose.resources.stringResource

@Composable
fun Day(
    day: CalendarDay,
    state: HabitDayState,
    selected: Boolean = false
) {
    Box(
        modifier = Modifier.aspectRatio(1f), // This is important for square sizing!
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            style = BloomTheme.typography.body.copy(
                fontWeight = if (selected) {
                    FontWeight.Bold
                } else FontWeight.Normal
            ),
            color = when {
                day.position != DayPosition.MonthDate -> BloomTheme.colors.textColor.disabled
                selected -> BloomTheme.colors.primary
                else -> BloomTheme.colors.textColor.primary
            },
            modifier = Modifier.align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .height(4.dp)
                .background(color = state.color(), shape = RoundedCornerShape(16.dp))
        )
    }
}

enum class HabitDayState {
    Completed,
    Missed,
    Future,
    None
}

@Composable
fun HabitDayState.color(): Color {
    return when (this) {
        HabitDayState.Completed -> BloomTheme.colors.success
        HabitDayState.Missed -> BloomTheme.colors.secondary
        HabitDayState.Future -> BloomTheme.colors.tertiary
        HabitDayState.None -> Color.Transparent
    }
}

@Composable
fun HabitDayState.title(): String {
    return when (this) {
        HabitDayState.Completed -> stringResource(Res.string.habit_day_state_completed)
        HabitDayState.Missed -> stringResource(Res.string.habit_day_state_missed)
        HabitDayState.Future -> stringResource(Res.string.habit_day_state_future)
        HabitDayState.None -> ""
    }
}