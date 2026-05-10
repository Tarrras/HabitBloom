package com.horizondev.habitbloom.core.notifications

import io.github.aakira.napier.Napier
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IOSNotificationCenterService(
    private val notificationCenter: UNUserNotificationCenter = UNUserNotificationCenter.currentNotificationCenter()
) {
    fun setDelegate(delegate: UNUserNotificationCenterDelegateProtocol) {
        notificationCenter.delegate = delegate
    }

    fun removeNotificationsById(identifiers: List<String>) {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(identifiers)
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(identifiers)
    }

    suspend fun makeNotificationRequest(request: UNNotificationRequest): Boolean =
        suspendCoroutine { continuation ->
            notificationCenter.addNotificationRequest(request) { error ->
                if (error != null) {
                    Napier.d("Error scheduling notification: ${error.localizedDescription}")
                }

                continuation.resume(error == null)
            }
        }
}
