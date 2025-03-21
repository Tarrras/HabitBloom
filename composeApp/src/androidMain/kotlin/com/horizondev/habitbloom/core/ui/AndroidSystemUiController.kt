package com.horizondev.habitbloom.core.ui

import android.app.Activity
import android.os.Build
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Android implementation of SystemUiController.
 */
class AndroidSystemUiController(private val window: Window) : SystemUiController {
    override fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        window.statusBarColor = color.toArgb()

        // Update status bar icon colors
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = darkIcons
        }
    }

    override fun setNavigationBarColor(color: Color, darkIcons: Boolean) {
        window.navigationBarColor = color.toArgb()

        // Update navigation bar icons (only for API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightNavigationBars = darkIcons
            }
        }
    }
}

/**
 * Actual implementation for getting a SystemUiController.
 */
@Composable
actual fun getSystemUiController(): SystemUiController {
    val view = LocalView.current
    val window = (view.context as Activity).window
    return AndroidSystemUiController(window)
} 