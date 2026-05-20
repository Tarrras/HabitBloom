package com.horizondev.habitbloom.screens.garden.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.horizondev.habitbloom.screens.garden.domain.FlowerType
import com.horizondev.habitbloom.screens.garden.domain.HabitFlower
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.HazeMaterials


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
                    flowerMaxStage = habitFlower.bloomingStage,
                    flowerHealth = habitFlower.health,
                    flowerType = FlowerType.fromTimeOfDay(habitFlower.timeOfDay)
                )
            }

            Text(
                text = habitFlower.name,
                style = BloomTheme.typography.subheading.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = BloomTheme.colors.textColor.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

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
