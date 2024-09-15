package com.horizondev.habitbloom.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.horizondev.habitbloom.database.HabitBloomDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(schema = HabitBloomDatabase.Schema, name = "bloom.db")
    }
}
