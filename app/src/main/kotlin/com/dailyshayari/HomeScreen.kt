
package com.dailyshayari

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyshayari.ui.theme.Background
import com.dailyshayari.ui.theme.DailyShayariTheme
import com.dailyshayari.ui.theme.PrimaryAccent
import com.dailyshayari.ui.theme.SecondarySurface
import com.dailyshayari.ui.theme.TextPrimary
import com.dailyshayari.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shayari Vibes", color = TextPrimary) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Logo",
                        tint = PrimaryAccent
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Background
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    BottomNavigationItem(Icons.Default.Home, "Home", true)
                    BottomNavigationItem(Icons.Default.Search, "Explore")
                    BottomNavigationItem(Icons.Default.Add, "Create")
                    BottomNavigationItem(Icons.Default.Search, "Search")
                    BottomNavigationItem(Icons.Default.Person, "Profile")
                }
            }
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Search Bar
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search saved shayari...", color = TextSecondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = SecondarySurface,
                    focusedContainerColor = SecondarySurface,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = PrimaryAccent
                )
            )

            // Today's Special
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Today's Special",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Offline Ready",
                        tint = PrimaryAccent
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Offline Ready", color = PrimaryAccent, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Horizontal Pager for Today's Special
            // TODO: Replace with a HorizontalPager
            ShayariCard()

            // Quick Collections
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Quick Collections",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            QuickCollectionItem("Good Morning", "124 Messages • Ready Offline", Icons.Default.Star)
            Spacer(modifier = Modifier.height(8.dp))
            QuickCollectionItem("Festival Greetings", "85 Messages • Ready Offline", Icons.Default.Star)


            // Browse Categories
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Browse Categories",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text("All Available Offline", color = TextSecondary, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            CategoryGrid()
        }
    }
}

@Composable
fun BottomNavigationItem(icon: ImageVector, label: String, selected: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) PrimaryAccent else TextSecondary
        )
        Text(label, color = if (selected) PrimaryAccent else TextSecondary, fontSize = 12.sp)
    }
}

@Composable
fun ShayariCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6A4ADC) // Placeholder color
        )
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(
                    text = "\"",
                    fontSize = 48.sp,
                    color = TextPrimary.copy(alpha = 0.5f)
                )
                Text(
                    "Ishq ki rahon mein khud ko khona bhi ek manzil hai...",
                    fontSize = 18.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Text(
                    "— Gulzar",
                    fontSize = 14.sp,
                    color = TextPrimary.copy(alpha = 0.8f),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionButton(Icons.Default.Favorite, "1.2k")
                    ActionButton(Icons.Default.Share, "Share")
                    ActionButton(Icons.Default.ThumbUp, "WhatsApp")
                    ActionButton(Icons.Default.Done, "Save")
                }
            }
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = TextPrimary
        )
        Text(text, color = TextPrimary, fontSize = 12.sp)
    }
}

@Composable
fun QuickCollectionItem(title: String, subtitle: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = SecondarySurface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp),
                tint = PrimaryAccent
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(subtitle, fontSize = 12.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Arrow",
                tint = TextSecondary
            )
        }
    }
}

@Composable
fun CategoryGrid() {
    val categories = listOf(
        "Love Shayari" to Icons.Default.Favorite,
        "Sad Quotes" to Icons.Default.Warning,
        "Inspiration" to Icons.Default.Info,
        "Friendship" to Icons.Default.Person,
        "Good Night" to Icons.Default.Info,
        "Attitude" to Icons.Default.Star
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories.size) { index ->
            val (name, icon) = categories[index]
            CategoryCard(name, icon)
        }
    }
}

@Composable
fun CategoryCard(name: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(28.dp)),
        colors = CardDefaults.cardColors(
            containerColor = SecondarySurface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, color = TextPrimary, fontSize = 14.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DailyShayariTheme {
        HomeScreen()
    }
}
