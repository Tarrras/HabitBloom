package com.horizondev.habitbloom.app

import androidx.compose.runtime.Composable
import com.horizondev.habitbloom.common.MainScreen
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        BloomTheme {
            MainScreen()
        }
    }
}