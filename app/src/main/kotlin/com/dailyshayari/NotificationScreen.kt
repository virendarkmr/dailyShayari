package com.dailyshayari

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyshayari.data.Notification
import com.dailyshayari.ui.theme.LocalLuxuryTextColors
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(onBackClick: () -> Unit) {
    val luxuryText = LocalLuxuryTextColors.current
    val notifications = remember { mutableStateListOf<Notification>() } // Empty for now as requested
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Unread")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", color = luxuryText.appTitle) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = luxuryText.appTitle
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Mark all as read */ }) {
                        Icon(
                            imageVector = Icons.Rounded.DoneAll,
                            contentDescription = "Mark all as read",
                            tint = Color(0xFFE65100) // Orange-ish color from image
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                tabs.forEachIndexed { index, title ->
                    Column(
                        modifier = Modifier
                            .padding(end = 24.dp)
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            color = if (selectedTab == index) luxuryText.appTitle else luxuryText.secondary,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.noRippleClickable { selectedTab = index }
                        )
                        if (selectedTab == index) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .width(24.dp)
                                    .height(2.dp)
                                    .background(Color(0xFFE65100))
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)

            if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notifications yet", color = luxuryText.secondary)
                }
            } else {
                val groupedNotifications = groupNotificationsByDate(notifications)
                LazyColumn {
                    groupedNotifications.forEach { (date, dailyNotifications) ->
                        item {
                            NotificationHeader(date)
                        }
                        items(dailyNotifications) { notification ->
                            NotificationItem(notification)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationHeader(date: String) {
    val luxuryText = LocalLuxuryTextColors.current
    Text(
        text = date.uppercase(),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2D241E)) // Dark brown header background from image
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = luxuryText.secondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}

@Composable
fun NotificationItem(notification: Notification) {
    // This will be implemented when we have data
}

fun groupNotificationsByDate(notifications: List<Notification>): Map<String, List<Notification>> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = sdf.format(Date())
    val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }.run { sdf.format(time) }

    return notifications.groupBy {
        val dateStr = sdf.format(Date(it.timestamp))
        when (dateStr) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> "Earlier"
        }
    }
}

// Fixed no-ripple clickable extension
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
