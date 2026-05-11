package com.horizondev.habitbloom.screens.habits.domain

import com.horizondev.habitbloom.screens.habits.domain.models.HabitInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HabitCatalogCachePolicyTest {

    @Test
    fun resolveCatalogRefreshResult_usesRemoteCatalogWhenRefreshSucceeds() {
        val cached = listOf(habit(id = "cached", name = "Cached"))
        val remote = listOf(habit(id = "remote", name = "Remote"))

        val result = resolveCatalogRefreshResult(
            cachedHabits = cached,
            remoteResult = Result.success(remote)
        )

        assertTrue(result.isSuccess)
        assertEquals(remote, result.getOrThrow())
    }

    @Test
    fun resolveCatalogRefreshResult_fallsBackToCacheWhenRefreshFails() {
        val cached = listOf(habit(id = "cached", name = "Cached"))

        val result = resolveCatalogRefreshResult(
            cachedHabits = cached,
            remoteResult = Result.failure(IllegalStateException("network unavailable"))
        )

        assertTrue(result.isSuccess)
        assertEquals(cached, result.getOrThrow())
    }

    @Test
    fun resolveCatalogRefreshResult_returnsRemoteFailureWhenCacheIsEmpty() {
        val result = resolveCatalogRefreshResult(
            cachedHabits = emptyList(),
            remoteResult = Result.failure(IllegalStateException("network unavailable"))
        )

        assertTrue(result.isFailure)
    }

    private fun habit(id: String, name: String): HabitInfo {
        return HabitInfo(
            id = id,
            description = "$name description",
            iconUrl = "https://example.com/$id.png",
            name = name
        )
    }
}
