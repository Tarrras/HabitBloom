package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.auth.data.AuthRemoteDataSource
import com.horizondev.habitbloom.habits.data.remote.HabitsRemoteDataSource
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun remoteDataModule() = module {
    single { Firebase.firestore } bind FirebaseFirestore::class
    single { Firebase.auth } bind FirebaseAuth::class

    factoryOf(::HabitsRemoteDataSource)
    factoryOf(::AuthRemoteDataSource)
}