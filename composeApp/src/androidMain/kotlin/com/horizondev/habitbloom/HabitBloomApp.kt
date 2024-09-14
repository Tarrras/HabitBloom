package com.horizondev.habitbloom

import android.app.Application
import com.horizondev.habitbloom.di.KoinInit
import com.russhwolf.settings.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class HabitBloomApp : Application() {

    override fun onCreate() {
        super.onCreate()

        KoinInit().init {
            androidLogger(level = if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(androidContext = this@HabitBloomApp)
        }
    }
}