package com.horizondev.habitbloom.core.designComponents

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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