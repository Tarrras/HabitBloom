package com.horizondev.habitbloom.common.settings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotificationStateTest {

    @Test
    fun testIsEnabled() {
        assertTrue(NotificationState.ENABLED.isEnabled())
        assertFalse(NotificationState.DISABLED.isEnabled())
        assertFalse(NotificationState.NOT_DETERMINED.isEnabled())
    }

    @Test
    fun testIsNotDetermined() {
        assertTrue(NotificationState.NOT_DETERMINED.isNotDetermined())
        assertFalse(NotificationState.ENABLED.isNotDetermined())
        assertFalse(NotificationState.DISABLED.isNotDetermined())
    }

    @Test
    fun testFromBoolean() {
        assertEquals(NotificationState.ENABLED, NotificationState.fromBoolean(true))
        assertEquals(NotificationState.DISABLED, NotificationState.fromBoolean(false))
    }
} 