package com.horizondev.habitbloom.screens.garden.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.domain.FlowerDisplayUtils
import com.horizondev.habitbloom.screens.garden.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth
import com.horizondev.habitbloom.screens.garden.domain.FlowerType
import com.horizondev.habitbloom.screens.garden.domain.HabitFlower
import com.horizondev.habitbloom.screens.habits.domain.models.TimeOfDay
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials
import org.jetbrains.compose.resources.DrawableResource

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
    hazeState: HazeState,
    modifier: Modifier = Modifier
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
            Box(contentAlignment = Alignment.Center) {
                GlowHaloSmall(intensity = habitFlower.health.value, modifier = Modifier)

                HabitFlowerIcon(
                    modifier = Modifier,
                    // Show current stage rather than historical max
                    flowerMaxStage = habitFlower.bloomingStage,
                    flowerHealth = habitFlower.health,
                    flowerType = FlowerType.fromTimeOfDay(habitFlower.timeOfDay)
                )

                LevelStarsOverlaySmall(
                    starCount = habitFlower.bloomingStage.ordinal.coerceIn(0, 4),
                    modifier = Modifier
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

            // Level indicator via stars is already shown around the flower
        }
    }
}

@Composable
private fun GlowHaloSmall(
    modifier: Modifier = Modifier,
    intensity: Float,
    firstColor: Color = BloomTheme.colors.primary.copy(alpha = 0.35f * intensity.coerceIn(0f, 1f)),
    secondColor: Color = BloomTheme.colors.primary.copy(alpha = 0.18f * intensity.coerceIn(0f, 1f))
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .alpha(0.6f)
    ) {
        val radius = size.minDimension / 3
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    firstColor,
                    secondColor,
                    Color.Transparent
                ),
                center = center,
                radius = radius
            ),
            radius = radius
        )
    }
}

@Composable
private fun LevelStarsOverlaySmall(starCount: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        val positions = listOf(
            Alignment.TopCenter,
            Alignment.CenterStart,
            Alignment.CenterEnd,
            Alignment.BottomCenter
        )
        repeat(starCount) { index ->
            val align = positions.getOrNull(index) ?: Alignment.TopCenter
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFFFD54F).copy(alpha = 0.9f),
                modifier = Modifier
                    .align(align)
                    .padding(4.dp)
            )
        }
    }
}

/**
 * Gets the appropriate flower resource based on growth stage and health.
 */
private fun getFlowerResource(
    bloomingStage: FlowerGrowthStage,
    health: FlowerHealth,
    timeOfDay: TimeOfDay
): DrawableResource {
    val displayedGrowthStage = FlowerDisplayUtils.determineDisplayGrowthStage(bloomingStage, health)
    return FlowerType.fromTimeOfDay(timeOfDay).getFlowerResource(displayedGrowthStage)
}