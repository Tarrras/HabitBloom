package com.horizondev.habitbloom.platform

import kotlinx.coroutines.flow.StateFlow

interface ImagePicker {
    val imagePickerResult: StateFlow<ImagePickerResult>

    suspend fun pickImage()
}

sealed class ImagePickerResult {
    data object None : ImagePickerResult()
    data object Loading : ImagePickerResult()
    data class Success(val imageUrl: String) : ImagePickerResult()
    data class Error(val message: String) : ImagePickerResult()
} 