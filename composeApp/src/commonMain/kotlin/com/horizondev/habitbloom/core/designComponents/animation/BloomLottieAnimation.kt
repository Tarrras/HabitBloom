package com.horizondev.habitbloom.core.designComponents.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Expect class for handling Lottie animations across platforms.
 * This defines the contract that platform-specific implementations must fulfill.
 */
expect class LottieAnimation {
    /**
     * Loads a Lottie animation from the specified asset name.
     * @param assetName The name of the animation file in the assets folder (without extension)
     */
    fun loadFromAsset(assetName: String)

    /**
     * Loads a Lottie animation from a URL.
     * @param url The URL of the Lottie animation JSON
     */
    fun loadFromUrl(url: String)

    /**
     * Updates the progress of the animation.
     * @param progress Value between 0.0 and 1.0 representing the animation progress
     */
    fun setProgress(progress: Float)

    /**
     * Gets the current animation progress.
     * @return The current progress value between 0.0 and 1.0
     */
    fun getProgress(): Float

    /**
     * Sets whether the animation should loop.
     * @param loop True if the animation should loop, false otherwise
     */
    fun setLoop(loop: Boolean)

    /**
     * Plays the animation.
     */
    fun play()

    /**
     * Pauses the animation.
     */
    fun pause()

    /**
     * Stops the animation and resets to the beginning.
     */
    fun stop()

    /**
     * Checks if the animation is currently playing.
     * @return True if the animation is playing, false otherwise
     */
    fun isPlaying(): Boolean
}

/**
 * Composable function to display a Lottie animation.
 *
 * @param assetName The name of the animation file in the assets
 * @param modifier Modifier for the animation
 * @param isPlaying Whether the animation should be playing
 * @param loop Whether the animation should loop
 * @param speed Animation playback speed
 * @param initialProgress Initial progress of the animation (0f to 1f)
 * @param onAnimationEnd Called when animation completes (if not looping)
 */
@Composable
expect fun BloomLottieAnimation(
    assetName: String,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    loop: Boolean = true,
    speed: Float = 1f,
    initialProgress: Float = 0f,
    onAnimationEnd: () -> Unit = {}
)

/**
 * Composable function to display a Lottie animation from a URL.
 *
 * @param url The URL of the Lottie animation JSON
 * @param modifier Modifier for the animation
 * @param isPlaying Whether the animation should be playing
 * @param loop Whether the animation should loop
 * @param speed Animation playback speed
 * @param initialProgress Initial progress of the animation (0f to 1f)
 * @param onAnimationEnd Called when animation completes (if not looping)
 */
@Composable
expect fun BloomLottieAnimationFromUrl(
    url: String,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    loop: Boolean = true,
    speed: Float = 1f,
    initialProgress: Float = 0f,
    onAnimationEnd: () -> Unit = {}
)

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