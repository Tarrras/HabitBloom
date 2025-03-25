package com.horizondev.habitbloom.screens.garden.components.flowerdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.domain.HabitFlowerDetail
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.ic_cancel
import habitbloom.composeapp.generated.resources.ic_check_circle
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

/**
 * Component to display the 7-day completion history of a habit.
 *
 * @param completions List of daily completion statuses
 * @param modifier Modifier for styling
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun CompletionHistorySection(
    completions: List<HabitFlowerDetail.DailyCompletion>,
    modifier: Modifier = Modifier
) {
    BloomCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Last 7 Days",
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                completions.forEach { completion ->
                    DayCompletionItem(
                        date = completion.date,
                        isCompleted = completion.isCompleted,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Component to display a single day's completion status.
 *
 * @param date The date for the completion status
 * @param isCompleted Whether the habit was completed on this date
 * @param modifier Modifier for styling
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
private fun DayCompletionItem(
    date: LocalDate,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Day of the week (abbreviated)
        val dayOfWeek = date.dayOfWeek.name.take(3).lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        Text(
            text = dayOfWeek,
            style = BloomTheme.typography.small,
            color = BloomTheme.colors.textColor.secondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Completion status indicator
        val backgroundColor = if (isCompleted) {
            BloomTheme.colors.success.copy(alpha = 0.2f)
        } else {
            BloomTheme.colors.error.copy(alpha = 0.2f)
        }

        val iconTint = if (isCompleted) {
            BloomTheme.colors.success
        } else {
            BloomTheme.colors.error
        }

        val icon = if (isCompleted) {
            Res.drawable.ic_check_circle
        } else {
            Res.drawable.ic_cancel
        }

        val contentDescription = if (isCompleted) {
            "Completed on $date"
        } else {
            "Not completed on $date"
        }

        Column(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Day of month
        Text(
            text = date.dayOfMonth.toString(),
            style = BloomTheme.typography.small,
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center
        )
    }
} 