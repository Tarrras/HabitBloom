package com.horizondev.habitbloom.screens.garden.components.flowerdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.garden.domain.getTitle
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.afternoon_habits_image
import habitbloom.composeapp.generated.resources.completion
import habitbloom.composeapp.generated.resources.completions
import habitbloom.composeapp.generated.resources.congratulations_full_bloom
import habitbloom.composeapp.generated.resources.current_stage
import habitbloom.composeapp.generated.resources.current_streak
import habitbloom.composeapp.generated.resources.day
import habitbloom.composeapp.generated.resources.days
import habitbloom.composeapp.generated.resources.days_in_row
import habitbloom.composeapp.generated.resources.evening_habits_image
import habitbloom.composeapp.generated.resources.more_completions_to_grow
import habitbloom.composeapp.generated.resources.morning_habits_image
import habitbloom.composeapp.generated.resources.progress_to_next_stage
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

/**
 * Component to display habit information section.
 *
 * @param habitName The name of the habit
 * @param timeOfDay The time of day associated with the habit
 * @param growthStage The current growth stage of the flower
 * @param currentStreak The current streak of the habit
 * @param streaksToNextStage The number of streaks needed to reach the next stage
 * @param modifier Modifier for styling
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun HabitInfoSection(
    habitName: String,
    timeOfDay: TimeOfDay,
    growthStage: FlowerGrowthStage,
    currentStreak: Int,
    streaksToNextStage: Int,
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
            // Habit name and time of day
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time of day icon
                val timeOfDayIcon = when (timeOfDay) {
                    TimeOfDay.Morning -> Res.drawable.morning_habits_image
                    TimeOfDay.Afternoon -> Res.drawable.afternoon_habits_image
                    TimeOfDay.Evening -> Res.drawable.evening_habits_image
                }

                // Habit name
                Text(
                    text = habitName,
                    style = BloomTheme.typography.heading,
                    color = BloomTheme.colors.textColor.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Growth stage
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.current_stage),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = growthStage.getTitle(),
                    style = BloomTheme.typography.subheading,
                    color = BloomTheme.colors.textColor.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Current streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.current_streak) + ": ",
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary
                )

                val daysText = if (currentStreak == 1)
                    stringResource(Res.string.day)
                else
                    stringResource(Res.string.days)
                
                Text(
                    text = stringResource(Res.string.days_in_row, currentStreak, daysText),
                    style = BloomTheme.typography.subheading,
                    color = BloomTheme.colors.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress to next stage
            if (streaksToNextStage > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.progress_to_next_stage),
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    val completionsText = if (streaksToNextStage == 1)
                        stringResource(Res.string.completion)
                    else
                        stringResource(Res.string.completions)
                    
                    Text(
                        text = stringResource(
                            Res.string.more_completions_to_grow,
                            streaksToNextStage,
                            completionsText
                        ),
                        style = BloomTheme.typography.subheading,
                        color = BloomTheme.colors.textColor.primary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = stringResource(Res.string.congratulations_full_bloom),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.success,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
} 