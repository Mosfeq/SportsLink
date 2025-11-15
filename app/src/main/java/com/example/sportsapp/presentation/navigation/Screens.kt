package com.example.sportsapp.presentation.navigation

sealed class Screens (val route: String) {
    object SportsListScreen: Screens("sports_list_screen")
    object MyEventsScreen: Screens("my_events_screen")
}