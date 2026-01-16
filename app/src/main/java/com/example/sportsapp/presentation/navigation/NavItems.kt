package com.example.sportsapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItems(
    val label: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val route: String
)

val listOfNavItems = listOf(
    NavItems(
        label = "Events",
        unselectedIcon = Icons.Outlined.Place,
        selectedIcon = Icons.Filled.Place,
        route = Screens.SportsListScreen.route
    ),
    NavItems(
        label = "My Events",
        unselectedIcon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        route = Screens.MyEventsScreen.route
    )
)