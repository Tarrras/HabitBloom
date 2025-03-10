package com.horizondev.habitbloom.core.designComponents.bottomBar

import org.jetbrains.compose.resources.DrawableResource

data class TopLevelRoute<T : Any>(
    val name: String,
    val route: T,
    val outlinedIconRes: DrawableResource,
    val filledIconRes: DrawableResource
)
