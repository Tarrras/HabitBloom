package com.horizondev.habitbloom.platform

import com.horizondev.habitbloom.common.locale.AppLocale
import com.horizondev.habitbloom.common.locale.AppLocaleManager
import platform.Foundation.NSLocaleLanguageCode

class IosAppLocaleManager : AppLocaleManager {
    override fun getLocale(): AppLocale {
        val nsLocale = NSLocaleLanguageCode ?: ""
        return AppLocale.fromCode(nsLocale.lowercase()) ?: AppLocale.English
    }
}