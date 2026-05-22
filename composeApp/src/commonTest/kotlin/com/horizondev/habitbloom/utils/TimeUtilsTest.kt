package com.horizondev.habitbloom.utils

import com.horizondev.habitbloom.common.locale.AppLocale
import com.horizondev.habitbloom.common.settings.uses24HourTimeFormat
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimeUtilsTest {

    @Test
    fun uses24HourTimeFormat_matchesSupportedLocales() {
        assertFalse(AppLocale.English.uses24HourTimeFormat())
        assertTrue(AppLocale.Ukraine.uses24HourTimeFormat())
    }

    @Test
    fun formatTimeForLocale_usesLocaleTimeFormat() {
        val time = LocalTime(20, 5)

        assertEquals("08:05 PM", formatTimeForLocale(time, AppLocale.English))
        assertEquals("20:05", formatTimeForLocale(time, AppLocale.Ukraine))
    }
}
