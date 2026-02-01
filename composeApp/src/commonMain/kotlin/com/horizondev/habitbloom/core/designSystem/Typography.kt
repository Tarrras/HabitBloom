package com.horizondev.habitbloom.core.designSystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import habitbloom.composeapp.generated.resources.Poppins_Black
import habitbloom.composeapp.generated.resources.Poppins_Bold
import habitbloom.composeapp.generated.resources.Poppins_ExtraBold
import habitbloom.composeapp.generated.resources.Poppins_ExtraLight
import habitbloom.composeapp.generated.resources.Poppins_Light
import habitbloom.composeapp.generated.resources.Poppins_Medium
import habitbloom.composeapp.generated.resources.Poppins_Regular
import habitbloom.composeapp.generated.resources.Poppins_SemiBold
import habitbloom.composeapp.generated.resources.Poppins_Thin
import habitbloom.composeapp.generated.resources.Res
import habitbloom.composeapp.generated.resources.montserrat_black
import habitbloom.composeapp.generated.resources.montserrat_bold
import habitbloom.composeapp.generated.resources.montserrat_extrabold
import habitbloom.composeapp.generated.resources.montserrat_extralight
import habitbloom.composeapp.generated.resources.montserrat_light
import habitbloom.composeapp.generated.resources.montserrat_medium
import habitbloom.composeapp.generated.resources.montserrat_regular
import habitbloom.composeapp.generated.resources.montserrat_semi_bold
import habitbloom.composeapp.generated.resources.montserrat_thin
import org.jetbrains.compose.resources.Font

@Composable
fun montserrat(): FontFamily {
    val montserratRegular =
        Font(
            resource = Res.font.montserrat_regular,
            weight = FontWeight.Normal,
            style = FontStyle.Normal,
        )

    val montserratBold =
        Font(
            resource = Res.font.montserrat_bold,
            FontWeight.Bold,
            FontStyle.Normal,
        )

    val montserratLight =
        Font(
            resource = Res.font.montserrat_light,
            FontWeight.Light,
            FontStyle.Normal,
        )

    val montserratMedium =
        Font(
            resource = Res.font.montserrat_medium,
            FontWeight.Medium,
            FontStyle.Normal,
        )

    val montserratSemiBold =
        Font(
            resource = Res.font.montserrat_semi_bold,
            FontWeight.SemiBold,
            FontStyle.Normal,
        )

    val montserratThin =
        Font(
            resource = Res.font.montserrat_thin,
            FontWeight.Thin,
            FontStyle.Normal,
        )

    val montserratExtraBold =
        Font(
            resource = Res.font.montserrat_extrabold,
            FontWeight.ExtraBold,
            FontStyle.Normal,
        )

    val montserratExtraLight =
        Font(
            resource = Res.font.montserrat_extralight,
            FontWeight.ExtraLight,
            FontStyle.Normal,
        )
    val montserratBlack = Font(
        resource = Res.font.montserrat_black,
        FontWeight.Black,
        FontStyle.Normal,
    )

    return FontFamily(
        montserratThin,
        montserratExtraLight,
        montserratLight,
        montserratRegular,
        montserratMedium,
        montserratSemiBold,
        montserratBold,
        montserratExtraBold,
        montserratBlack,
    )
}

