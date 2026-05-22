package com.horizondev.habitbloom.common.settings

import com.horizondev.habitbloom.common.locale.AppLocale

enum class TimeFormatOption {
    System,
    TwelveHour,
    TwentyFourHour;

    fun uses24HourFormat(locale: AppLocale): Boolean = when (this) {
        System -> locale.uses24HourTimeFormat()
        TwelveHour -> false
        TwentyFourHour -> true
    }
}

fun AppLocale.uses24HourTimeFormat(): Boolean = when (this) {
    AppLocale.English -> false
    AppLocale.Ukraine -> true
}
