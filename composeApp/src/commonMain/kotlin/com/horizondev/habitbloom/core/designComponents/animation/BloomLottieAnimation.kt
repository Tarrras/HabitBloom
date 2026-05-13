package com.horizondev.habitbloom.core.designComponents.animation

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import habitbloom.composeapp.generated.resources.Res
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

/**
 * Composable function to display a Lottie animation.
 *
 * @param assetName The animation file name in composeResources/files, with or without the .json extension.
 * @param modifier Modifier for the animation
 * @param isPlaying Whether the animation should be playing
 * @param loop Whether the animation should loop
 * @param speed Animation playback speed
 * @param initialProgress Initial progress of the animation (0f to 1f)
 * @param onAnimationEnd Called when animation completes (if not looping)
 */
@Composable
fun BloomLottieAnimation(
    assetName: String,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    loop: Boolean = true,
    speed: Float = 1f,
    initialProgress: Float = 0f,
    onAnimationEnd: () -> Unit = {}
) {
    val fileName = remember(assetName) {
        if (assetName.endsWith(".json")) assetName else "$assetName.json"
    }
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/$fileName").decodeToString()
        )
    }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = if (loop) Compottie.IterateForever else 1,
        speed = speed,
    )
    var isComplete by remember(composition) { mutableStateOf(false) }

    LaunchedEffect(composition, progress, loop) {
        if (composition != null && progress >= 1f && !isComplete && !loop) {
            isComplete = true
            onAnimationEnd()
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying && isComplete) {
            isComplete = false
        }
    }

    Image(
        painter = rememberLottiePainter(
            composition = composition,
            progress = { if (composition == null) initialProgress else progress },
        ),
        contentDescription = null,
        modifier = modifier,
        alignment = Alignment.Center,
        contentScale = ContentScale.Fit
    )
}

/**
 * Simplified utility composable for common use cases.
 * Displays a Lottie animation from assets with automatic playback.
 *
 * @param assetName The name of the animation file in the assets
 * @param modifier Modifier for the animation
 * @param loop Whether the animation should loop
 */
@Composable
fun BloomLottie(
    assetName: String,
    modifier: Modifier = Modifier,
    loop: Boolean = true
) {
    BloomLottieAnimation(
        assetName = assetName,
        modifier = modifier,
        isPlaying = true,
        loop = loop
    )
}
