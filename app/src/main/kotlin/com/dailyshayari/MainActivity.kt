
@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.dailyshayari

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.dailyshayari.ui.theme.ShayariTheme
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Explore : Screen()
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
    val screens = listOf(Screen.Home, Screen.Explore)
    val pagerState = rememberPagerState(pageCount = { screens.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            if (pagerState.currentPage == 0) {
                TopAppBar(
                    title = { Text("Shayari Vibes", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary) },
                    navigationIcon = { AppLogo() },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        },
        bottomBar = {
            AppBottomBar(
                pagerState = pagerState,
                onNavigate = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                }
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (screens[page]) {
                is Screen.Home -> HomeScreen()
                is Screen.Explore -> ExploreScreen()
            }
        }
    }
}


@Composable
fun AppBottomBar(pagerState: PagerState, onNavigate: (Int) -> Unit) {
    BottomAppBar(containerColor = MaterialTheme.colorScheme.background) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavigationItem(
                icon = Icons.Rounded.Home,
                label = "Home",
                selected = pagerState.currentPage == 0,
                onClick = { onNavigate(0) }
            )
            BottomNavigationItem(
                icon = Icons.Rounded.Search,
                label = "Explore",
                selected = pagerState.currentPage == 1,
                onClick = { onNavigate(1) }
            )
            BottomNavigationItem(icon = Icons.Rounded.Favorite, label = "Create", selected = false, onClick = {})
            BottomNavigationItem(icon = Icons.Rounded.Search, label = "Search", selected = false, onClick = {})
            BottomNavigationItem(icon = Icons.Rounded.Person, label = "Profile", selected = false, onClick = {})
        }
    }
}
