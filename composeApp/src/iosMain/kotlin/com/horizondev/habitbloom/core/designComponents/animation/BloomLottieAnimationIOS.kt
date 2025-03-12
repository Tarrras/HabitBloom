package com.horizondev.habitbloom.core.designComponents.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cocoapods.lottie_ios.LottieAnimationView
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * iOS implementation of LottieAnimation using the official lottie-ios library.
 */
actual class LottieAnimation {
    @OptIn(ExperimentalForeignApi::class)
    private var animationView: LottieAnimationView? = null
    private var isPlaying = false
    private var progress = 0f
    private var loop = false

    @OptIn(ExperimentalForeignApi::class)
    actual fun loadFromAsset(assetName: String) {
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun loadFromUrl(url: String) {
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun setProgress(progress: Float) {
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun getProgress(): Float {
        return 0f
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun setLoop(loop: Boolean) {
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun play() {
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun pause() {
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun stop() {
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun isPlaying(): Boolean {
        return isPlaying
    }
}

/**
 * Composable function to display a Lottie animation from assets.
 */
@OptIn(ExperimentalForeignApi::class)
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
    /*Box(modifier = modifier) {
        UIKitView(
            factory = {
                // Create animation view using the convenience initializer
                val animationView = LottieAnimationView(name = assetName)
                
                // Configure properties
                animationView.contentMode = UIViewContentModeScaleAspectFit
                animationView.loopMode = if (loop) LottieLoopMode.loop else LottieLoopMode.playOnce
                animationView.animationSpeed = speed.toDouble()
                
                // Calculate initial frame if needed
                if (initialProgress > 0f) {
                    val totalFrames = animationView.animation?.endFrame ?: 0.0
                    animationView.currentFrame = totalFrames * initialProgress.toDouble()
                }
                
                // Start playing if needed
                if (isPlaying) {
                    animationView.play { completed ->
                        if (completed && !loop) {
                            onAnimationEnd()
                        }
                    }
                }
                
                animationView
            },
            modifier = Modifier.matchParentSize(),
            update = { view ->
                // Update loop mode if changed
                view.loopMode = if (loop) LottieLoopMode.loop else LottieLoopMode.playOnce
                view.animationSpeed = speed.toDouble()
                
                // Update playback state if needed
                if (isPlaying && !view.isAnimationPlaying) {
                    view.play { completed ->
                        if (completed && !loop) {
                            onAnimationEnd()
                        }
                    }
                } else if (!isPlaying && view.isAnimationPlaying) {
                    view.pause()
                }
            }
        )
    }*/
}

/**
 * Composable function to display a Lottie animation from a URL.
 */
@OptIn(ExperimentalForeignApi::class)
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
    /*Box(modifier = modifier) {
        val nsUrl = remember { NSURL.URLWithString(url) }
        
        if (nsUrl != null) {
            UIKitView(
                factory = {
                    // Create animation view
                    val animationView = LottieAnimationView()
                    
                    // Configure basic properties
                    animationView.contentMode = UIViewContentModeScaleAspectFit
                    animationView.loopMode = if (loop) LottieLoopMode.loop else LottieLoopMode.playOnce
                    animationView.animationSpeed = speed.toDouble()
                    
                    // Load animation asynchronously
                    cocoapods.Lottie.LottieAnimation.loadedFrom(url = nsUrl) { animation ->
                        // Set animation
                        animationView.animation = animation
                        
                        // Set initial progress if needed
                        if (initialProgress > 0f) {
                            val totalFrames = animation?.endFrame ?: 0.0
                            animationView.currentFrame = totalFrames * initialProgress.toDouble()
                        }
                        
                        // Start playing if requested
                        if (isPlaying) {
                            animationView.play { completed ->
                                if (completed && !loop) {
                                    onAnimationEnd()
                                }
                            }
                        }
                    }
                    
                    animationView
                },
                modifier = Modifier.matchParentSize(),
                update = { view ->
                    // Update properties if they change
                    view.loopMode = if (loop) LottieLoopMode.loop else LottieLoopMode.playOnce
                    view.animationSpeed = speed.toDouble()
                    
                    // Update playback state if needed
                    if (view.animation != null) {
                        if (isPlaying && !view.isAnimationPlaying) {
                            view.play { completed ->
                                if (completed && !loop) {
                                    onAnimationEnd()
                                }
                            }
                        } else if (!isPlaying && view.isAnimationPlaying) {
                            view.pause()
                        }
                    }
                }
            )
        }
    }*/
} 