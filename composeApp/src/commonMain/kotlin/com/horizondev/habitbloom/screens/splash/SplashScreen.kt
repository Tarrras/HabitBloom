package com.horizondev.habitbloom.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.animation.BloomLoadingAnimation
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize().background(color = BloomTheme.colors.background)) {
        BloomLoadingAnimation(modifier = Modifier.align(Alignment.Center).size(200.dp))
    }
}