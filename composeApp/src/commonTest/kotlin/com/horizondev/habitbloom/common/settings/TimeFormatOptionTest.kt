package com.horizondev.habitbloom.common.settings

import com.horizondev.habitbloom.common.locale.AppLocale
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimeFormatOptionTest {

    @Test
    fun systemOptionUsesLocaleDefault() {
        assertFalse(TimeFormatOption.System.uses24HourFormat(AppLocale.English))
        assertTrue(TimeFormatOption.System.uses24HourFormat(AppLocale.Ukraine))
    }

    @Test
    fun explicitOptionsOverrideLocaleDefault() {
        assertFalse(TimeFormatOption.TwelveHour.uses24HourFormat(AppLocale.Ukraine))
        assertTrue(TimeFormatOption.TwentyFourHour.uses24HourFormat(AppLocale.English))
    }
}
