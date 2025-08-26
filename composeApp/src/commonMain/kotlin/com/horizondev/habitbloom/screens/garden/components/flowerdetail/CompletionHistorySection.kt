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
import com.horizondev.habitbloom.utils.getCurrentDate
import com.horizondev.habitbloom.utils.getShortTitle
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.completed_on_date
import habitbloom.composeapp.generated.resources.ic_cancel
import habitbloom.composeapp.generated.resources.ic_check_circle
import habitbloom.composeapp.generated.resources.last_seven_days
import habitbloom.composeapp.generated.resources.not_completed_on_date
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
        modifier = modifier,
        onClick = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.last_seven_days),
                style = BloomTheme.typography.heading,
                color = BloomTheme.colors.textColor.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                completions.forEachIndexed { index, dailyCompletion ->
                    DayCompletionItem(
                        date = dailyCompletion.date,
                        isCompleted = dailyCompletion.isCompleted,
                        isToday = dailyCompletion.date == getCurrentDate(),
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
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.dayOfWeek.getShortTitle(),
            style = BloomTheme.typography.small.copy(
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            ),
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
            stringResource(Res.string.completed_on_date, date.toString())
        } else {
            stringResource(Res.string.not_completed_on_date, date.toString())
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
            text = date.day.toString(),
            style = BloomTheme.typography.small.copy(
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            ),
            color = BloomTheme.colors.textColor.primary,
            textAlign = TextAlign.Center
        )
    }
} 