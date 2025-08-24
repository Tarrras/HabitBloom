package com.horizondev.habitbloom.core.designSystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Examples of how to use the enhanced BloomTheme colors in your components.
 *
 * The color scheme now includes all colors from your CSS theme with proper
 * light and dark theme support.
 */

// MARK: - Core Colors Usage
@Composable
fun CoreColorsExample() {
    Column {
        // Primary colors
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BloomTheme.colors.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Primary Background",
                color = BloomTheme.colors.primaryForeground
            )
        }

        // Card colors
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BloomTheme.colors.card,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = BloomTheme.colors.border,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Card with Border",
                color = BloomTheme.colors.cardForeground
            )
        }

        // Muted colors
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BloomTheme.colors.muted,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Muted Background",
                color = BloomTheme.colors.mutedForeground
            )
        }

        // Accent colors
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BloomTheme.colors.accent,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Accent Background",
                color = BloomTheme.colors.accentForeground
            )
        }

        // Brand colors
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BloomTheme.colors.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Brand Primary (Teal)",
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BloomTheme.colors.secondary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Brand Secondary (Cyan)",
                color = Color.White
            )
        }
    }
}


// MARK: - State Colors Usage
@Composable
fun StateColorsExample() {
    Column {
        // Destructive/Error state
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BloomTheme.colors.destructive,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Error/Destructive State",
                color = BloomTheme.colors.destructiveForeground
            )
        }

        // Success state
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BloomTheme.colors.success,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Success State",
                color = Color.White
            )
        }

        // Disabled state
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BloomTheme.colors.disabled,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Disabled State",
                color = BloomTheme.colors.textColor.disabled
            )
        }
    }
}

// MARK: - Input Colors Usage  
@Composable
fun InputColorsExample() {
    Column {
        // Input field example
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BloomTheme.colors.inputBackground,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = BloomTheme.colors.border,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Input Field",
                color = BloomTheme.colors.textColor.primary
            )
        }

        // Focused input with ring
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = BloomTheme.colors.inputBackground,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 2.dp,
                    color = BloomTheme.colors.ring,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "Focused Input Field",
                color = BloomTheme.colors.textColor.primary
            )
        }
    }
}

/**
 * HOW TO USE THESE COLORS:
 *
 * 1. **Semantic Usage** - Use colors by their semantic meaning:
 *    - BloomTheme.colors.destructive for error states
 *    - BloomTheme.colors.success for success states
 *    - BloomTheme.colors.muted for subtle backgrounds
 *
 * 2. **Foreground Pairing** - Always pair backgrounds with appropriate foregrounds:
 *    - BloomTheme.colors.card + BloomTheme.colors.cardForeground
 *    - BloomTheme.colors.primary + BloomTheme.colors.primaryForeground
 *
 * 3. **Chart Colors** - Use for data visualization:
 *    - BloomTheme.colors.chart.chart1 through chart5
 *    - All colors are designed to be distinct and accessible
 *
 * 4. **Input Fields** - Use inputBackground and border for form elements:
 *    - BloomTheme.colors.inputBackground for input backgrounds
 *    - BloomTheme.colors.border for subtle borders
 *    - BloomTheme.colors.ring for focus states
 *
 * 5. **Time of Day** - Use for habit-specific theming:
 *    - BloomTheme.colors.timeOfDay.morning/afternoon/evening
 *    - Each has chartBackground, chartBorder, gradients
 *
 * All colors automatically adapt to light/dark theme!
 *
 * BRAND COLORS USAGE:
 *
 * The brandPrimary (teal-500) and brandSecondary (cyan-500) colors are your
 * signature brand colors that create beautiful gradients and accents:
 *
 * // Gradient backgrounds (like date selector)
 * val brandGradient = Brush.linearGradient(
 *     colors = listOf(
 *         BloomTheme.colors.brandPrimary,   // teal-500
 *         BloomTheme.colors.brandSecondary  // cyan-500
 *     )
 * )
 *
 * // Single brand color usage
 * Button(
 *     colors = ButtonDefaults.buttonColors(
 *         containerColor = BloomTheme.colors.brandPrimary
 *     )
 * )
 *
 * // Progress indicators
 * CircularProgressIndicator(
 *     color = BloomTheme.colors.brandSecondary
 * )
 *
 * Brand colors work perfectly in both light and dark themes!
 */
