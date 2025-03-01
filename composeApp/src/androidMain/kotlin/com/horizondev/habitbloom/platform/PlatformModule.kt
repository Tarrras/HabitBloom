package com.horizondev.habitbloom.platform

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DatabaseDriverFactory(context = get()) }
    single { AndroidImagePicker(context = get()) } bind ImagePicker::class
}