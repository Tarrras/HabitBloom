package com.horizondev.habitbloom.platform

import android.content.Context
import com.horizondev.habitbloom.common.locale.AppLocale
import com.horizondev.habitbloom.common.locale.AppLocaleManager
import java.util.Locale

class AndroidAppLocaleManager(
    private val context: Context,
) : AppLocaleManager {

    override fun getLocale(): AppLocale {
        /*val deviceLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val currentAppLocales: LocaleList = context.getSystemService(LocaleManager::class.java).applicationLocales
            println("Current app locales")
            when {
                currentAppLocales.isEmpty -> "en"
                else -> currentAppLocales[0]?.toLanguageTag()?.split("-")?.firstOrNull() ?: "en"
            }
        } else {
            AppCompatDelegate.getApplicationLocales()
                .toLanguageTags().split("-")
                .firstOrNull() ?: "en"
        }*/
        val deviceLocale = Locale.getDefault().toLanguageTag()
            .split("-")
            .firstOrNull() ?: "en"

        return AppLocale.fromCode(deviceLocale) ?: AppLocale.English
    }
}