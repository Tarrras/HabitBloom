package com.horizondev.habitbloom

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.horizondev.habitbloom.common.MainScreen
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.platform.StatusBarColors
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        BloomTheme {
            StatusBarColors(
                statusBarColor = BloomTheme.colors.background,
                navBarColor = BloomTheme.colors.background
            )
            Navigator(
                screen = MainScreen()
            )
        }
    }
}