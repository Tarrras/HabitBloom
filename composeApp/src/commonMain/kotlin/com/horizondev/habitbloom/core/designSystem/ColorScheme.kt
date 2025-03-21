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
    val textColor: BloomTextColorScheme,
    val timeOfDay: TimeOfDayColorScheme
)

data class BloomTextColorScheme(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val disabled: Color,
    val white: Color,
)

data class TimeOfDayColorScheme(
    val morning: TimeOfDayColors,
    val afternoon: TimeOfDayColors,
    val evening: TimeOfDayColors
)

data class TimeOfDayColors(
    val chartBorder: Color,
    val chartBackground: Color,
    val gradientStart: Color,
    val gradientEnd: Color
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
    ),
    timeOfDay = TimeOfDayColorScheme(
        morning = TimeOfDayColors(
            chartBorder = Color(0xFFffc76e),
            chartBackground = Color(0xFFFFF3E0),
            gradientStart = Color(0xFFFFF8E1),
            gradientEnd = Color(0xFFF1F8E9)
        ),
        afternoon = TimeOfDayColors(
            chartBorder = Color(0xFF34d9ed),
            chartBackground = Color(0xFFE1F1F3),
            gradientStart = Color(0xFFE3F2FD),
            gradientEnd = Color(0xFFF1F8E9)
        ),
        evening = TimeOfDayColors(
            chartBorder = Color(0xFF9165f7),
            chartBackground = Color(0xFFEAE2FD),
            gradientStart = Color(0xFFF3E5F5),
            gradientEnd = Color(0xFFE1F5FE)
        )
    )
)

val darkColorScheme = BloomColorScheme(
    primary = Color(0xFF2CB8A6), // Keep the same primary for brand identity
    secondary = Color(0xFFFE8A81), // Keep the same secondary for brand identity
    tertiary = Color(0xFFFDCB6E), // Keep the same tertiary for brand identity
    background = Color(0xFF121212), // Dark background
    surface = Color(0xFF1E1E1E), // Slightly lighter surface for depth
    success = Color(0xFF4CAF50), // Slightly darker green for success
    error = Color(0xFFE53935), // Slightly adjusted error color
    disabled = Color(0xFF424242), // Darker gray for disabled elements
    textColor = BloomTextColorScheme(
        primary = Color(0xFFE1E1E1), // Light gray for primary text
        secondary = Color(0xFFB0B0B0), // Medium light gray for secondary text
        accent = Color(0xFFFF6B6B), // Brighter accent for dark theme
        disabled = Color(0xFF757575), // Medium gray for disabled text
        white = Color(0xFFFFFFFF) // Keep white
    ),
    timeOfDay = TimeOfDayColorScheme(
        morning = TimeOfDayColors(
            chartBorder = Color(0xFFFFB84D), // Slightly more saturated orange
            chartBackground = Color(0xFF332B1A), // Dark amber
            gradientStart = Color(0xFF262115),
            gradientEnd = Color(0xFF1E2415)
        ),
        afternoon = TimeOfDayColors(
            chartBorder = Color(0xFF20C7DB), // Slightly deeper cyan
            chartBackground = Color(0xFF1A2E33), // Dark blue-gray
            gradientStart = Color(0xFF15202B),
            gradientEnd = Color(0xFF1A2415)
        ),
        evening = TimeOfDayColors(
            chartBorder = Color(0xFF9F75FF), // Slightly brighter purple
            chartBackground = Color(0xFF2A2438), // Dark purple
            gradientStart = Color(0xFF221B2D),
            gradientEnd = Color(0xFF152632)
        )
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
        ),
        timeOfDay = TimeOfDayColorScheme(
            morning = TimeOfDayColors(
                chartBorder = Color.Unspecified,
                chartBackground = Color.Unspecified,
                gradientStart = Color.Unspecified,
                gradientEnd = Color.Unspecified
            ),
            afternoon = TimeOfDayColors(
                chartBorder = Color.Unspecified,
                chartBackground = Color.Unspecified,
                gradientStart = Color.Unspecified,
                gradientEnd = Color.Unspecified
            ),
            evening = TimeOfDayColors(
                chartBorder = Color.Unspecified,
                chartBackground = Color.Unspecified,
                gradientStart = Color.Unspecified,
                gradientEnd = Color.Unspecified
            )
        )
    )
}