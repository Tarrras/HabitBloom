package com.horizondev.habitbloom.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.horizondev.habitbloom.core.designComponents.bottomBar.TopLevelRoute
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.ic_calendar_filled
import habitbloom.composeapp.generated.resources.ic_calendar_outlined
import habitbloom.composeapp.generated.resources.ic_chart_proportion_filled
import habitbloom.composeapp.generated.resources.ic_chart_proportion_outlined
import habitbloom.composeapp.generated.resources.ic_garden_filled
import habitbloom.composeapp.generated.resources.ic_garden_outlined
import habitbloom.composeapp.generated.resources.ic_home_filled
import habitbloom.composeapp.generated.resources.ic_home_outlined
import habitbloom.composeapp.generated.resources.ic_setting_filled
import habitbloom.composeapp.generated.resources.ic_setting_outlined
import kotlinx.serialization.Serializable

/**
 * Represents a bottom navigation tab item.
 */
@Serializable
sealed class BottomNavItem {
    /**
     * Home tab.
     */
    @Serializable
    data object Home : BottomNavItem()

    /**
     * Statistics tab.
     */
    @Serializable
    data object Statistics : BottomNavItem()

    @Serializable
    data object Garden : BottomNavItem()

    /**
     * Calendar tab.
     */
    @Serializable
    data object Calendar : BottomNavItem()

    /**
     * Settings tab.
     */
    @Serializable
    data object Settings : BottomNavItem()
}

/**
 * Returns the list of bottom navigation items.
 */
@Composable
fun getBottomNavItems() = remember {
    listOf(
        TopLevelRoute(
            name = "Home",
            route = BottomNavItem.Home,
            outlinedIconRes = Res.drawable.ic_home_outlined,
            filledIconRes = Res.drawable.ic_home_filled
        ),
        TopLevelRoute(
            name = "Statistics",
            route = BottomNavItem.Statistics,
            outlinedIconRes = Res.drawable.ic_chart_proportion_outlined,
            filledIconRes = Res.drawable.ic_chart_proportion_filled
        ),
        TopLevelRoute(
            name = "Calendar",
            route = BottomNavItem.Calendar,
            outlinedIconRes = Res.drawable.ic_calendar_outlined,
            filledIconRes = Res.drawable.ic_calendar_filled
        ),
        TopLevelRoute(
            name = "Settings",
            route = BottomNavItem.Settings,
            outlinedIconRes = Res.drawable.ic_setting_outlined,
            filledIconRes = Res.drawable.ic_setting_filled
        ),
        TopLevelRoute(
            name = "Garden",
            route = BottomNavItem.Garden,
            outlinedIconRes = Res.drawable.ic_garden_outlined,
            filledIconRes = Res.drawable.ic_garden_filled
        )
    )
}