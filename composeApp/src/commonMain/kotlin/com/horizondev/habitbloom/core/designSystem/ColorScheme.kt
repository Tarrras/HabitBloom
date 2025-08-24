package com.horizondev.habitbloom.core.designSystem

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class BloomColorScheme(
    // Core colors
    val tertiary: Color,
    val error: Color,
    val disabled: Color,

    // Text colors
    val textColor: BloomTextColorScheme,

    // Time of day colors
    val timeOfDay: TimeOfDayColorScheme,

    val background: Color,
    val foreground: Color,
    val surface: Color,
    val surfaceVariant: Color,

    val card: Color,
    val cardForeground: Color,
    val cardSecondary: Color,
    val cardMuted: Color,

    val primary: Color,
    val primaryForeground: Color,
    val primaryVariant: Color,

    val secondary: Color,
    val secondaryForeground: Color,

    val accent: Color,
    val accentForeground: Color,
    val accentTeal: Color,
    val accentTealLight: Color,

    val muted: Color,
    val mutedForeground: Color,
    val subtle: Color,
    val subtleForeground: Color,

    val success: Color,
    val successForeground: Color,
    val warning: Color,
    val warningForeground: Color,
    val destructive: Color,
    val destructiveForeground: Color,

    val border: Color,
    val input: Color,
    val inputBackground: Color,
    val ring: Color,

    val glassBackground: Color,
    val glassBackgroundStrong: Color,
    val glassBorder: Color,
    val glassShadow: Color
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

    background = Color(0xFFFFFFFF),
    foreground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF8F9FA),

    tertiary = Color(0xFFFDCB6E),
    error = Color(0xFFD32F2F),
    disabled = Color(0xFFB0BEC5),

    card = Color(0xFFFFFFFF),
    cardForeground = Color(0xFF1A1A1A),
    cardSecondary = Color(0xFFF8FAFC),
    cardMuted = Color(0xFFF1F5F9),

    primary = Color(0xFF0891B2),
    primaryForeground = Color(0xFFFFFFFF),
    primaryVariant = Color(0xFF06B6D4),

    secondary = Color(0xFFF1F5F9),
    secondaryForeground = Color(0xFF475569),

    accent = Color(0xFFF0F9FF),
    accentForeground = Color(0xFF0369A1),
    accentTeal = Color(0xFF0891B2),
    accentTealLight = Color(0xFF06B6D4),

    muted = Color(0xFFF8FAFC),
    mutedForeground = Color(0xFF64748B),
    subtle = Color(0xFFE2E8F0),
    subtleForeground = Color(0xFF475569),

    success = Color(0xFF10B981),
    successForeground = Color(0xFFFFFFFF),
    warning = Color(0xFFF59E0B),
    warningForeground = Color(0xFFFFFFFF),
    destructive = Color(0xFFEF4444),
    destructiveForeground = Color(0xFFFFFFFF),

    border = Color(0xFFE2E8F0),
    input = Color(0xFFF8FAFC),
    inputBackground = Color(0xFFFFFFFF),
    ring = Color(0xFF0891B2),

    glassBackground = Color(0xCCFFFFFF),
    glassBackgroundStrong = Color(0xF2FFFFFF),
    glassBorder = Color(0x4DCDD5E1),
    glassShadow = Color(0x1A000000),
    
    textColor = BloomTextColorScheme(
        primary = Color(0xFF252525), // --foreground
        secondary = Color(0xFF717182), // --muted-foreground
        accent = Color(0xFFD4183D), // --destructive as accent
        disabled = Color(0xFFBDC3C7), // Keep existing
        white = Color(0xFFFFFFFF)
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
    ),

)

val darkColorScheme = BloomColorScheme(
    background = Color(0xFF0F172A),
    foreground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFF334155),

    tertiary = Color(0xFFFDCB6E),
    error = Color(0xFFE53935),
    disabled = Color(0xFF424242),

    card = Color(0xFF1E293B),
    cardForeground = Color(0xFFF8FAFC),
    cardSecondary = Color(0xFF334155),
    cardMuted = Color(0xFF475569),

    primary = Color(0xFF06B6D4),
    primaryForeground = Color(0xFF0F172A),
    primaryVariant = Color(0xFF0891B2),

    secondary = Color(0xFF334155),
    secondaryForeground = Color(0xFFE2E8F0),

    accent = Color(0xFF1E293B),
    accentForeground = Color(0xFF06B6D4),
    accentTeal = Color(0xFF06B6D4),
    accentTealLight = Color(0xFF22D3EE),

    muted = Color(0xFF334155),
    mutedForeground = Color(0xFF94A3B8),
    subtle = Color(0xFF475569),
    subtleForeground = Color(0xFFCbd5E1),

    success = Color(0xFF22C55E),
    successForeground = Color(0xFF0F172A),
    warning = Color(0xFFEAB308),
    warningForeground = Color(0xFF0F172A),
    destructive = Color(0xFFF87171),
    destructiveForeground = Color(0xFF0F172A),

    border = Color(0xFF334155),
    input = Color(0xFF1E293B),
    inputBackground = Color(0xFF334155),
    ring = Color(0xFF06B6D4),

    glassBackground = Color(0x661E293B),
    glassBackgroundStrong = Color(0xCC1E293B),
    glassBorder = Color(0x1A94A3B8),
    glassShadow = Color(0x4D000000),
    
    textColor = BloomTextColorScheme(
        primary = Color(0xFFFCFCFC), // --foreground
        secondary = Color(0xFFB4B4B4), // --muted-foreground
        accent = Color(0xFF8B0000), // --destructive as accent
        disabled = Color(0xFF757575), // Keep existing
        white = Color(0xFFFFFFFF)
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

        // Extended semantic colors
        card = Color.Unspecified,
        cardForeground = Color.Unspecified,
        primaryForeground = Color.Unspecified,
        secondaryForeground = Color.Unspecified,
        muted = Color.Unspecified,
        mutedForeground = Color.Unspecified,
        accent = Color.Unspecified,
        accentForeground = Color.Unspecified,
        destructive = Color.Unspecified,
        destructiveForeground = Color.Unspecified,
        border = Color.Unspecified,
        input = Color.Unspecified,
        inputBackground = Color.Unspecified,
        ring = Color.Unspecified,

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
        ),
        foreground = Color.Unspecified,
        surfaceVariant = Color.Unspecified,
        cardSecondary = Color.Unspecified,
        cardMuted = Color.Unspecified,
        primaryVariant = Color.Unspecified,
        accentTeal = Color.Unspecified,
        accentTealLight = Color.Unspecified,
        subtle = Color.Unspecified,
        subtleForeground = Color.Unspecified,
        successForeground = Color.Unspecified,
        warning = Color.Unspecified,
        warningForeground = Color.Unspecified,
        glassBackground = Color.Unspecified,
        glassBackgroundStrong = Color.Unspecified,
        glassBorder = Color.Unspecified,
        glassShadow = Color.Unspecified
    )
}