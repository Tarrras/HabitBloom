package com.horizondev.habitbloom.platform

import com.horizondev.habitbloom.common.locale.AppLocaleManager
import com.horizondev.habitbloom.core.notifications.IOSNotificationManager
import com.horizondev.habitbloom.core.notifications.IOSNotificationScheduler
import com.horizondev.habitbloom.core.notifications.NotificationScheduler
import dev.icerock.moko.permissions.ios.PermissionsController
import dev.icerock.moko.permissions.ios.PermissionsControllerProtocol
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseDriverFactory() }
    single { IOSImagePicker() } bind ImagePicker::class

    single {
        val koin = getKoin()
        IOSNotificationManager(
            notificationSchedulerProvider = { koin.get<NotificationScheduler>() },
            habitsRepositoryProvider = { koin.get() }
        )
    }
    single {
        IOSNotificationScheduler(get())
    } bind NotificationScheduler::class

    single { PermissionsController() } bind PermissionsControllerProtocol::class
    single { IosAppLocaleManager() } bind AppLocaleManager::class
}
