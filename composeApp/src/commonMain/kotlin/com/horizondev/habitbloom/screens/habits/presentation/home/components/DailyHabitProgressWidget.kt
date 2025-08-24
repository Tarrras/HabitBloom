package com.horizondev.habitbloom.screens.habits.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import com.horizondev.habitbloom.utils.getChartBorder
import com.horizondev.habitbloom.utils.getChartColor
import com.horizondev.habitbloom.utils.taskCompletionPercentage
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.remaining
import habitbloom.composeapp.generated.resources.today_progress
import org.jetbrains.compose.resources.stringResource

@Composable
fun DailyHabitProgressWidget(
    modifier: Modifier = Modifier,
    timeOfDay: TimeOfDay = TimeOfDay.Morning,
    habitsCount: Int = 0,
    completedHabitsCount: Int = 0,
) {
    val percentage = if (habitsCount > 0) {
        taskCompletionPercentage(habitsCount, completedHabitsCount)
    } else {
        0f
    }
    val remainingCount = habitsCount - completedHabitsCount

    BloomCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = timeOfDay.getChartColor()
        ),
        border = BorderStroke(width = 2.dp, color = timeOfDay.getChartBorder()),
        elevation = CardDefaults.cardElevation(0.dp),
        onClick = {}
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.today_progress),
                        style = BloomTheme.typography.heading.copy(fontSize = 18.sp),
                        color = BloomTheme.colors.textColor.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = BloomTheme.typography.title.copy(fontSize = 32.sp),
                    color = BloomTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            ModernProgressBar(
                progress = percentage,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$completedHabitsCount of $habitsCount completed",
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary
                )

                Text(
                    text = "$remainingCount ${stringResource(Res.string.remaining)}",
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary
                )
            }
        }
    }
}

@Composable
private fun ModernProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Int = 8
) {
    Row(
        modifier = modifier
            .height(height.dp)
            .background(
                color = BloomTheme.colors.disabled.copy(alpha = 0.3f),
                shape = RoundedCornerShape(height.dp)
            )
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(height.dp)
                .background(
                    color = BloomTheme.colors.primary,
                    shape = RoundedCornerShape(height.dp)
                )
        )
    }
}