package com.horizondev.habitbloom.core.designComponents.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomProgressIndicator(
    modifier: Modifier = Modifier,
    percentage: Float,
    radius: Dp = 20.dp,
    mainColor: Color = BloomTheme.colors.primary,
    uncompletedColor: Color = BloomTheme.colors.disabled.copy(alpha = 0.25f),
    completedStrokeWidth: Dp = 14.dp,
    uncompletedStrokeWidth: Dp = 8.dp,
    animationDuration: Int = 800,
    animDelay: Int = 0,
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val currentPercentage = animateFloatAsState(
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

    Canvas(
        modifier = modifier
            .size(radius * 5f),
    ) {
        drawArc(
            color = uncompletedColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(uncompletedStrokeWidth.toPx(), cap = StrokeCap.Square),
        )

        drawArc(
            color = mainColor,
            startAngle = 270f,
            sweepAngle = (360 * currentPercentage.value),
            useCenter = false,
            style = Stroke(completedStrokeWidth.toPx(), cap = StrokeCap.Square),
        )
    }
}