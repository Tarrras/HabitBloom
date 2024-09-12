package com.horizondev.habitbloom.designSystem

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
    primary = Color(0xFF4EC4B5),
    secondary = Color(0xFFF28C82),
    tertiary = Color(0xFFF4E4B2),
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    success = Color(0xFF7BC86C),
    error = Color(0xFFE57373),
    textColor = BloomTextColorScheme(
        primary = Color(0xFF333333),
        secondary = Color(0xFF333333),
        accent = Color(0xFFFF6F61),
        disabled = Color(0xFFBDC3C7),
        white = Color(0xFFFFFFFF)
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
        textColor = BloomTextColorScheme(
            primary = Color.Unspecified,
            secondary = Color.Unspecified,
            accent = Color.Unspecified,
            disabled = Color.Unspecified,
            white = Color.Unspecified
        )
    )
}