package com.example.sportsapp.data.repository

import com.example.sportsapp.data.remote.dao.FirebaseDao
import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.domain.repository.SportEventRepository
import com.example.sportsapp.util.UiState
import javax.inject.Inject

class SportEventRepositoryImplementation @Inject constructor(
    private val db: FirebaseDao
): SportEventRepository {

    override fun getSportsEventsList(result: (UiState<List<SportEvent>>) -> Unit) {
        db.getSportsEventsList {
            result.invoke(it)
        }
    }

    override fun getJoinedSportsEventsList(result: (UiState<List<SportEvent>>) -> Unit) {
        db.getJoinedEventsList {
            result.invoke(it)
        }
    }

    override fun addSportEvent(
        sportEvent: SportEvent,
        response: (UiState<String>) -> Unit
    ) {
        db.addSportEvent(sportEvent){
            response.invoke(it)
        }
    }

    override fun removeSportEvent(sportEventTitle: String) {
        db.removeSportEvent(sportEventTitle)
    }

    override fun joinSportEvent(sportEvent: SportEvent, response: (UiState<String>) -> Unit) {
        db.joinSportEvent(sportEvent){
            response.invoke(it)
        }
    }

}