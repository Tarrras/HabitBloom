package com.horizondev.habitbloom.screens.garden.components.flowerdetail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import kotlin.random.Random

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
    // Calculate the animated scale for a subtle breathing effect
    val infiniteTransition = rememberInfiniteTransition()
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    BloomCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = {}
    ) {
        // Add a subtle light gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            BloomTheme.colors.background,
                            BloomTheme.colors.background.copy(alpha = 0.7f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Background particle effects
            if (!flowerHealth.isCritical) {
                ParticleEffects(modifier = Modifier.fillMaxSize())
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // The flower based on growth stage and health status
                val displayedGrowthStage = determineDisplayGrowthStage(growthStage, flowerHealth)
                val flowerResource = flowerType.getFlowerResource(displayedGrowthStage)
                val flowerSize = when (displayedGrowthStage) {
                    FlowerGrowthStage.SEED -> 120.dp
                    FlowerGrowthStage.SPROUT -> 140.dp
                    FlowerGrowthStage.BUSH -> 160.dp
                    FlowerGrowthStage.BUD -> 180.dp
                    FlowerGrowthStage.BLOOM -> 200.dp
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

                Spacer(modifier = Modifier.height(8.dp))

                // Box for the flower visualization
                Box(
                    modifier = Modifier.height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // The flower image with effects (includes pot)
                    Image(
                        painter = painterResource(flowerResource),
                        contentDescription = "Flower at $displayedGrowthStage stage",
                        modifier = Modifier
                            .size(flowerSize)
                            .align(Alignment.Center)
                            .scale(if (flowerHealth.isWilting) 1f else breathingScale) // Subtle breathing animation when healthy
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
                        // Animated water drop that pulses
                        val pulseScale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.3f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                        
                        Icon(
                            painter = painterResource(Res.drawable.ic_solid_water_drop),
                            contentDescription = "Needs urgent watering",
                            tint = BloomTheme.colors.error,
                            modifier = Modifier
                                .size(28.dp)
                                .scale(pulseScale)
                                .align(Alignment.TopEnd)
                                .padding(top = 8.dp, end = 8.dp)
                                .alpha(0.9f)
                        )
                    }

                    // Add subtle shine effect on healthy flowers
                    if (flowerHealth.value > FlowerHealth.HEALTHY_THRESHOLD && displayedGrowthStage == FlowerGrowthStage.BLOOM) {
                        ShineEffect(
                            Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (flowerSize.value * 0.15f).dp)
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

/**
 * A shine effect for healthy blooming flowers.
 */
@Composable
private fun ShineEffect(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = modifier) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFFF176).copy(alpha = alpha),
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer {
                    rotationZ = rotationAnim
                }
        )
    }
}

/**
 * Animated background particles for a lively scene.
 */
@Composable
private fun ParticleEffects(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    val particles = remember {
        List(8) {
            ParticleState(
                x = Random.nextFloat() * 400f,
                y = Random.nextFloat() * 300f,
                alpha = 0.1f + (Random.nextFloat() * 0.1f),
                size = 4f + (Random.nextFloat() * 4f)
            )
        }
    }

    // Different animation offsets for particles
    val animations = particles.mapIndexed { index, _ ->
        val delay = (index * 300) % 2500
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, delayMillis = delay, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Canvas(modifier = modifier) {
        particles.forEachIndexed { index, particle ->
            val animationValue = animations[index].value
            val x = particle.x + (animationValue * 20f * if (index % 2 == 0) 1 else -1)
            val y = particle.y - (animationValue * 15f)

            drawCircle(
                color = Color(0xFFE3F2FD).copy(alpha = particle.alpha),
                radius = particle.size,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * Data class to track particle information.
 */
private data class ParticleState(
    val x: Float,
    val y: Float,
    val alpha: Float,
    val size: Float
)

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