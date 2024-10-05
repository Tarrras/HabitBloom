package com.horizondev.habitbloom.common.mainTabs

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.ic_calendar_filled
import habitbloom.composeapp.generated.resources.ic_chart_proportion_filled
import habitbloom.composeapp.generated.resources.ic_home_filled
import habitbloom.composeapp.generated.resources.ic_profile_filled
import org.jetbrains.compose.resources.painterResource

@Composable
fun TabFilledIcon(item: Tab) = when (item.options.title) {
    HabitBloomTab.HomeTab.toString() -> painterResource(Res.drawable.ic_home_filled)
    HabitBloomTab.StatisticTab.toString() -> painterResource(Res.drawable.ic_chart_proportion_filled)
    HabitBloomTab.CalendarTab.toString() -> painterResource(Res.drawable.ic_calendar_filled)
    HabitBloomTab.ProfileTab.toString() -> painterResource(Res.drawable.ic_profile_filled)
    else -> painterResource(Res.drawable.ic_home_filled)
}
