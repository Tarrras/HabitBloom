package com.horizondev.habitbloom.platform

import com.horizondev.habitbloom.common.locale.AppLocaleManager
import com.horizondev.habitbloom.core.notifications.AndroidNotificationManager
import com.horizondev.habitbloom.core.notifications.AndroidNotificationScheduler
import com.horizondev.habitbloom.core.notifications.NotificationScheduler
import dev.icerock.moko.permissions.PermissionsController
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseDriverFactory(context = get()) }
    single { AndroidImagePicker(context = get()) } bind ImagePicker::class
    single { AndroidAppLocaleManager(context = androidApplication()) } bind AppLocaleManager::class

    single { AndroidNotificationManager(context = androidContext()) }

    single {
        AndroidNotificationScheduler(context = androidContext())
    } bind NotificationScheduler::class

    single { PermissionsController(androidApplication()) }
}