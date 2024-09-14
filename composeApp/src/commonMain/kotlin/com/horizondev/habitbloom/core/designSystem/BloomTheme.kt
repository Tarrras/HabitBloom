package com.horizondev.habitbloom.core.designSystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun BloomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val extendedColors =
        if (darkTheme) lightColorScheme else lightColorScheme //todo add dark colors later

    CompositionLocalProvider(
        LocalBloomColorScheme provides extendedColors,
        LocalBloomTypography provides bloomTypography()
    ) {
        MaterialTheme(
            content = content
        )
    }
}

object BloomTheme {
    val colors: BloomColorScheme
        @Composable
        get() = LocalBloomColorScheme.current

    val typography: BloomTypography
        @Composable
        get() = LocalBloomTypography.current
}