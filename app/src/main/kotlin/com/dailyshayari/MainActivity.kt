package com.dailyshayari

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dailyshayari.ui.theme.ShayariTheme

sealed class Screen {
    object Home : Screen()
    object Explore : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShayariTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
                when (currentScreen) {
                    is Screen.Home -> HomeScreen(
                        currentScreen = currentScreen,
                        navigateTo = { screen -> currentScreen = screen }
                    )
                    is Screen.Explore -> ExploreScreen(
                        currentScreen = currentScreen,
                        navigateTo = { screen -> currentScreen = screen }
                    )
                }
            }
        }
    }
}

@Composable
fun AppBottomBar(currentScreen: Screen, navigateTo: (Screen) -> Unit) {
    BottomAppBar(containerColor = MaterialTheme.colorScheme.background) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavigationItem(
                icon = Icons.Rounded.Home,
                label = "Home",
                selected = currentScreen is Screen.Home,
                onClick = { navigateTo(Screen.Home) }
            )
            BottomNavigationItem(
                icon = Icons.Rounded.Search,
                label = "Explore",
                selected = currentScreen is Screen.Explore,
                onClick = { navigateTo(Screen.Explore) }
            )
            BottomNavigationItem(icon = Icons.Rounded.Favorite, label = "Create", selected = false, onClick = {})
            BottomNavigationItem(icon = Icons.Rounded.Search, label = "Search", selected = false, onClick = {})
            BottomNavigationItem(icon = Icons.Rounded.Person, label = "Profile", selected = false, onClick = {})
        }
    }
}
