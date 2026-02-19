package com.dailyshayari.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * A class to model the text color roles for the luxury theme.
 */
data class LuxuryTextRoles(
    val appTitle: Color,
    val sectionHeader: Color,
    val authorName: Color,
    val body: Color,
    val secondary: Color
)

val LocalLuxuryTextColors = staticCompositionLocalOf {
    LuxuryTextRoles(
        appTitle = Color.Unspecified,
        sectionHeader = Color.Unspecified,
        authorName = Color.Unspecified,
        body = Color.Unspecified,
        secondary = Color.Unspecified
    )
}
