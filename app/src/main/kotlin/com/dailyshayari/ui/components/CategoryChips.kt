package com.dailyshayari.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CategoryChips(selectedCategory: String?, onCategorySelected: (String) -> Unit) {
    val categories = listOf("All", "Love", "Sad", "Motivation", "Friendship", "Gita", "Quotes")
    val luxuryGold = Color(0xFFC6A75E)
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(categories) { category ->
            val isSelected = category.equals(selectedCategory, ignoreCase = true)
            val backgroundColor by animateColorAsState(targetValue = if (isSelected) luxuryGold else Color.Transparent, animationSpec = tween(300))
            val textColor by animateColorAsState(targetValue = if (isSelected) Color.Black else luxuryGold, animationSpec = tween(300))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(backgroundColor)
                    .border(1.dp, luxuryGold, RoundedCornerShape(50))
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(text = category, color = textColor, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
