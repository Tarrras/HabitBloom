package com.horizondev.habitbloom.screens.onboarding

import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.onboarding_consistency_desc
import habitbloom.composeapp.generated.resources.onboarding_consistency_title
import habitbloom.composeapp.generated.resources.onboarding_growth_desc
import habitbloom.composeapp.generated.resources.onboarding_growth_title
import habitbloom.composeapp.generated.resources.onboarding_habit_desc
import habitbloom.composeapp.generated.resources.onboarding_habit_title
import habitbloom.composeapp.generated.resources.onboarding_illustration_step_consistency
import habitbloom.composeapp.generated.resources.onboarding_illustration_step_flower
import habitbloom.composeapp.generated.resources.onboarding_illustration_step_habit_tracking
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class OnboardingPage(
    val imageResource: DrawableResource,
    val titleResource: StringResource,
    val descriptionResource: StringResource
) {
    TRACK_HABITS(
        imageResource = Res.drawable.onboarding_illustration_step_habit_tracking,
        titleResource = Res.string.onboarding_habit_title,
        descriptionResource = Res.string.onboarding_habit_desc
    ),

    BUILD_CONSISTENCY(
        imageResource = Res.drawable.onboarding_illustration_step_consistency,
        titleResource = Res.string.onboarding_consistency_title,
        descriptionResource = Res.string.onboarding_consistency_desc
    ),

    WATCH_GROWTH(
        imageResource = Res.drawable.onboarding_illustration_step_flower,
        titleResource = Res.string.onboarding_growth_title,
        descriptionResource = Res.string.onboarding_growth_desc
    )
} 