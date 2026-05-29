package com.horizondev.habitbloom.screens.garden.components.flowerdetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.screens.garden.domain.FlowerHealth

@Composable
internal fun flowerHealthStatusColor(healthValue: Float): Color = when {
    healthValue >= FlowerHealth.HEALTHY_THRESHOLD -> BloomTheme.colors.success
    healthValue >= FlowerHealth.WILTING_THRESHOLD -> BloomTheme.colors.warning
    else -> BloomTheme.colors.error
}