@Composable
fun poppins(): FontFamily {
    val fixeltextRegular =
        Font(
            resource = Res.font.Poppins_Regular,
            weight = FontWeight.Normal,
            style = FontStyle.Normal,
        )

    val fixeltextBold =
        Font(
            resource = Res.font.Poppins_Bold,
            FontWeight.Bold,
            FontStyle.Normal,
        )

    val fixeltextLight =
        Font(
            resource = Res.font.Poppins_Light,
            FontWeight.Light,
            FontStyle.Normal,
        )

    val fixeltextMedium =
        Font(
            resource = Res.font.Poppins_Medium,
            FontWeight.Medium,
            FontStyle.Normal,
        )

    val fixeltextSemiBold =
        Font(
            resource = Res.font.Poppins_SemiBold,
            FontWeight.SemiBold,
            FontStyle.Normal,
        )

    val fixeltextThin =
        Font(
            resource = Res.font.Poppins_Thin,
            FontWeight.Thin,
            FontStyle.Normal,
        )

    val fixeltextExtraBold =
        Font(
            resource = Res.font.Poppins_ExtraBold,
            FontWeight.ExtraBold,
            FontStyle.Normal,
        )

    val fixeltextExtraLight =
        Font(
            resource = Res.font.Poppins_ExtraLight,
            FontWeight.ExtraLight,
            FontStyle.Normal,
        )
    val fixeltextBlack = Font(
        resource = Res.font.Poppins_Black,
        FontWeight.Black,
        FontStyle.Normal,
    )

    return FontFamily(
        fixeltextThin,
        fixeltextExtraLight,
        fixeltextLight,
        fixeltextRegular,
        fixeltextMedium,
        fixeltextSemiBold,
        fixeltextBold,
        fixeltextExtraBold,
        fixeltextBlack,
    )
}

data class BloomTypography(
    // Existing styles (preserved for compatibility)
    val title: TextStyle,
    val heading: TextStyle,
    val subheading: TextStyle,
    val body: TextStyle,
    val small: TextStyle,
    val button: TextStyle,
    val formLabel: TextStyle,

    // New styles

    // Display
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,

    // Headline
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,

    // Body
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,

    // Label
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,

    // Title
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle
)

@Composable
fun bloomTypography(): BloomTypography {
    val fontFamily = poppins()
    return BloomTypography(
        // Existing styles (preserved for compatibility)
        title = TextStyle(
            fontFamily = fontFamily,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
        ),
        heading = TextStyle(
            fontFamily = fontFamily,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Medium
        ),
        subheading = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal
        ),
        body = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal
        ),
        small = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal
        ),
        button = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium
        ),
        formLabel = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal
        ),

        // Display
        displayLarge = TextStyle(
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 43.sp,
            letterSpacing = (-0.4).sp
        ),
        displayMedium = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 39.sp,
            letterSpacing = (-0.32).sp
        ),
        displaySmall = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 34.sp,
            letterSpacing = (-0.4).sp
        ),

        // Headline
        headlineLarge = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 28.sp,
            letterSpacing = (-0.32).sp
        ),
        headlineMedium = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 27.sp,
            letterSpacing = (-0.24).sp
        ),
        headlineSmall = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 24.sp,
            letterSpacing = (-0.18).sp
        ),

        // Body
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 26.sp,
            letterSpacing = (-0.18).sp
        ),
        bodyMedium = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 22.sp,
            letterSpacing = (-0.18).sp
        ),
        bodySmall = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 17.sp
        ),

        // Label
        labelLarge = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 21.sp,
            letterSpacing = (-0.18).sp
        ),
        labelMedium = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 17.sp
        ),
        labelSmall = TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 13.sp
        ),

        // Title
        titleLarge = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 28.sp,
            letterSpacing = (-0.32).sp
        ),
        titleMedium = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 24.sp,
            letterSpacing = (-0.18).sp
        ),
        titleSmall = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 21.sp,
            letterSpacing = (-0.18).sp
        )
    )
}


val LocalBloomTypography = staticCompositionLocalOf {
    BloomTypography(
        // Existing styles
        title = TextStyle.Default,
        body = TextStyle.Default,
        heading = TextStyle.Default,
        subheading = TextStyle.Default,
        small = TextStyle.Default,
        button = TextStyle.Default,
        formLabel = TextStyle.Default,

        // Display
        displayLarge = TextStyle.Default,
        displayMedium = TextStyle.Default,
        displaySmall = TextStyle.Default,

        // Headline
        headlineLarge = TextStyle.Default,
        headlineMedium = TextStyle.Default,
        headlineSmall = TextStyle.Default,

        // Body
        bodyLarge = TextStyle.Default,
        bodyMedium = TextStyle.Default,
        bodySmall = TextStyle.Default,

        // Label
        labelLarge = TextStyle.Default,
        labelMedium = TextStyle.Default,
        labelSmall = TextStyle.Default,

        // Title
        titleLarge = TextStyle.Default,
        titleMedium = TextStyle.Default,
        titleSmall = TextStyle.Default
    )
}