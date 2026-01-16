package com.example.sportsapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.sportsapp.presentation.navigation.AppNavigation
import com.example.sportsapp.ui.theme.SportsAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var darkMode by mutableStateOf(false)

        setContent {
            SportsAppTheme(darkTheme = darkMode) {
                AppNavigation()
            }
        }
    }
}