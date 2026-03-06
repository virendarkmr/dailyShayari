package com.dailyshayari.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyshayari.ui.theme.GoldPrimary
import com.dailyshayari.ui.theme.PlayfairDisplayFontFamily

@Composable
fun BoxScope.AppWatermark(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    color: Color = GoldPrimary
) {
    // Maximum contrast selection
    val textColor = if (backgroundColor != null) {
        if (backgroundColor.luminance() > 0.5f) Color.Black else Color.White
    } else {
        color
    }

    // Heavy shadow for "floating" effect on busy backgrounds
    val textShadow = Shadow(
        color = if (textColor.luminance() > 0.5f) Color.Black.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.9f),
        offset = Offset(2f, 2f),
        blurRadius = 8f
    )

    Text(
        text = "Daily Shayari",
        modifier = modifier
            .align(Alignment.TopEnd)
            .padding(14.dp),
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = PlayfairDisplayFontFamily,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp,
            fontSize = 12.sp,
            shadow = textShadow
        ),
        color = textColor
    )
}
