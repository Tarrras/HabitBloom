package com.horizondev.habitbloom.core.designComponents.containers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme

@Composable
fun BloomSurface(
    modifier: Modifier = Modifier,
    color: Color = BloomTheme.colors.surface,
    shape: Shape = RoundedCornerShape(16.dp),
    contentColor: Color = contentColorFor(color),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = shape,
        shadowElevation = shadowElevation,
        content = content,
        border = border,
        tonalElevation = tonalElevation,
        contentColor = contentColor
    )
}