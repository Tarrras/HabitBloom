package com.horizondev.habitbloom.app

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.horizondev.habitbloom.common.MainScreen
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import com.horizondev.habitbloom.platform.StatusBarColors
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    val screenModel: AppScreenModel = koinInject()

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