package com.example.sportsapp.domain.repository

import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.domain.model.User
import com.example.sportsapp.util.UiState

interface SportEventRepository {

    fun register (name: String, email: String, password: String, result: (UiState<String>) -> Unit)
    fun signIn (email: String, password: String, result: (UiState<String>) -> Unit)

    fun getCurrentUser(result: (UiState<User>) -> Unit)
    fun getSportsEventsList(result: (UiState<List<SportEvent>>) -> Unit)

    fun getHostedSportsEventsList(result: (UiState<List<SportEvent>>) -> Unit)

    fun getJoinedSportsEventsList(result: (UiState<List<SportEvent>>) -> Unit)

    fun addSportEvent (sportEvent: SportEvent, response: (UiState<String>) -> Unit)

    fun removeSportEvent (sportEventTitle: String)

    fun joinSportEvent (sportEvent: SportEvent, response: (UiState<String>) -> Unit)

    fun leaveSportEvent (sportEvent: SportEvent, response: (UiState<String>) -> Unit)

}