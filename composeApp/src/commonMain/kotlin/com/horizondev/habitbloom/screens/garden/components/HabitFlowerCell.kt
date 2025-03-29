package com.horizondev.habitbloom.screens.garden.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth
import com.horizondev.habitbloom.screens.garden.domain.FlowerType
import com.horizondev.habitbloom.screens.garden.domain.HabitFlower
import com.horizondev.habitbloom.screens.garden.domain.iconWidth
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.current_streak
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * A UI component that displays a habit as a flower in the garden.
 *
 * @param habitFlower The habit flower data to display
 * @param onClick Callback invoked when the cell is clicked
 */
@Composable
fun HabitFlowerCell(
    habitFlower: HabitFlower,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .hazeEffect(
                state = hazeState, style = HazeMaterials.regular(
                    containerColor = BloomTheme.colors.surface
                )
            )
            .clickable {
                onClick()
            }
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Calculate the flower image resource based on the blooming stage and health
            val flowerResource = remember(habitFlower.maxStage, habitFlower.health) {
                getFlowerResource(
                    bloomingStage = habitFlower.maxStage,
                    health = habitFlower.health,
                    timeOfDay = habitFlower.timeOfDay
                )
            }

            Box(
                contentAlignment = Alignment.Center
            ) {
                // Show a larger image for later stages
                val displayedGrowthStage = determineDisplayGrowthStage(
                    habitFlower.maxStage,
                    habitFlower.health
                )
                val flowerSize = displayedGrowthStage.iconWidth()

                // Calculate health-based visual effects
                val saturation = if (habitFlower.health.isWilting) {
                    // Desaturate based on health (0.7 to 1.0 based on health)
                    0.7f + (habitFlower.health.value * 0.3f)
                } else {
                    1.0f // Full saturation when healthy
                }

                // Create color matrix for health-based visual effect
                val colorMatrix = remember(habitFlower.health.value) {
                    ColorMatrix().apply {
                        setToSaturation(saturation)
                    }
                }

                Image(
                    painter = painterResource(flowerResource),
                    contentDescription = habitFlower.name,
                    modifier = Modifier
                        .width(flowerSize)
                        .alpha(if (habitFlower.health.isWilting) 0.9f else 1f),
                    contentScale = ContentScale.FillWidth,
                    colorFilter = if (habitFlower.health.isWilting) {
                        ColorFilter.colorMatrix(colorMatrix)
                    } else {
                        null
                    }
                )
            }

            // Habit name
            Text(
                text = habitFlower.name,
                style = BloomTheme.typography.subheading.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = BloomTheme.colors.textColor.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Streak counter 
            Text(
                text = buildAnnotatedString {
                    append(
                        stringResource(Res.string.current_streak)
                    )
                    append(":")
                    append(" ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(habitFlower.streak.toString())
                    }
                },
                style = BloomTheme.typography.small,
                color = BloomTheme.colors.textColor.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Determines the growth stage to display based on the actual growth stage and flower health.
 * When health is critical, the flower will be displayed at a lower growth stage.
 * When health is wilting but not critical, visual effects will be applied but stage remains.
 *
 * @param actualGrowthStage The current or maximum growth stage based on streak
 * @param flowerHealth The health status of the flower
 * @return The growth stage to display visually
 */
private fun determineDisplayGrowthStage(
    actualGrowthStage: FlowerGrowthStage,
    flowerHealth: FlowerHealth
): FlowerGrowthStage {
    // When health is critical (below 0.3), reduce the stage by 1
    if (flowerHealth.isCritical) {
        val currentIndex = actualGrowthStage.ordinal
        // Ensure we don't go below SEED stage
        return if (currentIndex > 0) {
            FlowerGrowthStage.entries[currentIndex - 1]
        } else {
            FlowerGrowthStage.SEED
        }
    }

    // If health is wilting (below 0.7) but not critical, keep the same stage
    // Visual effects (desaturation and alpha) will be applied by other code
    return actualGrowthStage
}

/**
 * Gets the appropriate flower resource based on growth stage and health.
 */
private fun getFlowerResource(
    bloomingStage: FlowerGrowthStage,
    health: FlowerHealth,
    timeOfDay: TimeOfDay
): DrawableResource {
    val displayedGrowthStage = determineDisplayGrowthStage(bloomingStage, health)
    return FlowerType.fromTimeOfDay(timeOfDay).getFlowerResource(displayedGrowthStage)
}