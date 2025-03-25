package com.horizondev.habitbloom.screens.flowerdetail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.flowerdetail.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.afternoon_habits_image
import habitbloom.composeapp.generated.resources.evening_habits_image
import habitbloom.composeapp.generated.resources.morning_habits_image
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

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

                val timeOfDayColor = when (timeOfDay) {
                    TimeOfDay.Morning -> Color(0xFFFFC107)
                    TimeOfDay.Afternoon -> Color(0xFFFF9800)
                    TimeOfDay.Evening -> Color(0xFF673AB7)
                }

                Image(
                    painter = painterResource(timeOfDayIcon),
                    contentDescription = "$timeOfDay time",
                    modifier = Modifier
                        .padding(4.dp)
                        .size(48.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

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
                    text = "Current Stage: ",
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary
                )

                Text(
                    text = "growthStage.stageName", //todo add later
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
                    text = "Current Streak: ",
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary
                )

                Text(
                    text = "$currentStreak ${if (currentStreak == 1) "day" else "days"} in a row",
                    style = BloomTheme.typography.body,
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
                        text = "Progress to next stage: ",
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary
                    )

                    Text(
                        text = "$streaksToNextStage more ${if (streaksToNextStage == 1) "completion" else "completions"} to grow",
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Text(
                    text = "Congratulations! Your flower has reached full bloom!",
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.success,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
} 