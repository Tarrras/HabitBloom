package com.horizondev.habitbloom.core.designSystem

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class BloomColorScheme(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val background: Color,
    val surface: Color,
    val success: Color,
    val error: Color,
    val disabled: Color,
    val textColor: BloomTextColorScheme
)

data class BloomTextColorScheme(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val disabled: Color,
    val white: Color,
)

val lightColorScheme = BloomColorScheme(
    primary = Color(0xFF2CB8A6), // Brighter teal
    secondary = Color(0xFFFE8A81), // Coral/Pink
    tertiary = Color(0xFFFDCB6E), // Bright golden yellow
    background = Color(0xFFF9F8F4), // Soft warm off-white
    surface = Color(0xFFFFFFFF), // White surface
    success = Color(0xFF64C764), // Vivid green for success
    error = Color(0xFFD32F2F), // Bold red for errors
    disabled = Color(0xFFB0BEC5), // Soft gray for disabled elements
    textColor = BloomTextColorScheme(
        primary = Color(0xFF333333), // Dark gray for primary text
        secondary = Color(0xFF9E9E9E), // Medium gray for secondary text
        accent = Color(0xFFFC5C65), // Bright pinkish-red for accent text
        disabled = Color(0xFFBDC3C7), // Light gray for disabled text
        white = Color(0xFFFFFFFF) // White for text on dark backgrounds
    )
)

val LocalBloomColorScheme = staticCompositionLocalOf {
    BloomColorScheme(
        primary = Color.Unspecified,
        secondary = Color.Unspecified,
        tertiary = Color.Unspecified,
        background = Color.Unspecified,
        surface = Color.Unspecified,
        success = Color.Unspecified,
        error = Color.Unspecified,
        disabled = Color.Unspecified,
        textColor = BloomTextColorScheme(
            primary = Color.Unspecified,
            secondary = Color.Unspecified,
            accent = Color.Unspecified,
            disabled = Color.Unspecified,
            white = Color.Unspecified
        )
    )
}