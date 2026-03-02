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
    color: Color = GoldPrimary.copy(alpha = 0.7f)
) {
    // Determine the best text color if a background color is provided
    val textColor = if (backgroundColor != null) {
        if (backgroundColor.luminance() > 0.5f) Color.Black.copy(alpha = 0.6f) 
        else Color.White.copy(alpha = 0.7f)
    } else {
        color
    }

    // Add a shadow for guaranteed visibility on any complex background
    val textShadow = Shadow(
        color = if (textColor.luminance() > 0.5f) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.5f),
        offset = Offset(1f, 1f),
        blurRadius = 2f
    )

    Text(
        text = "Daily Shayari",
        modifier = modifier
            .align(Alignment.TopEnd)
            .padding(12.dp),
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = PlayfairDisplayFontFamily,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontSize = 10.sp,
            shadow = textShadow
        ),
        color = textColor
    )
}
