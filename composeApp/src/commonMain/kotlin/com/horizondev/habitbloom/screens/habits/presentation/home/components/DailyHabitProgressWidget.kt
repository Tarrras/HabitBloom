package com.horizondev.habitbloom.screens.habits.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.daily_summary
import habitbloom.composeapp.generated.resources.some_tasks_completed
import org.jetbrains.compose.resources.stringResource

@Composable
fun DailyHabitProgressWidget(
    modifier: Modifier = Modifier,
    habitsCount: Int = 0,
    completedHabitsCount: Int = 0,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(Res.string.daily_summary),
            style = BloomTheme.typography.title,
            color = BloomTheme.colors.textColor.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(
                Res.string.some_tasks_completed,
                completedHabitsCount,
                habitsCount
            ),
            style = BloomTheme.typography.subheading,
            color = BloomTheme.colors.textColor.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        HabitProgressIndicator(
            dailyHabits = habitsCount,
            completedHabits = completedHabitsCount
        )
    }
}