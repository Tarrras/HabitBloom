package com.horizondev.habitbloom.core.permissions

import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.notifications.RemoteNotificationPermission

/**
 * Manager class for handling permissions across platforms using moko-permissions
 */
class PermissionsManager(private val permissionsController: PermissionsController) {

    /**
     * Request notification permission and return whether it's granted
     * @return Boolean indicating if permission was granted
     */
    suspend fun requestNotificationPermission(): Boolean {
        return try {
            val permission = RemoteNotificationPermission
            // If already has permission, return true
            if (permissionsController.isPermissionGranted(permission)) {
                return true
            }

            // Request permission
            val permissionResult = permissionsController.providePermission(permission)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Check if notification permission is granted
     * @return Boolean indicating if permission is granted
     */
    suspend fun hasNotificationPermission(): Boolean {
        return try {
            permissionsController.isPermissionGranted(RemoteNotificationPermission)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
} 