package com.horizondev.habitbloom.core.designComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomLoader(modifier: Modifier = Modifier, isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Color.Gray.copy(alpha = 0.4f)
                )
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = BloomTheme.colors.primary,
                strokeWidth = 3.dp
            )
        }
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