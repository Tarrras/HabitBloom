package com.horizondev.habitbloom.core.designComponents.animation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * Android implementation of LottieAnimation using Airbnb's Lottie Compose library.
 */
actual class LottieAnimation {
    private var composition: LottieComposition? = null
    private var lottieDrawable: LottieDrawable? = null
    private var isPlaying = false
    private var progress = 0f
    private var loop = false
    private val context: Context? = null

    actual fun loadFromAsset(assetName: String) {
        // For Android, this is handled directly in the composable
    }

    actual fun loadFromUrl(url: String) {
        // For Android, this is handled directly in the composable
    }

    actual fun setProgress(progress: Float) {
        this.progress = progress
        lottieDrawable?.progress = progress
    }

    actual fun getProgress(): Float {
        return lottieDrawable?.progress ?: progress
    }

    actual fun setLoop(loop: Boolean) {
        this.loop = loop
        lottieDrawable?.repeatCount = if (loop) LottieDrawable.INFINITE else 0
    }

    actual fun play() {
        isPlaying = true
        lottieDrawable?.playAnimation()
    }

    actual fun pause() {
        isPlaying = false
        lottieDrawable?.pauseAnimation()
    }

    actual fun stop() {
        isPlaying = false
        lottieDrawable?.stop()
    }

    actual fun isPlaying(): Boolean {
        return lottieDrawable?.isAnimating ?: isPlaying
    }
}

/**
 * Composable function to display a Lottie animation from assets.
 */
@Composable
actual fun BloomLottieAnimation(
    assetName: String,
    modifier: Modifier,
    isPlaying: Boolean,
    loop: Boolean,
    speed: Float,
    initialProgress: Float,
    onAnimationEnd: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("$assetName.json")
    )

    val iterations = if (loop) LottieConstants.IterateForever else 1

    var isComplete by remember { mutableStateOf(false) }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = iterations,
        speed = speed,
    )

    // Handle animation completion
    LaunchedEffect(composition, progress) {
        if (composition != null && progress == 1f && !isComplete && !loop) {
            isComplete = true
            onAnimationEnd()
        }
    }

    // Reset completion state when the animation is replayed
    LaunchedEffect(composition, isPlaying) {
        if (isPlaying && isComplete) {
            isComplete = false
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}

/**
 * Composable function to display a Lottie animation from a URL.
 */
@Composable
actual fun BloomLottieAnimationFromUrl(
    url: String,
    modifier: Modifier,
    isPlaying: Boolean,
    loop: Boolean,
    speed: Float,
    initialProgress: Float,
    onAnimationEnd: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url(url)
    )

    val iterations = if (loop) LottieConstants.IterateForever else 1

    var isComplete by remember { mutableStateOf(false) }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = iterations,
        speed = speed,
    )

    // Handle animation completion
    LaunchedEffect(composition, progress) {
        if (composition != null && progress == 1f && !isComplete && !loop) {
            isComplete = true
            onAnimationEnd()
        }
    }

    // Reset completion state when the animation is replayed
    LaunchedEffect(composition, isPlaying) {
        if (isPlaying && isComplete) {
            isComplete = false
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
} 