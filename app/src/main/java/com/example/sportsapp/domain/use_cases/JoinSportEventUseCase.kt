package com.example.sportsapp.domain.use_cases

import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.domain.repository.SportEventRepository
import com.example.sportsapp.util.UiState
import javax.inject.Inject

class JoinSportEventUseCase @Inject constructor(
    private val repository: SportEventRepository
) {

    operator fun invoke(sportEvent: SportEvent, response: (UiState<String>) -> Unit){
        repository.joinSportEvent(sportEvent){
            response.invoke(it)
        }
    }

}