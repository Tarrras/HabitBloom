package com.horizondev.habitbloom.platform

import com.horizondev.habitbloom.common.locale.AppLocaleManager
import com.horizondev.habitbloom.core.notifications.IOSNotificationCenterService
import com.horizondev.habitbloom.core.notifications.IOSNotificationEventHandler
import com.horizondev.habitbloom.core.notifications.IOSNotificationManager
import com.horizondev.habitbloom.core.notifications.IOSNotificationScheduler
import com.horizondev.habitbloom.core.notifications.NotificationScheduler
import dev.icerock.moko.permissions.ios.PermissionsController
import dev.icerock.moko.permissions.ios.PermissionsControllerProtocol
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactory() }
    single<IOSImagePicker> { IOSImagePicker() } bind ImagePicker::class

    single<IOSNotificationCenterService> { IOSNotificationCenterService() }

    single<IOSNotificationEventHandler> {
        IOSNotificationEventHandler(
            notificationScheduler = get(),
            habitsRepository = get()
        )
    }

    single<IOSNotificationManager>(createdAtStart = true) {
        IOSNotificationManager(
            notificationCenterService = get(),
            notificationEventHandler = get()
        )
    }

    single<NotificationScheduler> {
        IOSNotificationScheduler(get())
    }

    // On iOS, the common PermissionsController type is an actual typealias
    // to PermissionsControllerProtocol, so Koin must expose that contract directly.
    single<PermissionsControllerProtocol> { PermissionsController() }

    single<IosAppLocaleManager> { IosAppLocaleManager() } bind AppLocaleManager::class
}
