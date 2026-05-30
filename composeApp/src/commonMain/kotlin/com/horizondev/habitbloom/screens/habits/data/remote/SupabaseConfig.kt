package com.horizondev.habitbloom.screens.habits.data.remote

import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage


object SupabaseConfig {

    fun createClient(): SupabaseClient {
        Napier.d("Initializing Supabase client")
        return createSupabaseClient(
            supabaseUrl = requiredConfig("SUPABASE_URL", SupabaseEnvironment.url),
            supabaseKey = requiredConfig(
                name = "SUPABASE_PUBLISHABLE_KEY",
                value = SupabaseEnvironment.publishableKey
            )
        ) {
            install(Auth) {
                flowType = FlowType.PKCE
                scheme = requiredConfig("SUPABASE_AUTH_SCHEME", SupabaseEnvironment.authScheme)
                host = requiredConfig("SUPABASE_AUTH_HOST", SupabaseEnvironment.authHost)
            }
            install(Storage)
        }
    }

    private fun requiredConfig(name: String, value: String): String {
        return value.takeIf { it.isNotBlank() }
            ?: error("$name is not configured for this build.")
    }
}
