package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.habits.data.HabitsRemoteDataSource
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun dataModule() = module {
    single { Firebase.firestore } bind FirebaseFirestore::class
    factoryOf(::HabitsRemoteDataSource)
}