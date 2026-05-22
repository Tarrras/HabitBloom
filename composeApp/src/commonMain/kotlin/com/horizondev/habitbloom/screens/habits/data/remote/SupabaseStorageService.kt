package com.horizondev.habitbloom.screens.habits.data.remote

import com.horizondev.habitbloom.platform.platformReadFileAsBytes
import com.horizondev.habitbloom.utils.getCurrentDateTime
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Service for uploading files to Supabase Storage.
 */
class SupabaseStorageService(
    private val supabaseClient: SupabaseClient
) {
    private val storage = supabaseClient.storage
    private val bucketName = "habit_images"

    suspend fun initializeBucket() {
        try {
            val buckets = storage.listBuckets()
            if (buckets.none { it.name == bucketName }) {
                storage.createBucket(bucketName) {
                    public = true
                }
                Napier.d("Created Supabase bucket: $bucketName")
            }
        } catch (e: Exception) {
            Napier.e("Failed to initialize Supabase bucket", e)
        }
    }

    suspend fun uploadHabitImage(
        filePath: String,
        fileName: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            Napier.d("Uploading image from $filePath to Supabase")

            // Generate a unique filename if not provided
            val storageFileName =
                fileName ?: "habit_${getCurrentDateTime().time.toMillisecondOfDay()}.jpg"
            val storagePath = "public/$storageFileName"

            // Read file as bytes
            val fileBytes = readFileAsBytes(filePath)

            // Upload to Supabase
            storage.from(bucketName).upload(storagePath, fileBytes) {
                upsert = true  // Overwrite if exists
            }

            // Get the public URL
            val publicUrl = storage.from(bucketName).publicUrl(storagePath)
            Napier.d("Image uploaded successfully to Supabase. URL: $publicUrl")

            Result.success(publicUrl)
        } catch (e: Exception) {
            Napier.e("Failed to upload image to Supabase", e)
            Result.failure(e)
        }
    }

    /**
     * Helper function to read file as bytes - platform-specific implementation needed
     */
    private suspend fun readFileAsBytes(filePath: String): ByteArray {
        // This will be handled by platform-specific code
        return platformReadFileAsBytes(filePath)
    }
}
