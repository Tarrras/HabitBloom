package com.horizondev.habitbloom.core.designSystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import org.koin.compose.koinInject

@Composable
fun BloomTheme(
    content: @Composable () -> Unit
) {
    val themeUseCase: ThemeUseCase = koinInject()
    val themeMode by themeUseCase.themeMode.collectAsState(initial = ThemeOption.Device)

    val extendedColors = when (themeMode) {
        ThemeOption.Light -> lightColorScheme
        ThemeOption.Dark -> darkColorScheme
        ThemeOption.Device -> if (isSystemInDarkTheme()) lightColorScheme else lightColorScheme
    }

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