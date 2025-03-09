package com.horizondev.habitbloom.core.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Navigator class which holds the current navigation target state (must be Signleton)
 * */
class CommonNavigator {

    private val _destinationsSharedFlow = MutableSharedFlow<NavTarget>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val destinationsSharedFlow = _destinationsSharedFlow.asSharedFlow()

    fun navigateTo(navTarget: NavTarget) {
        _destinationsSharedFlow.tryEmit(navTarget)
    }
}
