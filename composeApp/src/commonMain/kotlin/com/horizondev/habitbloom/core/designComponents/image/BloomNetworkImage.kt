package com.horizondev.habitbloom.core.designComponents.image

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.horizondev.habitbloom.core.designComponents.BloomCircularProgressIndicator
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun BloomNetworkImage(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shape: Shape = CircleShape,
    iconUrl: String,
    contentDescription: String?
) {
    KamelImage(
        resource = asyncPainterResource(data = iconUrl),
        modifier = modifier.size(size).clip(shape),
        contentDescription = contentDescription,
        contentScale = ContentScale.FillBounds,
        onLoading = { progress ->
            BloomCircularProgressIndicator(
                progress = {
                    progress
                }, modifier = Modifier
                    .matchParentSize()
                    .padding(4.dp)
            )
        },
        animationSpec = tween()
    )
}