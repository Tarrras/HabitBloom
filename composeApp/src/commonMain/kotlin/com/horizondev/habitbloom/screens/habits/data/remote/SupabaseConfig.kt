package com.horizondev.habitbloom.screens.habits.data.remote

import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

/**
 * Configuration class for Supabase
 */
object SupabaseConfig {
    private const val SUPABASE_URL = "https://atknoeuytcpacngmsgrg.supabase.co"
    private const val SUPABASE_PUBLISHABLE_KEY = "sb_publishable_ekEGbYzpUSyzBOjP6ty1Uw_xlt0j14F"

    /**
     * Creates and configures a SupabaseClient
     */
    fun createClient(): SupabaseClient {
        Napier.d("Initializing Supabase client")
        return createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_PUBLISHABLE_KEY
        ) {
            install(Auth) {
                flowType = FlowType.PKCE
                scheme = "com.horizondev.habitbloom"
                host = "auth-callback"
            }
            install(Storage)
        }
    }
}
