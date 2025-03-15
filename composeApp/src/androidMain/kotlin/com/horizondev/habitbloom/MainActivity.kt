package com.horizondev.habitbloom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.horizondev.habitbloom.app.App
import com.horizondev.habitbloom.platform.AndroidImagePicker
import dev.icerock.moko.permissions.PermissionsController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val imagePicker: AndroidImagePicker by inject()
    private val permissionsController: PermissionsController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Napier.base(DebugAntilog())

        // Register image picker
        imagePicker.register(this)

        // Register permissions controller
        permissionsController.bind(this)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}