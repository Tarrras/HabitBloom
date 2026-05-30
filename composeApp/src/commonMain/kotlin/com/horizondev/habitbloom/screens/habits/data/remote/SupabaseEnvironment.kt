package com.horizondev.habitbloom.screens.habits.data.remote

expect object SupabaseEnvironment {
    val url: String
    val publishableKey: String
    val authScheme: String
    val authHost: String
}
