package com.horizondev.habitbloom.platform

import com.horizondev.habitbloom.core.notifications.IOSNotificationCenterService
import com.horizondev.habitbloom.core.notifications.IOSNotificationEventHandler
import com.horizondev.habitbloom.core.notifications.IOSNotificationManager
import com.horizondev.habitbloom.core.notifications.IOSNotificationScheduler
import com.horizondev.habitbloom.core.notifications.NotificationScheduler
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class PlatformModuleTest {
    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun platformModule_initializes_on_ios() {
        assertNotNull(platformModule)
    }

    @Test
    fun platformModule_resolves_ios_notification_dependencies() {
        val koin = startKoin {
            modules(platformModule)
        }.koin

        assertNotNull(koin.get<IOSNotificationCenterService>())
        assertNotNull(koin.get<IOSNotificationEventHandler>())
        assertNotNull(koin.get<IOSNotificationManager>())
        assertIs<IOSNotificationScheduler>(koin.get<NotificationScheduler>())
    }
}
