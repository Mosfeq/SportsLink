package com.example.sportsapp.data.repository

import com.example.sportsapp.data.remote.dao.FirebaseDao
import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.domain.model.User
import com.example.sportsapp.domain.repository.SportEventRepository
import com.example.sportsapp.util.UiState
import javax.inject.Inject

class SportEventRepositoryImplementation @Inject constructor(
    private val db: FirebaseDao
): SportEventRepository {

    override fun register(name: String, email: String, password: String, result: (UiState<String>) -> Unit) {
        db.register(name, email, password){
            result.invoke(it)
        }
    }

    override fun signIn(email: String, password: String, result: (UiState<String>) -> Unit) {
        db.signIn(email, password){
            result.invoke(it)
        }
    }

    override fun getCurrentUser(result: (UiState<User>) -> Unit) {
        db.getCurrentUser {
            result.invoke(it)
        }
    }

    override fun getSportsEventsList(result: (UiState<List<SportEvent>>) -> Unit) {
        db.getSportsEventsList {
            result.invoke(it)
        }
    }

    override fun getHostedSportsEventsList(result: (UiState<List<SportEvent>>) -> Unit) {
        db.getHostedEventsList {
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

    override fun leaveSportEvent(sportEvent: SportEvent, response: (UiState<String>) -> Unit) {
        db.leaveSportEvent(sportEvent){
            response.invoke(it)
        }
    }

}