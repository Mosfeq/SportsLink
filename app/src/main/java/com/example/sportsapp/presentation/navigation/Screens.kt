package com.example.sportsapp.presentation.navigation

sealed class Screens (val route: String) {

    object RegisterScreen: Screens("register_screen"){
        fun createRoute() = "register_screen"
    }
    object SignInScreen: Screens("sign_in_screen"){
        fun createRoute() = "sign_in_screen"
    }
    object SportsListScreen: Screens("sports_list_screen"){
        fun createRoute() = "sports_list_screen"
    }
    object MyEventsScreen: Screens("my_events_screen")
    object EventDetailScreen: Screens("event_detail_screen/{eventId}"){
        fun createRoute(eventId: String) = "event_detail_screen/$eventId"
    }
}