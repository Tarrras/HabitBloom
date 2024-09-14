package com.horizondev.habitbloom.core.designSystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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

data class BloomTypography(
    val title: TextStyle,
    val heading: TextStyle,
    val subheading: TextStyle,
    val body: TextStyle,
    val small: TextStyle,
    val button: TextStyle,
    val formLabel: TextStyle
)

@Composable
fun bloomTypography(): BloomTypography {
    val montserrat = montserrat()
    return BloomTypography(
        title = TextStyle(
            fontFamily = montserrat,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Bold
        ),
        heading = TextStyle(
            fontFamily = montserrat,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Medium
        ),
        subheading = TextStyle(
            fontFamily = montserrat,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Normal
        ),
        body = TextStyle(
            fontFamily = montserrat,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Normal
        ),
        small = TextStyle(
            fontFamily = montserrat,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Normal
        ),
        button = TextStyle(
            fontFamily = montserrat,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Medium
        ),
        formLabel = TextStyle(
            fontFamily = montserrat,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp,
            fontWeight = FontWeight.Normal
        )
    )
}


val LocalBloomTypography = staticCompositionLocalOf {
    BloomTypography(
        title = TextStyle.Default,
        body = TextStyle.Default,
        heading = TextStyle.Default,
        subheading = TextStyle.Default,
        small = TextStyle.Default,
        button = TextStyle.Default,
        formLabel = TextStyle.Default
    )
}