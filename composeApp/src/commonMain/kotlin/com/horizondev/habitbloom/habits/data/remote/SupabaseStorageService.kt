package com.horizondev.habitbloom.habits.data.remote

import com.horizondev.habitbloom.platform.platformReadFileAsBytes
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

/**
 * Service for uploading files to Supabase Storage.
 */
class SupabaseStorageService(
    private val supabaseClient: SupabaseClient
) {
    // Access the storage bucket
    private val storage = supabaseClient.storage
    private val bucketName = "habit_images"

    /**
     * Ensures that the required bucket exists in Supabase Storage.
     * This should be called during app initialization.
     */
    suspend fun initializeBucket() {
        try {
            // Check if bucket exists, create if not
            val buckets = storage.retrieveBuckets()
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

    /**
     * Uploads an image to Supabase Storage and returns the download URL.
     *
     * @param filePath The local file path of the image to upload
     * @param fileName The name to use for the file in storage (default: generates a random name)
     * @return Result containing the download URL on success, or an exception on failure
     */
    suspend fun uploadHabitImage(
        filePath: String,
        fileName: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            Napier.d("Uploading image from $filePath to Supabase")

            // Generate a unique filename if not provided
            val storageFileName =
                fileName ?: "habit_${Clock.System.now().toEpochMilliseconds()}.jpg"
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
