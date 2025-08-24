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

    // Display styles - for hero content and large headers
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,

    // Heading hierarchy - H1 through H6
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h4: TextStyle,
    val h5: TextStyle,
    val h6: TextStyle,

    // Body text variants
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,

    // UI component text
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,

    // Utility text styles
    val caption: TextStyle,
    val overline: TextStyle,

    // Button variants
    val buttonLarge: TextStyle,
    val buttonMedium: TextStyle,
    val buttonSmall: TextStyle
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

        // Display styles - for hero content and large headers
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.2).sp
        ),
        displaySmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.15).sp
        ),

        // Heading hierarchy - H1 through H6
        h1 = TextStyle(
            fontFamily = fontFamily,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.1).sp
        ),
        h2 = TextStyle(
            fontFamily = fontFamily,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.05).sp
        ),
        h3 = TextStyle(
            fontFamily = fontFamily,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp
        ),
        h4 = TextStyle(
            fontFamily = fontFamily,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp
        ),
        h5 = TextStyle(
            fontFamily = fontFamily,
            fontSize = 18.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp
        ),
        h6 = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp
        ),

        // Body text variants
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.1.sp
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.2.sp
        ),

        // UI component text
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
        ),

        // Utility text styles
        caption = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.4.sp
        ),
        overline = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.5.sp
        ),

        // Button variants
        buttonLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.1.sp
        ),
        buttonMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.2.sp
        ),
        buttonSmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.3.sp
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

        // Display styles
        displayLarge = TextStyle.Default,
        displayMedium = TextStyle.Default,
        displaySmall = TextStyle.Default,

        // Heading hierarchy
        h1 = TextStyle.Default,
        h2 = TextStyle.Default,
        h3 = TextStyle.Default,
        h4 = TextStyle.Default,
        h5 = TextStyle.Default,
        h6 = TextStyle.Default,

        // Body variants
        bodyLarge = TextStyle.Default,
        bodyMedium = TextStyle.Default,
        bodySmall = TextStyle.Default,

        // Labels
        labelLarge = TextStyle.Default,
        labelMedium = TextStyle.Default,
        labelSmall = TextStyle.Default,

        // Utility
        caption = TextStyle.Default,
        overline = TextStyle.Default,

        // Button variants
        buttonLarge = TextStyle.Default,
        buttonMedium = TextStyle.Default,
        buttonSmall = TextStyle.Default
    )
}