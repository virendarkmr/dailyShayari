package com.dailyshayari

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailyshayari.data.Notification
import com.dailyshayari.ui.theme.GoldPrimary
import com.dailyshayari.ui.theme.LocalLuxuryTextColors
import com.dailyshayari.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    viewModel: NotificationViewModel = viewModel()
) {
    val luxuryText = LocalLuxuryTextColors.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Unread")

    val allNotifications by viewModel.allNotifications.collectAsState()
    val unreadNotifications by viewModel.unreadNotifications.collectAsState()

    val displayNotifications = if (selectedTab == 0) allNotifications else unreadNotifications

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
                    IconButton(onClick = { viewModel.markAllAsRead() }) {
                        Icon(
                            imageVector = Icons.Rounded.DoneAll,
                            contentDescription = "Mark all as read",
                            tint = Color(0xFFE65100)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                color = if (selectedTab == index) luxuryText.appTitle else luxuryText.secondary,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.noRippleClickable { selectedTab = index }
                            )
                            if (index == 1 && unreadNotifications.isNotEmpty()) {
                                Spacer(Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFFE65100), CircleShape)
                                )
                            }
                        }
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

            HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f), thickness = 0.5.dp)

            if (displayNotifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notifications yet", color = luxuryText.secondary)
                }
            } else {
                val groupedNotifications = groupNotificationsByDate(displayNotifications)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    groupedNotifications.forEach { (date, dailyNotifications) ->
                        item(key = date) {
                            NotificationHeader(date)
                        }
                        items(dailyNotifications, key = { it.id }) { notification ->
                            NotificationItem(
                                notification = notification,
                                onClick = { viewModel.markAsRead(notification.id) }
                            )
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
            .background(Color(0xFF2D241E))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = luxuryText.secondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}

@Composable
fun NotificationItem(notification: Notification, onClick: () -> Unit) {
    val luxuryText = LocalLuxuryTextColors.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Icon Container
        Box(modifier = Modifier.size(48.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF3E2723), Color(0xFF1B1B1B))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (notification.title.contains("Quote")) Icons.Rounded.AutoAwesome else Icons.Rounded.Notifications,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(10.dp)
                        .background(Color(0xFFE65100), CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title,
                    color = luxuryText.body,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = formatRelativeTime(notification.timestamp),
                    color = luxuryText.secondary,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.message,
                color = luxuryText.secondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

fun formatRelativeTime(timestamp: Long): String {
    return DateUtils.getRelativeTimeSpanString(
        timestamp,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()
}

fun groupNotificationsByDate(notifications: List<Notification>): Map<String, List<Notification>> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = sdf.format(Date())
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, -1)
    val yesterday = sdf.format(calendar.time)

    return notifications.groupBy {
        val dateStr = sdf.format(Date(it.timestamp))
        when (dateStr) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> "Earlier"
        }
    }
}

// Simple clickable extension for convenience in NotificationScreen to avoid ripple
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
