package com.horizondev.habitbloom.common.locale

enum class AppLocale(
    val code: String
) {
    English("en"),
    Ukraine("uk");

    companion object {
        fun fromCode(code: String): AppLocale? {
            return AppLocale.entries.find { it.code == code }
        }
    }
}