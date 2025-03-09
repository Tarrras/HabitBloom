package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.core.navigation.CommonNavigator
import org.koin.dsl.module

fun navigationModule() = module {
    single { CommonNavigator() }
}