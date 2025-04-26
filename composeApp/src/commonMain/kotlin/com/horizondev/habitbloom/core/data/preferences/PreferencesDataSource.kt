package com.horizondev.habitbloom.core.data.preferences

/**
 * Interface for accessing and storing key-value preferences.
 */
interface PreferencesDataSource {
    /**
     * Gets a boolean value from preferences.
     *
     * @param key The preference key
     * @param defaultValue The default value to return if the preference doesn't exist
     * @return The preference value, or the default value if not found
     */
    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean

    /**
     * Stores a boolean value in preferences.
     *
     * @param key The preference key
     * @param value The value to store
     */
    suspend fun putBoolean(key: String, value: Boolean)

    /**
     * Gets a string value from preferences.
     *
     * @param key The preference key
     * @param defaultValue The default value to return if the preference doesn't exist
     * @return The preference value, or the default value if not found
     */
    suspend fun getString(key: String, defaultValue: String): String

    /**
     * Stores a string value in preferences.
     *
     * @param key The preference key
     * @param value The value to store
     */
    suspend fun putString(key: String, value: String)

    /**
     * Checks if a preference with the given key exists.
     *
     * @param key The preference key
     * @return True if the preference exists, false otherwise
     */
    suspend fun contains(key: String): Boolean

    /**
     * Removes a preference.
     *
     * @param key The preference key
     */
    suspend fun remove(key: String)
} 