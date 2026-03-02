
@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.dailyshayari

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dailyshayari.ui.theme.ShayariTheme
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Explore : Screen()
    object Create : Screen()
    object Settings : Screen()
    object Favorites : Screen()
    object Search : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShayariTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val screens = listOf(Screen.Home, Screen.Explore, Screen.Create, Screen.Settings)
    val pagerState = rememberPagerState(pageCount = { screens.size })
    val coroutineScope = rememberCoroutineScope()
    val selectedCategory = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    // State for double back button press
    var backPressedTime = remember { mutableStateOf(0L) }
    val backPressInterval = 2000L // 2 seconds interval

    val onNavigate: (Int, String?) -> Unit = { pageIndex, category ->
        if (category != null) {
            selectedCategory.value = category
        }
        coroutineScope.launch {
            pagerState.animateScrollToPage(pageIndex)
        }
    }

    // Handle back button press
    BackHandler {
        if (currentScreen is Screen.Favorites || currentScreen is Screen.Search) {
            currentScreen = Screen.Settings
        } else if (pagerState.currentPage != 0) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(0)
            }
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime.value <= backPressInterval) {
                // Second back press within 2 seconds - exit app
                (context as? ComponentActivity)?.finish()
            } else {
                // First back press - show toast message
                backPressedTime.value = currentTime
                Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState) { page ->
            when (val screen = screens[page]) {
                is Screen.Home -> HomeScreen(pagerState, onNavigate)
                is Screen.Explore -> ExploreScreen(pagerState, onNavigate, initialCategory = selectedCategory.value)
                is Screen.Create -> CreateScreen(onBackClick = { onNavigate(0, null) })
                is Screen.Settings -> SettingsScreen(
                    onNavigate = onNavigate, 
                    onFavoritesClick = { currentScreen = Screen.Favorites },
                    onSearchClick = { currentScreen = Screen.Search }
                )
                is Screen.Favorites -> {} 
                is Screen.Search -> {}
            }
        }

        if (currentScreen is Screen.Favorites) {
            FavoritesScreen(onBackClick = { currentScreen = Screen.Settings })
        }
        
        if (currentScreen is Screen.Search) {
            SearchScreen(onBackClick = { currentScreen = Screen.Settings })
        }
    }
}


@Composable
fun AppBottomBar(pagerState: PagerState, onNavigate: (Int, String?) -> Unit) {
    BottomAppBar(containerColor = MaterialTheme.colorScheme.background) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavigationItem(
                icon = Icons.Rounded.Home,
                label = "Home",
                selected = pagerState.currentPage == 0,
                onClick = { onNavigate(0, null) }
            )
            BottomNavigationItem(
                icon = Icons.Rounded.Search,
                label = "Explore",
                selected = pagerState.currentPage == 1,
                onClick = { onNavigate(1, null) }
            )
            BottomNavigationItem(
                icon = Icons.Rounded.Create,
                label = "Create",
                selected = pagerState.currentPage == 2,
                onClick = { onNavigate(2, null) }
            )
            BottomNavigationItem(
                icon = Icons.Rounded.Settings,
                label = "Settings",
                selected = pagerState.currentPage == 3,
                onClick = { onNavigate(3, null) }
            )
        }
    }
}
