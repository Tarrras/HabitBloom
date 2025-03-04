package com.horizondev.habitbloom.platform

/**
 * Platform-specific implementation to read file bytes
 */
expect suspend fun platformReadFileAsBytes(filePath: String): ByteArray