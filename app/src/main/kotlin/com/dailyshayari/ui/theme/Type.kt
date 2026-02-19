package com.dailyshayari.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dailyshayari.R

val PlayfairDisplayFontFamily = FontFamily(
    Font(R.font.playfairdisplay_variable, FontWeight.SemiBold)
)

val PoppinsFontFamily = FontFamily(
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_medium, FontWeight.Medium)
)

val NotoSansDevanagariFontFamily = FontFamily(
    Font(R.font.notosansdevanagari_variable, FontWeight.Normal)
)


val Typography = Typography(
    // 1) App Title
    headlineLarge = TextStyle(
        fontFamily = PlayfairDisplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
    ),
    // 2) Section Titles
    headlineMedium = TextStyle(
        fontFamily = PlayfairDisplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
    ),
    // 3) Category Card Text
    titleMedium = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    // 4) Shayari Content Text
    bodyLarge = TextStyle(
        fontFamily = NotoSansDevanagariFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 28.sp
    )
)
