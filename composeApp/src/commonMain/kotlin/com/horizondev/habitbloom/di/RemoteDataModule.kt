package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.auth.data.SupabaseAuthGateway
import com.horizondev.habitbloom.auth.domain.AuthGateway
import com.horizondev.habitbloom.screens.habits.data.remote.HabitsRemoteDataSource
import com.horizondev.habitbloom.screens.habits.data.remote.SupabaseConfig
import com.horizondev.habitbloom.screens.habits.data.remote.SupabaseStorageService
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import io.github.jan.supabase.SupabaseClient
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val remoteDataModule = module {
    // Firebase components (still using Firebase for Firestore-backed catalog data)
    single { Firebase.firestore } bind FirebaseFirestore::class

    // Supabase components
    single { SupabaseConfig.createClient() } bind SupabaseClient::class
    single<AuthGateway> { SupabaseAuthGateway(supabaseClient = get()) }

    // Services
    singleOf(::SupabaseStorageService) // New Supabase Storage service

    // Data sources
    factoryOf(::HabitsRemoteDataSource)
}
