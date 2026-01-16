package com.example.sportsapp.domain.model

data class User(
    val name: String = "",
    val email: String = "",
    val hostedEvents: List<SportEvent> = emptyList(),
    val joinedEvents: List<SportEvent> = emptyList()
)