package com.horizondev.habitbloom.core.designComponents.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import habitbloom.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BloomLoadingAnimation(
    modifier: Modifier = Modifier,
    fileName: String = "drawable/ic_bloom.gif"
) {
    AsyncImage(
        modifier = modifier,
        model = Res.getUri(fileName),
        contentDescription = null
    )
}