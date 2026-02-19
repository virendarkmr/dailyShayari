package com.dailyshayari

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dailyshayari.ui.theme.ShayariTheme

sealed class Screen {
    object Home : Screen()
    object Explore : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

            ShayariTheme {
                when (currentScreen) {
                    is Screen.Home -> HomeScreen(navigateTo = { screen -> currentScreen = screen })
                    is Screen.Explore -> ExploreScreen()
                }
            }
        }
    }
}
