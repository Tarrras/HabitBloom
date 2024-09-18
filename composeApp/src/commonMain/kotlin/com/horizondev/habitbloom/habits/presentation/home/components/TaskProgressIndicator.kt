package com.horizondev.habitbloom.habits.presentation.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.habitsCompleteMessage
import com.horizondev.habitbloom.utils.taskCompletionPercentage
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HabitProgressIndicator(
    dailyHabits: Int,
    completedHabits: Int,
    radius: Dp = 20.dp,
    indicatorBackgroundColor: Color = BloomTheme.colors.disabled.copy(alpha = 0.25f),
    mainColor: Color = BloomTheme.colors.primary,
    strokeWidth: Dp = 8.dp,
    animationDuration: Int = 800,
    animDelay: Int = 0,
) {
    val percentage = remember(dailyHabits, completedHabits) {
        taskCompletionPercentage(
            habitsCount = dailyHabits, completedHabits = completedHabits
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
                size = Size(height = strokeWidth.toPx(), width = size.width)
            )

            drawRoundRect(
                color = mainColor,
                cornerRadius = CornerRadius(radius.toPx()),
                size = Size(height = strokeWidth.toPx(), width = size.width * currentPercentage)
            )
        }


        if (dailyHabits > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = habitsCompleteMessage(
                    habitsCount = dailyHabits,
                    completedHabits = completedHabits
                ),
                style = BloomTheme.typography.body,
                color = BloomTheme.colors.textColor.secondary,
            )
        }
    }
}
