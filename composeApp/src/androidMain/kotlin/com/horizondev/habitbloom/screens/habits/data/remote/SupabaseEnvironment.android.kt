package com.horizondev.habitbloom.screens.habits.data.remote

import com.horizondev.habitbloom.BuildConfig

actual object SupabaseEnvironment {
    actual val url: String = BuildConfig.SUPABASE_URL
    actual val publishableKey: String = BuildConfig.SUPABASE_PUBLISHABLE_KEY
    actual val authScheme: String = BuildConfig.SUPABASE_AUTH_SCHEME
    actual val authHost: String = BuildConfig.SUPABASE_AUTH_HOST
}
