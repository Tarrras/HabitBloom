package com.horizondev.habitbloom.habits.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.horizondev.habitbloom.core.designSystem.BloomTheme

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
            "Habit Bloom",
            style = BloomTheme.typography.heading,
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center,
            modifier = modifier
        )
    }
}