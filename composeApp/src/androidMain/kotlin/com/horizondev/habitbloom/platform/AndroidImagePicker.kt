package com.horizondev.habitbloom.platform

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class AndroidImagePicker(
    private val context: Context
) : ImagePicker {
    private var imagePickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private val _imagePickerResult = MutableStateFlow<ImagePickerResult>(ImagePickerResult.None)
    override val imagePickerResult: StateFlow<ImagePickerResult> = _imagePickerResult.asStateFlow()

    fun register(activity: ComponentActivity) {
        imagePickerLauncher = activity.registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            uri?.let { handleImageResult(it) } ?: run {
                _imagePickerResult.value = ImagePickerResult.Error("No image selected")
            }
        }
    }

    override suspend fun pickImage() {
        imagePickerLauncher?.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        ) ?: run {
            _imagePickerResult.value = ImagePickerResult.Error("Image picker not initialized")
        }
    }

    private fun handleImageResult(uri: Uri) {
        try {
            _imagePickerResult.value = ImagePickerResult.Loading

            // Create a file in the app's cache directory
            val destinationFile = File(context.cacheDir, "habit_images/${UUID.randomUUID()}.jpg")
            destinationFile.parentFile?.mkdirs()

            // Copy the image from content URI to our app's cache
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }

            _imagePickerResult.value = ImagePickerResult.Success(destinationFile.absolutePath)
        } catch (e: Exception) {
            _imagePickerResult.value = ImagePickerResult.Error(e.message ?: "Unknown error")
        }
    }
} 