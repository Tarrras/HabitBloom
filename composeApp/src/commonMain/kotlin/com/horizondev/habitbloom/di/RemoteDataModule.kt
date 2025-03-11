package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.auth.data.AuthRemoteDataSource
import com.horizondev.habitbloom.screens.habits.data.remote.HabitsRemoteDataSource
import com.horizondev.habitbloom.screens.habits.data.remote.SupabaseConfig
import com.horizondev.habitbloom.screens.habits.data.remote.SupabaseStorageService
import com.horizondev.habitbloom.screens.profile.data.ProfileRemoteDataSource
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import io.github.jan.supabase.SupabaseClient
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val remoteDataModule = module {
    // Firebase components (still using Firebase for auth/firestore)
    single { Firebase.firestore } bind FirebaseFirestore::class
    single { Firebase.auth } bind FirebaseAuth::class

    // Supabase components
    single { SupabaseConfig.createClient() } bind SupabaseClient::class

    // Services
    singleOf(::SupabaseStorageService) // New Supabase Storage service

    // Data sources
    factoryOf(::HabitsRemoteDataSource)
    factoryOf(::AuthRemoteDataSource)
    factoryOf(::ProfileRemoteDataSource)
}