package com.horizondev.habitbloom.platform

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.horizondev.habitbloom.database.HabitBloomDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(HabitBloomDatabase.Schema, context, "bloom.db")
}