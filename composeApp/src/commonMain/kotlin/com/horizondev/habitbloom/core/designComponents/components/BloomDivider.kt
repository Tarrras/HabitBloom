package com.horizondev.habitbloom.core.designComponents.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomHorizontalDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(modifier = modifier, color = BloomTheme.colors.border)
}