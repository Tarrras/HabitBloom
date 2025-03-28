package com.horizondev.habitbloom.screens.garden.components.flowerdetail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth
import com.horizondev.habitbloom.screens.garden.domain.FlowerType
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.ic_solid_water_drop
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

/**
 * Component to display a flower visualization at a specific growth stage.
 *
 * @param flowerType The type of flower to display
 * @param growthStage The current growth stage of the flower
 * @param showWateringAnimation Whether to show water drop animation
 * @param modifier Modifier for styling
 * @param flowerHealth The health status of the flower
 */
@Composable
fun FlowerVisualization(
    flowerType: FlowerType,
    growthStage: FlowerGrowthStage,
    showWateringAnimation: Boolean = false,
    modifier: Modifier = Modifier,
    flowerHealth: FlowerHealth = FlowerHealth()
) {
    BloomCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = {}
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // The flower based on growth stage
                val flowerResource = flowerType.getFlowerResource(growthStage)
                val flowerSize = when (growthStage) {
                    FlowerGrowthStage.SEED -> 60.dp
                    FlowerGrowthStage.SPROUT -> 80.dp
                    FlowerGrowthStage.BUSH -> 100.dp
                    FlowerGrowthStage.BUD -> 120.dp
                    FlowerGrowthStage.BLOOM -> 150.dp
                }

                // Calculate health-based visual effects
                val saturation = if (flowerHealth.isWilting) {
                    // Desaturate based on health (0.7 to 1.0 based on health)
                    0.7f + (flowerHealth.value * 0.3f)
                } else {
                    1.0f // Full saturation when healthy
                }

                // Create color matrix for health-based visual effect
                val colorMatrix = ColorMatrix().apply {
                    setToSaturation(saturation)
                }

                // Apply wilting animation if health is critical
                val rotationAngle = remember { Animatable(0f) }

                LaunchedEffect(flowerHealth.isCritical) {
                    if (flowerHealth.isCritical) {
                        rotationAngle.animateTo(
                            targetValue = 2f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000),
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                    } else {
                        // Reset rotation if not critical
                        rotationAngle.snapTo(0f)
                    }
                }

                Box(
                    modifier = Modifier.height(180.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // The flower with health effects
                    Image(
                        painter = painterResource(flowerResource),
                        contentDescription = "Flower at $growthStage stage",
                        modifier = Modifier
                            .size(flowerSize)
                            .graphicsLayer {
                                if (flowerHealth.isCritical) {
                                    rotationZ = rotationAngle.value
                                }
                            }
                            .alpha(if (flowerHealth.isWilting) 0.9f else 1f),
                        contentScale = ContentScale.Fit,
                        colorFilter = if (flowerHealth.isWilting) {
                            ColorFilter.colorMatrix(colorMatrix)
                        } else {
                            null
                        }
                    )

                    // Water drops animation
                    if (showWateringAnimation) {
                        WaterDropsAnimation(
                            Modifier.align(Alignment.TopCenter).padding(top = 48.dp)
                        )
                    }

                    // Critical health indicator
                    if (flowerHealth.isCritical && !showWateringAnimation) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_solid_water_drop),
                            contentDescription = "Needs urgent watering",
                            tint = BloomTheme.colors.error,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopEnd)
                                .padding(top = 8.dp, end = 8.dp)
                                .alpha(0.9f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Component for animated water drops.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
private fun WaterDropsAnimation(
    modifier: Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Animation for drop 1
    val drop1Alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val drop1Scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Animation for drop 2 (offset timing)
    val drop2Alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, delayMillis = 250, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val drop2Scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, delayMillis = 250, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Animation for drop 3 (offset timing)
    val drop3Alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, delayMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val drop3Scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, delayMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Drop 1
        Icon(
            painter = painterResource(Res.drawable.ic_solid_water_drop),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(start = 20.dp, top = 40.dp)
                .alpha(drop1Alpha)
                .scale(drop1Scale),
            tint = Color(0xFF1ca3ec)
        )

        // Drop 2
        Icon(
            painter = painterResource(Res.drawable.ic_solid_water_drop),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(end = 30.dp, top = 20.dp)
                .alpha(drop2Alpha)
                .scale(drop2Scale),
            tint = Color(0xFF1ca3ec)
        )

        // Drop 3
        Icon(
            painter = painterResource(Res.drawable.ic_solid_water_drop),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(start = 30.dp, bottom = 30.dp)
                .alpha(drop3Alpha)
                .scale(drop3Scale),
            tint = Color(0xFF1ca3ec)
        )
    }
} 