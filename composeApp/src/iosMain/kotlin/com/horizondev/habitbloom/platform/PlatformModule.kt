package com.horizondev.habitbloom.platform

import com.horizondev.habitbloom.core.notifications.IOSNotificationDelegate
import com.horizondev.habitbloom.core.notifications.IOSNotificationManager
import com.horizondev.habitbloom.core.notifications.IOSNotificationScheduler
import com.horizondev.habitbloom.core.notifications.NotificationScheduler
import dev.icerock.moko.permissions.ios.PermissionsController
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseDriverFactory() }
    single { IOSImagePicker() } bind ImagePicker::class

    single { IOSNotificationDelegate(get(), get()) }
    single { IOSNotificationManager(get()) }
    single {
        IOSNotificationScheduler(get())
    } bind NotificationScheduler::class

    single { PermissionsController() }

}