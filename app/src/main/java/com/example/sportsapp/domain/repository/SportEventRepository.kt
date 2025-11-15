package com.example.sportsapp.domain.repository

import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.util.UiState

interface SportEventRepository {

    fun getSportsEventsList(result: (UiState<List<SportEvent>>) -> Unit)

    fun getJoinedSportsEventsList(result: (UiState<List<SportEvent>>) -> Unit)

    fun addSportEvent (sportEvent: SportEvent, response: (UiState<String>) -> Unit)

    fun removeSportEvent (sportEventTitle: String)

    fun joinSportEvent (sportEvent: SportEvent, response: (UiState<String>) -> Unit)

}