package com.example.sportsapp.domain.use_cases

import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.domain.repository.SportEventRepository
import com.example.sportsapp.util.UiState
import javax.inject.Inject

class GetJoinedSportEventListUseCase @Inject constructor(
    private val repository: SportEventRepository
) {

    operator fun invoke(result: (UiState<List<SportEvent>>) -> Unit){
        repository.getJoinedSportsEventsList {
            result.invoke(it)
        }
    }

}