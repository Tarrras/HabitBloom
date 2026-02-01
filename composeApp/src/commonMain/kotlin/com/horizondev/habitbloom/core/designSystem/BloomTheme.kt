package com.horizondev.habitbloom.core.designSystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.horizondev.habitbloom.common.settings.ThemeOption
import com.horizondev.habitbloom.core.theme.ThemeUseCase
import com.horizondev.habitbloom.core.ui.SystemUiColors
import org.koin.compose.koinInject

@Composable
fun BloomTheme(
    content: @Composable () -> Unit
) {
    val themeUseCase: ThemeUseCase = koinInject()
    val themeMode by themeUseCase.themeModeFlow.collectAsState(initial = ThemeOption.Device)

    val isDarkTheme = when (themeMode) {
        ThemeOption.Light -> false
        ThemeOption.Dark -> true
        ThemeOption.Device -> isSystemInDarkTheme()
    }

    val extendedColors = when (themeMode) {
        ThemeOption.Light -> lightColorScheme
        ThemeOption.Dark -> darkColorScheme
        ThemeOption.Device -> if (isDarkTheme) darkColorScheme else lightColorScheme
    }

    // Apply system UI colors
    SystemUiColors(
        statusBarColor = Color.Transparent,
        navigationBarColor = Color.Transparent,
        isDarkTheme = isDarkTheme
    )

    CompositionLocalProvider(
        LocalBloomColorScheme provides extendedColors,
        LocalBloomTypography provides bloomTypography()
    ) {
        MaterialTheme(
            content = content
        )
    }
}


@Composable
fun BloomThemePreview(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalBloomColorScheme provides lightColorScheme,
        LocalBloomTypography provides bloomTypography()
    ) {
        MaterialTheme {
            Scaffold(containerColor = BloomTheme.colors.background) { paddingValues -> content() }
        }
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