
@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.dailyshayari

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dailyshayari.ui.theme.ShayariTheme
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Explore : Screen()
    object Create : Screen()
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
    val screens = listOf(Screen.Home, Screen.Explore, Screen.Create)
    val pagerState = rememberPagerState(pageCount = { screens.size })
    val coroutineScope = rememberCoroutineScope()
    val selectedCategory = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

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
        if (pagerState.currentPage != 0) {
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

    HorizontalPager(state = pagerState) { page ->
        when (screens[page]) {
            is Screen.Home -> HomeScreen(pagerState, onNavigate)
            is Screen.Explore -> ExploreScreen(pagerState, onNavigate, initialCategory = selectedCategory.value)
            is Screen.Create -> CreateScreen(onBackClick = { onNavigate(0, null) })
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
            BottomNavigationItem(icon = Icons.Rounded.Favorite, label = "Favorites", selected = false, onClick = {})
            BottomNavigationItem(icon = Icons.Rounded.Person, label = "Profile", selected = false, onClick = {})
        }
    }
}
