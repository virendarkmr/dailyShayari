package com.dailyshayari

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyshayari.ui.theme.GoldPrimary
import com.dailyshayari.ui.theme.LocalLuxuryTextColors
import com.dailyshayari.ui.theme.ScreenPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigate: (Int, String?) -> Unit) {
    val luxuryText = LocalLuxuryTextColors.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Settings",
                            style = MaterialTheme.typography.headlineMedium,
                            color = luxuryText.appTitle
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            // AppBottomBar is handled in MainActivity
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = ScreenPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting Section
            Column {
                Text(
                    "Namaste!",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = luxuryText.body
                )
                Text(
                    "Welcome to your personal poetry space.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = luxuryText.secondary
                )
            }

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    count = "245",
                    label = "LIKED",
                    icon = Icons.Rounded.Favorite,
                    iconColor = Color(0xFFE91E63)
                )
                StatCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(1, null) },
                    count = "Search",
                    label = "SHAYARI",
                    icon = Icons.Rounded.Search,
                    iconColor = GoldPrimary
                )
            }

            // Engagement Section
            SettingsSection(title = "ENGAGEMENT") {
                SettingsItem(
                    icon = Icons.Rounded.Share,
                    title = "Share App",
                    trailingIcon = Icons.Rounded.OpenInNew,
                    onClick = {}
                )
                Spacer(modifier = Modifier.height(8.dp))
                SettingsItem(
                    icon = Icons.Rounded.Star,
                    title = "Rate Us",
                    onClick = {}
                )
                Spacer(modifier = Modifier.height(8.dp))
                SettingsItem(
                    icon = Icons.Rounded.Policy,
                    title = "Privacy Policy",
                    onClick = {}
                )
            }

            // Go Ad-Free Card
            AdFreeCard()

            // Version Info
            Text(
                "VERSION 2.4.0 • MADE WITH ❤️ IN INDIA",
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = luxuryText.secondary.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    count: String,
    label: String,
    icon: ImageVector,
    iconColor: Color
) {
    val luxuryText = LocalLuxuryTextColors.current
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = luxuryText.body
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = luxuryText.secondary
            )
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    val luxuryText = LocalLuxuryTextColors.current
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.2.sp),
            color = luxuryText.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    iconContainerColor: Color = MaterialTheme.colorScheme.surface,
    trailingIcon: ImageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
    onClick: () -> Unit
) {
    val luxuryText = LocalLuxuryTextColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconContainerColor.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (iconContainerColor == MaterialTheme.colorScheme.surface) luxuryText.secondary else iconContainerColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = luxuryText.body)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = luxuryText.secondary)
            }
        }
        Icon(
            imageVector = trailingIcon,
            contentDescription = null,
            tint = luxuryText.secondary.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun AdFreeCard() {
    val luxuryText = LocalLuxuryTextColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF0D47A1).copy(alpha = 0.3f), Color(0xFF000000).copy(alpha = 0.5f))
                )
            )
            .border(1.dp, Color(0xFF2196F3).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Go Ad-Free",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Support the creators and remove all banners.",
                    style = MaterialTheme.typography.bodySmall,
                    color = luxuryText.secondary
                )
            }
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF2196F3).copy(alpha = 0.3f),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
