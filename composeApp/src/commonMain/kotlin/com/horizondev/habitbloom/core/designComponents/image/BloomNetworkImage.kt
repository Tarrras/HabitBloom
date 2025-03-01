package com.horizondev.habitbloom.core.designComponents.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun BloomNetworkImage(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shape: Shape = CircleShape,
    iconUrl: String,
    contentDescription: String?
) {
    Box(modifier = modifier.size(size)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(iconUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = Modifier
                .matchParentSize()
                .clip(shape),
            contentScale = ContentScale.Crop
        )
    }
}