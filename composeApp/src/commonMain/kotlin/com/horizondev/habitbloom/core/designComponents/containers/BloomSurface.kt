package com.horizondev.habitbloom.core.designComponents.containers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.utils.clippedShadow

@Composable
fun BloomSurface(
    modifier: Modifier = Modifier,
    color: Color = BloomTheme.colors.glassBackgroundStrong,
    shape: Shape = RoundedCornerShape(16.dp),
    shadowElevation: Dp = 4.dp,
    border: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clippedShadow(
                elevation = shadowElevation,
                shape = shape
            )
            .clip(shape)
            .background(color)
            .let {
                border?.let { stroke ->
                    it.border(shape = shape, border = stroke)
                } ?: it
            }
            .let {
                onClick?.let { onClickLambda ->
                    it.clickable {
                        onClickLambda()
                    }
                } ?: it
            },
        content = content
    )
}