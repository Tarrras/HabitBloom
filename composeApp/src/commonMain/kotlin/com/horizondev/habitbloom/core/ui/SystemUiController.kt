package com.horizondev.habitbloom.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Interface for controlling system UI elements like status bar and navigation bar.
 */
interface SystemUiController {
    /**
     * Set the status bar color.
     *
     * @param color The color to set for the status bar
     * @param darkIcons Whether to use dark icons on the status bar
     */
    fun setStatusBarColor(color: Color, darkIcons: Boolean = false)

    /**
     * Set the navigation bar color.
     *
     * @param color The color to set for the navigation bar
     * @param darkIcons Whether to use dark icons on the navigation bar
     */
    fun setNavigationBarColor(color: Color, darkIcons: Boolean = false)
}

/**
 * Expect function to get the platform-specific SystemUiController implementation.
 */
@Composable
expect fun getSystemUiController(): SystemUiController

/**
 * Composable to update system UI colors based on the current theme.
 *
 * @param statusBarColor Color for the status bar
 * @param navigationBarColor Color for the navigation bar
 * @param isDarkTheme Whether dark theme is currently active
 */
@Composable
fun SystemUiColors(
    statusBarColor: Color,
    navigationBarColor: Color,
    isDarkTheme: Boolean
) {
    val systemUiController = getSystemUiController()

    // Use light status bar icons for dark background, dark icons for light background
    systemUiController.setStatusBarColor(
        color = statusBarColor,
        darkIcons = !isDarkTheme // Use dark icons when NOT in dark theme
    )

    systemUiController.setNavigationBarColor(
        color = navigationBarColor,
        darkIcons = !isDarkTheme // Use dark icons when NOT in dark theme
    )
} 