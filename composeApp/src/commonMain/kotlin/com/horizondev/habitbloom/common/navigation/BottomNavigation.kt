package com.horizondev.habitbloom.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.ic_calendar_filled
import habitbloom.composeapp.generated.resources.ic_calendar_outlined
import habitbloom.composeapp.generated.resources.ic_chart_proportion_filled
import habitbloom.composeapp.generated.resources.ic_chart_proportion_outlined
import habitbloom.composeapp.generated.resources.ic_home_filled
import habitbloom.composeapp.generated.resources.ic_home_outlined
import habitbloom.composeapp.generated.resources.ic_profile_filled
import habitbloom.composeapp.generated.resources.ic_profile_outlined
import org.jetbrains.compose.resources.DrawableResource

/**
 * Represents a bottom navigation tab item.
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val outlinedIconRes: DrawableResource,
    val filledIconRes: DrawableResource
) {
    /**
     * Home tab.
     */
    data object Home : BottomNavItem(
        route = "home",
        title = "Home",
        outlinedIconRes = Res.drawable.ic_home_outlined,
        filledIconRes = Res.drawable.ic_home_filled
    )

    /**
     * Statistics tab.
     */
    data object Statistics : BottomNavItem(
        route = "statistics",
        title = "Statistics",
        outlinedIconRes = Res.drawable.ic_chart_proportion_outlined,
        filledIconRes = Res.drawable.ic_chart_proportion_filled
    )

    /**
     * Calendar tab.
     */
    data object Calendar : BottomNavItem(
        route = "calendar",
        title = "Calendar",
        outlinedIconRes = Res.drawable.ic_calendar_outlined,
        filledIconRes = Res.drawable.ic_calendar_filled
    )

    /**
     * Profile tab.
     */
    data object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        outlinedIconRes = Res.drawable.ic_profile_outlined,
        filledIconRes = Res.drawable.ic_profile_filled
    )
}

/**
 * Returns the list of bottom navigation items.
 */
@Composable
fun getBottomNavItems(): List<BottomNavItem> {
    return remember {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.Statistics,
            BottomNavItem.Calendar,
            BottomNavItem.Profile
        )
    }
} 