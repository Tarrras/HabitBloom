package com.horizondev.habitbloom

import androidx.compose.ui.window.ComposeUIViewController
import com.horizondev.habitbloom.app.App
import com.horizondev.habitbloom.di.KoinInit
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import dev.gitlive.firebase.initialize
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun MainViewController() = ComposeUIViewController { App() }

fun initialize() {
    Napier.base(DebugAntilog())
    Firebase.initialize()
    Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
    KoinInit().init()
}