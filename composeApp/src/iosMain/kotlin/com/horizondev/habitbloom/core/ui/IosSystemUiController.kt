package com.horizondev.habitbloom.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

/**
 * iOS implementation of SystemUiController.
 * Note: iOS has limited API for controlling system UI compared to Android.
 */
class IosSystemUiController : SystemUiController {
    override fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        // iOS doesn't allow setting the status bar background color directly
        // We can only set the status bar style (light or dark)
        val statusBarStyle = if (darkIcons) {
            UIStatusBarStyleDarkContent // Dark text for light backgrounds
        } else {
            UIStatusBarStyleLightContent // Light text for dark backgrounds
        }

        UIApplication.sharedApplication.setStatusBarStyle(statusBarStyle, true)
    }

    override fun setNavigationBarColor(color: Color, darkIcons: Boolean) {
        // iOS doesn't have a direct equivalent to Android's navigation bar
        // No action needed
    }
}

/**
 * Actual implementation for getting a SystemUiController.
 */
@Composable
actual fun getSystemUiController(): SystemUiController {
    return IosSystemUiController()
} 