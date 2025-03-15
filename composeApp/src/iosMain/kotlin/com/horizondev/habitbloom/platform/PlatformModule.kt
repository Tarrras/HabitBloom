package com.horizondev.habitbloom.platform

import com.horizondev.habitbloom.core.notifications.IOSNotificationManager
import com.horizondev.habitbloom.core.notifications.NotificationManager
import dev.icerock.moko.permissions.ios.PermissionsController
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseDriverFactory() }
    single { IOSImagePicker() } bind ImagePicker::class

    single {
        IOSNotificationManager()
    } bind NotificationManager::class

    single { PermissionsController() }

}