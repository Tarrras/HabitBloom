package com.horizondev.habitbloom.core.data.preferences

import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Implementation of [PreferencesDataSource] using multiplatform Settings.
 */
class PreferencesDataSourceImpl(
    private val settings: Settings
) : PreferencesDataSource {

    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            settings.getBoolean(key, defaultValue)
        }

    override suspend fun putBoolean(key: String, value: Boolean) =
        withContext(Dispatchers.IO) {
            settings.putBoolean(key, value)
        }

    override suspend fun getString(key: String, defaultValue: String): String =
        withContext(Dispatchers.IO) {
            settings.getString(key, defaultValue)
        }

    override suspend fun putString(key: String, value: String) =
        withContext(Dispatchers.IO) {
            settings.putString(key, value)
        }

    override suspend fun contains(key: String): Boolean =
        withContext(Dispatchers.IO) {
            settings.hasKey(key)
        }

    override suspend fun remove(key: String) =
        withContext(Dispatchers.IO) {
            settings.remove(key)
        }
} 