package com.horizondev.habitbloom

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.horizondev.habitbloom.app.App
import com.horizondev.habitbloom.auth.domain.AuthDeepLinkRepository
import com.horizondev.habitbloom.auth.domain.isPasswordRecoveryDeepLink
import com.horizondev.habitbloom.platform.AndroidImagePicker
import dev.icerock.moko.permissions.PermissionsController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val imagePicker: AndroidImagePicker by inject()
    private val permissionsController: PermissionsController by inject()
    private val supabaseClient: SupabaseClient by inject()
    private val authDeepLinkRepository: AuthDeepLinkRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Napier.base(DebugAntilog())

        // Register image picker
        imagePicker.register(this)

        // Register permissions controller
        permissionsController.bind(this)

        handleAuthCallback(intent)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAuthCallback(intent)
    }

    private fun handleAuthCallback(intent: Intent) {
        intent.dataString
            ?.takeIf(::isPasswordRecoveryDeepLink)
            ?.let { authDeepLinkRepository.notifyPasswordRecoveryLinkOpened() }

        supabaseClient.handleDeeplinks(
            intent = intent,
            onError = { Napier.e("Failed to handle Supabase auth callback", it) }
        )
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
