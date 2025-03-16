package com.horizondev.habitbloom.core.notifications

import io.github.aakira.napier.Napier
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IOSNotificationManager(
    delegate: IOSNotificationDelegate
) {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    init {
        notificationCenter.delegate = delegate
    }

    /**
     * Removes notification by identifiers.
     */
    fun removeNotificationsById(identifiers: List<String>) {
        // Remove both pending and delivered notifications
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(identifiers)
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(identifiers)
    }

    suspend fun makeNotificationRequest(request: UNNotificationRequest) =
        suspendCoroutine { continuation ->
            // Add notification request to notification center
            notificationCenter.addNotificationRequest(request) { error ->
                if (error != null) {
                    Napier.d("Error scheduling notification: ${error.localizedDescription}")
                }

                continuation.resume(error == null)
            }
        }
}