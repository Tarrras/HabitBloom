package com.horizondev.habitbloom.screens.habits.presentation.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.taskCompletionPercentage
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.some_tasks_completed
import org.jetbrains.compose.resources.stringResource

@Composable
fun HabitProgressIndicator(
    totalHabits: Int,
    totalCompletedHabits: Int,
    radius: Dp = 20.dp,
    indicatorBackgroundColor: Color = BloomTheme.colors.disabled.copy(alpha = 0.25f),
    mainColor: Color = BloomTheme.colors.primary,
    strokeHeight: Dp = 8.dp,
    animationDuration: Int = 800,
    animDelay: Int = 0,
) {
    val percentage = remember(totalHabits, totalCompletedHabits) {
        taskCompletionPercentage(
            habitsCount = totalHabits, completedHabits = totalCompletedHabits
        )
    }

    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val currentPercentage by animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animDelay,
        ),
        label = "",
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Column {
        Canvas(
            modifier = Modifier.fillMaxWidth()
        ) {
            drawRoundRect(
                color = indicatorBackgroundColor,
                cornerRadius = CornerRadius(radius.toPx()),
                size = Size(height = strokeHeight.toPx(), width = size.width)
            )

            drawRoundRect(
                color = mainColor,
                cornerRadius = CornerRadius(radius.toPx()),
                size = Size(height = strokeHeight.toPx(), width = size.width * currentPercentage)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(
                Res.string.some_tasks_completed,
                totalCompletedHabits,
                totalHabits
            ),
            style = BloomTheme.typography.body,
            color = BloomTheme.colors.textColor.primary,
        )

    }
}
