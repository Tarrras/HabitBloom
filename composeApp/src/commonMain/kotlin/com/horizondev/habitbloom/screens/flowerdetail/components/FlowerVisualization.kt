package com.horizondev.habitbloom.screens.flowerdetail.components

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.containers.BloomCard
import com.horizondev.habitbloom.screens.flowerdetail.domain.FlowerGrowthStage
import com.horizondev.habitbloom.screens.flowerdetail.domain.FlowerType
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.ic_water_drop
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

/**
 * Component to display a flower visualization at a specific growth stage.
 *
 * @param flowerType The type of flower to display
 * @param growthStage The current growth stage of the flower
 * @param showWateringAnimation Whether to show water drop animation
 * @param modifier Modifier for styling
 */
@Composable
fun FlowerVisualization(
    flowerType: FlowerType,
    growthStage: FlowerGrowthStage,
    showWateringAnimation: Boolean = false,
    modifier: Modifier = Modifier
) {
    BloomCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
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

                Box(
                    modifier = Modifier.height(180.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // The flower
                    Image(
                        painter = painterResource(flowerResource),
                        contentDescription = "Flower at $growthStage stage",
                        modifier = Modifier.size(flowerSize),
                        contentScale = ContentScale.Fit
                    )

                    // Water drops animation
                    if (showWateringAnimation) {
                        WaterDropsAnimation()
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
private fun WaterDropsAnimation() {
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
        modifier = Modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        // Drop 1
        Image(
            painter = painterResource(Res.drawable.ic_water_drop),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(start = 20.dp, top = 40.dp)
                .alpha(drop1Alpha)
                .scale(drop1Scale),
            colorFilter = ColorFilter.tint(Color(0xFF4FC3F7))
        )

        // Drop 2
        Image(
            painter = painterResource(Res.drawable.ic_water_drop),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .padding(end = 30.dp, top = 20.dp)
                .alpha(drop2Alpha)
                .scale(drop2Scale),
            colorFilter = ColorFilter.tint(Color(0xFF29B6F6))
        )

        // Drop 3
        Image(
            painter = painterResource(Res.drawable.ic_water_drop),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(start = 30.dp, bottom = 30.dp)
                .alpha(drop3Alpha)
                .scale(drop3Scale),
            colorFilter = ColorFilter.tint(Color(0xFF03A9F4))
        )
    }
} 