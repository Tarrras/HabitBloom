package com.horizondev.habitbloom.screens.habits.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.add_afternoon_habit
import habitbloom.composeapp.generated.resources.add_evening_habit
import habitbloom.composeapp.generated.resources.add_morning_habit
import habitbloom.composeapp.generated.resources.afternoon_habits_image
import habitbloom.composeapp.generated.resources.evening_habits_image
import habitbloom.composeapp.generated.resources.morning_habits_image
import habitbloom.composeapp.generated.resources.no_afternoon_habits
import habitbloom.composeapp.generated.resources.no_evening_habits
import habitbloom.composeapp.generated.resources.no_morning_habits
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyHabitsForTimeOfDayPlaceholder(
    modifier: Modifier = Modifier,
    selectTimeOfDay: TimeOfDay
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Image(
            painter = selectTimeOfDay.placeholderImage(),
            contentDescription = selectTimeOfDay.name,
            modifier = Modifier.size(250.dp),
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = selectTimeOfDay.placeholderTitle(),
            style = BloomTheme.typography.heading,
            color = BloomTheme.colors.textColor.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = selectTimeOfDay.placeholderText(),
            style = BloomTheme.typography.subheading,
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TimeOfDay.placeholderImage(): Painter {
    return when (this) {
        TimeOfDay.Morning -> painterResource(Res.drawable.morning_habits_image)
        TimeOfDay.Afternoon -> painterResource(Res.drawable.afternoon_habits_image)
        TimeOfDay.Evening -> painterResource(Res.drawable.evening_habits_image)
    }
}

@Composable
fun TimeOfDay.placeholderTitle(): String {
    return when (this) {
        TimeOfDay.Morning -> stringResource(Res.string.no_morning_habits)
        TimeOfDay.Afternoon -> stringResource(Res.string.no_afternoon_habits)
        TimeOfDay.Evening -> stringResource(Res.string.no_evening_habits)
    }
}

@Composable
fun TimeOfDay.placeholderText(): String {
    return when (this) {
        TimeOfDay.Morning -> stringResource(Res.string.add_morning_habit)
        TimeOfDay.Afternoon -> stringResource(Res.string.add_afternoon_habit)
        TimeOfDay.Evening -> stringResource(Res.string.add_evening_habit)
    }
}