package com.example.sportsapp.domain.use_cases

import com.example.sportsapp.domain.model.User
import com.example.sportsapp.domain.repository.SportEventRepository
import com.example.sportsapp.util.UiState
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: SportEventRepository
) {

    operator fun invoke(result: (UiState<User>) -> Unit){
        repository.getCurrentUser {
            result.invoke(it)
        }
    }

}