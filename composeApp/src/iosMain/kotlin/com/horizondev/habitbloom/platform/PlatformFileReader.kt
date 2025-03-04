package com.horizondev.habitbloom.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy

/**
 * iOS implementation of file reading
 */
@OptIn(ExperimentalForeignApi::class)
actual suspend fun platformReadFileAsBytes(filePath: String): ByteArray {
    // For iOS, we use NSFileManager and NSData to read files
    val fileManager = NSFileManager.defaultManager

    // Check if file exists
    if (!fileManager.fileExistsAtPath(filePath)) {
        throw IllegalArgumentException("File not found: $filePath")
    }

    // Read file data
    val nsData = NSData.dataWithContentsOfFile(filePath)
        ?: throw IllegalStateException("Failed to read file: $filePath")

    // Convert NSData to ByteArray
    val length = nsData.length.toInt()
    val result = ByteArray(length)

    result.usePinned { pinnedResult ->
        memcpy(pinnedResult.addressOf(0), nsData.bytes, length.toULong())
    }

    return result
} 