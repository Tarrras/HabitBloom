package com.horizondev.habitbloom.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.horizondev.habitbloom.common.MainScreen
import com.horizondev.habitbloom.core.designSystem.BloomTheme
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinContext {
        val viewModel = koinViewModel<AppViewModel>()

        LaunchedEffect(Unit) {
            viewModel.initUser()
        }

        BloomTheme {
            MainScreen()
        }
    }
}