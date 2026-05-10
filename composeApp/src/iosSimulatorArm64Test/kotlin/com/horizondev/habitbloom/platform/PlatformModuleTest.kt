package com.horizondev.habitbloom.platform

import kotlin.test.Test
import kotlin.test.assertNotNull

class PlatformModuleTest {
    @Test
    fun platformModule_initializes_on_ios() {
        assertNotNull(platformModule)
    }
}
