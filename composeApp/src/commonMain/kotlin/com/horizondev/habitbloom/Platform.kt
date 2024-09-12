package com.horizondev.habitbloom

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform