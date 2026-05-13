package com.horizondev.habitbloom.core.designComponents.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BloomLoadingAnimation(
    modifier: Modifier = Modifier,
    animationName: String = "blooming_flower"
) {
    BloomLottieAnimation(
        assetName = animationName,
        modifier = modifier,
        isPlaying = true,
        loop = true
    )
}
