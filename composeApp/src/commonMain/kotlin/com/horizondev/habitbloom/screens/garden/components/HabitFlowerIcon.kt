package com.horizondev.habitbloom.screens.garden.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.screens.garden.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth
import com.horizondev.habitbloom.screens.garden.domain.FlowerType
import com.horizondev.habitbloom.screens.garden.domain.iconWidth
import org.jetbrains.compose.resources.painterResource

@Composable
fun HabitFlowerIcon(
    modifier: Modifier = Modifier,
    flowerMaxStage: FlowerGrowthStage,
    flowerHealth: FlowerHealth,
    flowerType: FlowerType
) {
    // Align health with XP logic: do not regress stage; visuals only
    val displayedGrowthStage = remember(flowerMaxStage, flowerHealth) { flowerMaxStage }
    val flowerResource = flowerType.getFlowerResource(displayedGrowthStage)


    val flowerSize = displayedGrowthStage.iconWidth()

    // Calculate health-based visual effects
    val saturation = if (flowerHealth.isWilting) {
        // Desaturate based on health (0.7 to 1.0 based on health)
        0.7f + (flowerHealth.value * 0.3f)
    } else {
        1.0f // Full saturation when healthy
    }

    // Create color matrix for health-based visual effect
    val colorMatrix = remember(flowerHealth.value) {
        ColorMatrix().apply {
            setToSaturation(saturation)
        }
    }

    Box(modifier = modifier) {
        Image(
            painter = painterResource(flowerResource),
            contentDescription = null,
            modifier = Modifier
                .width(flowerSize)
                .alpha(if (flowerHealth.isWilting) 0.9f else 1f),
            contentScale = ContentScale.FillWidth,
            colorFilter = if (flowerHealth.isWilting) {
                ColorFilter.colorMatrix(colorMatrix)
            } else {
                null
            }
        )

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