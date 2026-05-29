package com.horizondev.habitbloom.screens.garden.components.flowerdetail

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.components.HabitFlowerIcon
import com.horizondev.habitbloom.screens.garden.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth
import com.horizondev.habitbloom.screens.garden.domain.FlowerType
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.ic_solid_water_drop
import org.jetbrains.compose.resources.painterResource
import kotlin.random.Random


@Composable
fun FlowerVisualization(
    flowerType: FlowerType,
    growthStage: FlowerGrowthStage,
    flowerHealth: FlowerHealth,
    level: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val breathingTargetScale = 1f + (0.01f + 0.02f * flowerHealth.value.coerceIn(0f, 1f))
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = breathingTargetScale,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier.height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!flowerHealth.isCritical) {
            val density = 0.5f + (flowerHealth.value.coerceIn(0f, 1f) * 0.5f)
            ParticleEffects(modifier = Modifier.fillMaxSize(), density = density)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                GlowHalo(
                    intensity = flowerHealth.value.coerceIn(0f, 1f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(180.dp)
                        .alpha(0.7f)
                )

                // The flower image with effects (includes pot)
                HabitFlowerIcon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .scale(1.2f)
                        .scale(breathingScale)
                        .alpha(if (flowerHealth.isWilting) 0.9f else 1f),
                    flowerMaxStage = growthStage,
                    flowerHealth = flowerHealth,
                    flowerType = flowerType
                )

                if (flowerHealth.isCritical) {
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
                        contentDescription = null,
                        tint = BloomTheme.colors.error,
                        modifier = Modifier
                            .size(28.dp)
                            .scale(pulseScale)
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .alpha(0.9f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GlowHalo(intensity: Float, modifier: Modifier = Modifier) {
    val inner = BloomTheme.colors.primary.copy(alpha = 0.45f * intensity)
    val middle = BloomTheme.colors.primary.copy(alpha = 0.25f * intensity)
    val outer = Color.Transparent

    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(inner, middle, outer),
                center = center,
                radius = radius
            ),
            radius = radius
        )
    }
}


@Composable
private fun ParticleEffects(modifier: Modifier = Modifier, density: Float = 1f) {
    val infiniteTransition = rememberInfiniteTransition()

    val particles = remember(density) {
        val count = (12 * density.coerceIn(0.2f, 1.0f)).toInt().coerceAtLeast(3)
        List(count) {
            ParticleState(
                x = Random.nextFloat() * 400f,
                y = Random.nextFloat() * 300f,
                alpha = 0.4f + (Random.nextFloat() * 0.3f),
                size = 6f + (Random.nextFloat() * 6f),
                rotationAngle = Random.nextFloat() * 360f,
                colorIndex = Random.nextInt(3)  // 3 different green shades
            )
        }
    }

    val animations = particles.mapIndexed { index, _ ->
        val delay = (index * 300) % 2500
        val duration = 3000 + (index % 5) * 500 // Varied durations

        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, delayMillis = delay, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val rotations = particles.mapIndexed { index, _ ->
        val delay = (index * 200) % 2000
        val duration = 4000 + (index % 3) * 1000 // Varied durations

        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(duration, delayMillis = delay, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    val leafColors = listOf(
        Color(0xFF8BC34A), // Light green
        Color(0xFF689F38), // Medium green
        Color(0xFFA5D6A7)  // Pale green
    )

    Box(modifier = modifier) {
        particles.forEachIndexed { index, particle ->
            val animationValue = animations[index].value
            val rotationValue = rotations[index].value

            val x = particle.x + (animationValue * 30f * if (index % 2 == 0) 1 else -1)
            val y = particle.y - (animationValue * 20f)

            // Draw a leaf
            LeafParticle(
                color = leafColors[particle.colorIndex],
                alpha = particle.alpha,
                size = (particle.size * 1.5f).dp,
                angle = rotationValue + particle.rotationAngle,
                modifier = Modifier.offset(
                    x = x.dp,
                    y = y.dp
                )
            )
        }
    }
}

/**
 * A simple leaf-shaped particle.
 */
@Composable
private fun LeafParticle(
    color: Color,
    alpha: Float,
    size: Dp,
    angle: Float,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationZ = angle
            }
    ) {
        // Draw leaf shape
        val leafPath = Path().apply {
            // Start from the bottom center
            moveTo(size.toPx() / 2, size.toPx())

            // Draw curve to the right and back
            cubicTo(
                size.toPx() * 0.8f, size.toPx() * 0.7f,  // control point 1
                size.toPx(), size.toPx() * 0.3f,        // control point 2
                size.toPx() / 2, 0f                     // end point
            )

            // Draw curve to the left and back to the bottom
            cubicTo(
                0f, size.toPx() * 0.3f,                // control point 1
                size.toPx() * 0.2f, size.toPx() * 0.7f, // control point 2
                size.toPx() / 2, size.toPx()           // end point
            )

            close()
        }

        // Draw the leaf with a stem
        drawPath(
            path = leafPath,
            color = color.copy(alpha = alpha),
        )

        // Draw a small stem
        drawLine(
            color = Color(0xFF795548).copy(alpha = alpha),
            start = Offset(size.toPx() / 2, size.toPx()),
            end = Offset(size.toPx() / 2, size.toPx() * 1.2f),
            strokeWidth = size.toPx() * 0.1f
        )
    }
}

/**
 * Data class to track particle information.
 */
private data class ParticleState(
    val x: Float,
    val y: Float,
    val alpha: Float,
    val size: Float,
    val rotationAngle: Float = 0f,
    val colorIndex: Int = 0
) 
