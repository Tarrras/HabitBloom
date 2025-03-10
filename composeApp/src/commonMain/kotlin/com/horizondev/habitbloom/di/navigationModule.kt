package com.horizondev.habitbloom.di

import com.horizondev.habitbloom.core.navigation.CommonNavigator
import org.koin.dsl.module

val navigationModule = module {
    single { CommonNavigator() }
}