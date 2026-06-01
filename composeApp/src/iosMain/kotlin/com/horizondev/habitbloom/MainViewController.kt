package com.horizondev.habitbloom

import androidx.compose.ui.window.ComposeUIViewController
import com.horizondev.habitbloom.app.App
import com.horizondev.habitbloom.auth.domain.AuthDeepLinkRepository
import com.horizondev.habitbloom.auth.domain.isPasswordRecoveryDeepLink
import com.horizondev.habitbloom.di.KoinInit
import com.horizondev.habitbloom.platform.IOSImagePicker
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import dev.gitlive.firebase.initialize
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.Foundation.NSURL
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val controller = ComposeUIViewController { App() }
    attachImagePicker(controller)
    return controller
}

private fun attachImagePicker(controller: UIViewController) {
    object : KoinComponent {
        private val imagePicker: IOSImagePicker by inject()

        init {
            imagePicker.attach(controller)
        }
    }
}

fun initialize() {
    Napier.base(DebugAntilog())
    Firebase.initialize()
    Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
    KoinInit().init()
}

fun handleSupabaseAuthCallback(url: String) {
    val callbackUrl = NSURL(string = url)
    object : KoinComponent {
        private val supabaseClient: SupabaseClient by inject()
        private val authDeepLinkRepository: AuthDeepLinkRepository by inject()

        init {
            if (isPasswordRecoveryDeepLink(url)) {
                authDeepLinkRepository.notifyPasswordRecoveryLinkOpened()
            }

            supabaseClient.handleDeeplinks(
                url = callbackUrl,
                onError = { Napier.e("Failed to handle Supabase auth callback", it) }
            )
        }
    }
}
