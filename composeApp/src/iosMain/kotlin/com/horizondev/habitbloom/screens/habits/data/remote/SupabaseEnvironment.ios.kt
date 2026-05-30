package com.horizondev.habitbloom.screens.habits.data.remote

import platform.Foundation.NSBundle

actual object SupabaseEnvironment {
    actual val url: String
        get() = infoValue("SUPABASE_URL")

    actual val publishableKey: String
        get() = infoValue("SUPABASE_PUBLISHABLE_KEY")

    actual val authScheme: String
        get() = infoValue("SUPABASE_AUTH_SCHEME")

    actual val authHost: String
        get() = infoValue("SUPABASE_AUTH_HOST")

    private fun infoValue(key: String): String {
        return NSBundle.mainBundle.objectForInfoDictionaryKey(key) as? String ?: ""
    }
}
