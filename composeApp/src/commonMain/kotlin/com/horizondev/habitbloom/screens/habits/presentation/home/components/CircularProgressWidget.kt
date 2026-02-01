package com.horizondev.habitbloom.screens.habits.presentation.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.getProgressTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.habits_completed_of
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProgressSection(
    modifier: Modifier = Modifier,
    completedHabits: Int,
    totalHabits: Int,
    selectedPeriod: TimeOfDay,
    trackColor: Color = BloomTheme.colors.border.copy(alpha = 0.6f),
    progressBrush: Brush = Brush.linearGradient(
        colors = listOf(
            BloomTheme.colors.primary,
            BloomTheme.colors.primaryVariant
        )
    )
) {
    val progress = if (totalHabits > 0) completedHabits.toFloat() / totalHabits else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(700)
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BloomTheme.colors.glassBackgroundStrong,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(128.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 16f
                        val radius = size.minDimension / 2 - strokeWidth

                        // Background ring
                        drawCircle(
                            color = trackColor,
                            radius = radius,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Progress ring
                        drawArc(
                            brush = progressBrush,
                            startAngle = -90f,
                            sweepAngle = 360 * animatedProgress,
                            useCenter = false,
                            topLeft = Offset(strokeWidth, strokeWidth),
                            size = Size(
                                size.width - 2 * strokeWidth,
                                size.height - 2 * strokeWidth
                            ),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Center content (number + emoji)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$completedHabits",
                            fontSize = 28.sp,
                            color = BloomTheme.colors.foreground,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = selectedPeriod.getPeriodEmoji(),
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Progress title
                Text(
                    text = selectedPeriod.getProgressTitle(),
                    style = BloomTheme.typography.headlineMedium,
                    color = BloomTheme.colors.textColor.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Progress subtitle
                Text(
                    text = stringResource(
                        Res.string.habits_completed_of,
                        completedHabits,
                        totalHabits
                    ),
                    style = BloomTheme.typography.bodyMedium,
                    color = BloomTheme.colors.textColor.secondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun TimeOfDay.getPeriodEmoji(): String = when (this) {
    TimeOfDay.Morning -> "🌅"
    TimeOfDay.Afternoon -> "☀️"
    TimeOfDay.Evening -> "🌙"
}
