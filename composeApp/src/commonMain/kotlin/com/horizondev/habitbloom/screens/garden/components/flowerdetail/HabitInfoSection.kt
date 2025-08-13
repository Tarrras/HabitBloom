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
import com.horizondev.habitbloom.core.designComponents.charts.BloomLinearProgressIndicator
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth
import com.horizondev.habitbloom.screens.garden.domain.getTitle
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.afternoon_habits_image
import habitbloom.composeapp.generated.resources.congratulations_full_bloom
import habitbloom.composeapp.generated.resources.current_stage
import habitbloom.composeapp.generated.resources.evening_habits_image
import habitbloom.composeapp.generated.resources.flower_health_critical
import habitbloom.composeapp.generated.resources.flower_health_impact
import habitbloom.composeapp.generated.resources.flower_health_wilting
import habitbloom.composeapp.generated.resources.flower_potential_stage
import habitbloom.composeapp.generated.resources.level_label
import habitbloom.composeapp.generated.resources.morning_habits_image
import habitbloom.composeapp.generated.resources.vitality
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

/**
 * Component to display habit information section.
 *
 * @param habitName The name of the habit
 * @param timeOfDay The time of day associated with the habit
 * @param growthStage The current growth stage of the flower (already accounts for health)
 * @param streakBasedGrowthStage The growth stage based only on streak (without health effects) [Deprecated]
 * @param flowerHealth The health status of the flower
 * @param currentStreak The current streak of the habit [Deprecated]
 * @param streaksToNextStage The number of streaks needed to reach the next stage [Deprecated]
 * @param modifier Modifier for styling
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun HabitInfoSection(
    habitName: String,
    timeOfDay: TimeOfDay,
    growthStage: FlowerGrowthStage,
    streakBasedGrowthStage: FlowerGrowthStage,
    flowerHealth: FlowerHealth,
    currentStreak: Int,
    streaksToNextStage: Int,
    level: Int,
    vitalityPercent: Int,
    xpToNextLevel: Int,
    xpInLevel: Int,
    xpForCurrentLevel: Int,
    modifier: Modifier = Modifier
) {
    // Determine if health is impacting the growth stage
    val isHealthImpactingStage = growthStage != streakBasedGrowthStage

    // Determine health status for display
    val healthStatusColor = when {
        flowerHealth.isCritical -> BloomTheme.colors.error
        flowerHealth.isWilting -> BloomTheme.colors.secondary
        else -> BloomTheme.colors.success
    }

    BloomCard(
        modifier = modifier,
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

            // Show the potential stage if health is impacting it
            if (isHealthImpactingStage) {
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.flower_potential_stage),
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = streakBasedGrowthStage.getTitle(),
                        style = BloomTheme.typography.body,
                        color = healthStatusColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Health impact explanation
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when {
                        flowerHealth.isCritical -> stringResource(Res.string.flower_health_critical)
                        flowerHealth.isWilting -> stringResource(Res.string.flower_health_wilting)
                        else -> stringResource(Res.string.flower_health_impact)
                    },
                    style = BloomTheme.typography.small,
                    color = healthStatusColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Level and vitality
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.level_label, level),
                    style = BloomTheme.typography.subheading,
                    color = BloomTheme.colors.primary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(Res.string.vitality) + ": $vitalityPercent%",
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.textColor.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Level progress within current level
            if (xpForCurrentLevel > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Level progress",
                        style = BloomTheme.typography.body,
                        color = BloomTheme.colors.textColor.secondary
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    val percent =
                        (xpInLevel.toFloat() / xpForCurrentLevel.toFloat()).coerceIn(0f, 1f)
                    Text(
                        text = "${(percent * 100).toInt()}%",
                        style = BloomTheme.typography.subheading,
                        color = BloomTheme.colors.textColor.primary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                BloomLinearProgressIndicator(
                    percentage = (xpInLevel.toFloat() / xpForCurrentLevel.toFloat()).coerceIn(
                        0f,
                        1f
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = stringResource(Res.string.congratulations_full_bloom),
                    style = BloomTheme.typography.body,
                    color = BloomTheme.colors.success,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Streak UI removed in favor of level/XP model
        }
    }
} 