package com.horizondev.habitbloom.core.notifications

import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptionAlert
import platform.UserNotifications.UNNotificationPresentationOptionBadge
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

class IOSNotificationDelegate(
    private val notificationEventHandler: IOSNotificationEventHandler
) : NSObject(), UNUserNotificationCenterDelegateProtocol {
    // Called when a notification is delivered to a foreground app
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit
    ) {
        // Show the notification with sound, alert, and badge
        withCompletionHandler(
            UNNotificationPresentationOptionAlert or
                    UNNotificationPresentationOptionBadge or
                    UNNotificationPresentationOptionSound
        )

        // Schedule the next notification
        handleNotificationDelivery(willPresentNotification)
    }

    // Called to handle the user's response to a delivered notification
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        didReceiveNotificationResponse: UNNotificationResponse,
        withCompletionHandler: () -> Unit
    ) {
        // Complete the handler
        withCompletionHandler()

        // Schedule the next notification
        handleNotificationDelivery(didReceiveNotificationResponse.notification)
    }

    private fun handleNotificationDelivery(notification: UNNotification) {
        notificationEventHandler.handleNotificationDelivery(notification)
    }
}
