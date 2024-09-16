package com.horizondev.habitbloom

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.horizondev.habitbloom.common.MainScreen
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        BloomTheme {
            Navigator(
                screen = MainScreen()
            )
        }
    }
}