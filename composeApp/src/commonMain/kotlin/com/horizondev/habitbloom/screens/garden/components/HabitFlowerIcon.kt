package com.horizondev.habitbloom.screens.garden.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
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
    val displayedGrowthStage = remember(flowerMaxStage, flowerHealth) { flowerMaxStage }
    val flowerResource = flowerType.getFlowerResource(displayedGrowthStage)
    val flowerSize = displayedGrowthStage.iconWidth()

    val saturation = if (flowerHealth.isWilting) {
        0.7f + (flowerHealth.value * 0.3f)
    } else {
        1.0f
    }

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
    }
}

