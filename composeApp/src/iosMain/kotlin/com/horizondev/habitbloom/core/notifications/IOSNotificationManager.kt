package com.horizondev.habitbloom.core.notifications

class IOSNotificationManager(
    notificationCenterService: IOSNotificationCenterService,
    notificationEventHandler: IOSNotificationEventHandler
) {
    private val notificationDelegate = IOSNotificationDelegate(notificationEventHandler)

    init {
        notificationCenterService.setDelegate(notificationDelegate)
    }
}
