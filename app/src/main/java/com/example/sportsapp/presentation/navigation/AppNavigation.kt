package com.example.sportsapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sportsapp.presentation.screens.auth.register.RegisterScreen
import com.example.sportsapp.presentation.screens.event_detail.EventDetailScreen
import com.example.sportsapp.presentation.screens.my_events.MyEventsScreen
import com.example.sportsapp.presentation.screens.auth.sign_in.SignInScreen
import com.example.sportsapp.presentation.screens.sports_list.SportsListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = Color(0xFFF2F2F7),
        topBar = {
            if (currentRoute == Screens.EventDetailScreen.route){
                TopAppBar(
                    title = { Text(text = "Event Detail")},
                    navigationIcon = {
                        IconButton(onClick = {navController.popBackStack()}) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (currentRoute != Screens.SignInScreen.route &&
                currentRoute != Screens.EventDetailScreen.route &&
                currentRoute != Screens.RegisterScreen.route)
            {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    listOfNavItems.forEach { navItem ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true

                        NavigationBarItem(
                            label = {
                                Text(
                                    text = navItem.label,
                                    fontSize = if (isSelected) 13.sp else 11.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) navItem.selectedIcon else navItem.unselectedIcon,
                                    contentDescription = navItem.label,
                                    modifier = Modifier
                                        .size(if (isSelected) 30.dp else 22.dp)
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(navItem.route){
                                    popUpTo(navController.graph.findStartDestination().id){
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF007AFF),
                                selectedTextColor = Color(0xFF007AFF),
                                unselectedIconColor = Color(0xFF8E8E93),
                                unselectedTextColor = Color(0xFF8E8E93),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.SportsListScreen.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = Screens.RegisterScreen.route){
                RegisterScreen(navController)
            }
            composable(route = Screens.SignInScreen.route){
                SignInScreen(navController)
            }
            composable(route = Screens.SportsListScreen.route,){
                SportsListScreen(navController)
            }
            composable(
                route = Screens.MyEventsScreen.route,
            ){
                MyEventsScreen(navController)
            }
            composable(
                route = Screens.EventDetailScreen.route,
                arguments = listOf(
                    navArgument("eventId"){
                        type = NavType.StringType
                    }
                )
            ){
                val eventId = it.arguments?.getString("eventId") ?: ""
                EventDetailScreen(eventId)
            }
        }
    }

}