package com.horizondev.habitbloom.core.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable

/**
 * Desktop implementation of SystemUiController.
 * Desktop doesn't have status bar or navigation bar, so this is a no-op implementation.
 */
class DesktopSystemUiController : SystemUiController {
    override fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        // No operation for desktop
    }

    override fun setNavigationBarColor(color: Color, darkIcons: Boolean) {
        // No operation for desktop
    }
}

/**
 * Actual implementation for getting a SystemUiController.
 */
@Composable
actual fun getSystemUiController(): SystemUiController {
    return DesktopSystemUiController()
} 