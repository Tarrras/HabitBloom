package com.horizondev.habitbloom.platform

import com.horizondev.habitbloom.core.notifications.AndroidNotificationManager
import com.horizondev.habitbloom.core.notifications.NotificationManager
import dev.icerock.moko.permissions.PermissionsController
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseDriverFactory(context = get()) }
    single { AndroidImagePicker(context = get()) } bind ImagePicker::class

    single {
        AndroidNotificationManager(context = androidContext())
    } bind NotificationManager::class

    single { PermissionsController(androidApplication()) }
}