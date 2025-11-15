package com.example.sportsapp.domain.use_cases

import com.example.sportsapp.domain.repository.SportEventRepository
import javax.inject.Inject

class RemoveSportEventUseCase @Inject constructor(
    private val repository: SportEventRepository
){

    operator fun invoke(sportEventTitle: String){
        repository.removeSportEvent(sportEventTitle)
    }

}