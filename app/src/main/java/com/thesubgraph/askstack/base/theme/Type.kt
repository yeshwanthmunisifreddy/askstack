package com.thesubgraph.askstack.base.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.thesubgraph.askstack.R

// Set of Material typography styles to start with

val workSansFontFamily = FontFamily(
    listOf(
        Font(R.font.worksans_regular, FontWeight.Normal),
        Font(R.font.worksans_light, FontWeight.Light),
        Font(R.font.worksans_bold, FontWeight.Bold)
    )
)

val defaultFontFamily = workSansFontFamily

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = defaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
val TextStyle_Size10_Weight400: TextStyle
    get() = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 10.sp,
        fontFamily = defaultFontFamily,
    )

val TextStyle_Size12_Weight400: TextStyle
    get() = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        fontFamily = defaultFontFamily,
    )

val TextStyle_Size14_Weight400: TextStyle
    get() = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        fontFamily = defaultFontFamily,
    )

val TextStyle_Size16_Weight400: TextStyle
    get() = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        fontFamily = defaultFontFamily,
    )


val TextStyle_Size12_Weight700: TextStyle
    get() = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 12.sp,
        fontFamily = defaultFontFamily,
    )

val TextStyle_Size14_Weight700: TextStyle
    get() = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 14.sp,
        fontFamily = defaultFontFamily,
    )

val TextStyle_Size16_Weight700: TextStyle
    get() = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 16.sp,
        fontFamily = defaultFontFamily,
    )


val TextStyle_Size16_Weight600: TextStyle
    get() = TextStyle(
        fontWeight = FontWeight.W600,
        fontSize = 16.sp,
        fontFamily = defaultFontFamily,
    )


