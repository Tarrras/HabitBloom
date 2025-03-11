package com.horizondev.habitbloom.screens.habits.data.remote

import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

/**
 * Configuration class for Supabase
 */
object SupabaseConfig {
    // IMPORTANT: Replace these with your actual Supabase URL and key
    private const val SUPABASE_URL = "https://zszfbydhmxoymohutwyh.supabase.co"
    private const val SUPABASE_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpzemZieWRobXhveW1vaHV0d3loIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDExMTQwNDksImV4cCI6MjA1NjY5MDA0OX0.jEl_tuU1DbzdXfbVF9y-FVn_0e_HzSSANdeWDj0YiXs"

    /**
     * Creates and configures a SupabaseClient
     */
    fun createClient(): SupabaseClient {
        Napier.d("Initializing Supabase client")
        return createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            // Install necessary plugins
            install(Storage)
        }
    }
} 