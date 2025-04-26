package com.horizondev.habitbloom.screens.onboarding

import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.afternoon_habits_image
import habitbloom.composeapp.generated.resources.evening_habits_image
import habitbloom.composeapp.generated.resources.morning_habits_image
import habitbloom.composeapp.generated.resources.onboarding_consistency_desc
import habitbloom.composeapp.generated.resources.onboarding_consistency_title
import habitbloom.composeapp.generated.resources.onboarding_growth_desc
import habitbloom.composeapp.generated.resources.onboarding_growth_title
import habitbloom.composeapp.generated.resources.onboarding_habit_desc
import habitbloom.composeapp.generated.resources.onboarding_habit_title
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * Enum representing the different pages in the onboarding flow
 */
enum class OnboardingPage(
    val imageResource: DrawableResource,
    val titleResource: StringResource,
    val descriptionResource: StringResource
) {
    // First page: Introducing habit tracking
    TRACK_HABITS(
        imageResource = Res.drawable.morning_habits_image,
        titleResource = Res.string.onboarding_habit_title,
        descriptionResource = Res.string.onboarding_habit_desc
    ),

    // Second page: Highlighting consistency and streaks
    BUILD_CONSISTENCY(
        imageResource = Res.drawable.afternoon_habits_image,
        titleResource = Res.string.onboarding_consistency_title,
        descriptionResource = Res.string.onboarding_consistency_desc
    ),

    // Third page: Showcasing visual growth
    WATCH_GROWTH(
        imageResource = Res.drawable.evening_habits_image,
        titleResource = Res.string.onboarding_growth_title,
        descriptionResource = Res.string.onboarding_growth_desc
    )
} 