package com.horizondev.habitbloom.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Android implementation of file reading
 */
actual suspend fun platformReadFileAsBytes(filePath: String): ByteArray =
    withContext(Dispatchers.IO) {
        // For Android, we can use Java's File class to read the bytes
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("File not found: $filePath")
        }
        file.readBytes()
    }