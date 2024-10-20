package com.horizondev.habitbloom.core.designComponents

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomLoader(modifier: Modifier = Modifier, isLoading: Boolean) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = modifier,
            color = BloomTheme.colors.primary,
            strokeWidth = 2.dp
        )
    }
}

@Composable
fun BloomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    gapSize: Dp = 4.dp,
    strokeWidth: Dp = 4.dp,
    progress: () -> Float
) {
    CircularProgressIndicator(
        progress = progress,
        modifier = modifier,
        color = BloomTheme.colors.primary,
        strokeWidth = strokeWidth,
        gapSize = gapSize
    )
}