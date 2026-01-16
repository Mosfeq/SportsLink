package com.example.sportsapp.domain.use_cases

import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.domain.repository.SportEventRepository
import com.example.sportsapp.util.UiState
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: SportEventRepository
) {

    operator fun invoke(email: String, password: String, result: (UiState<String>) -> Unit){
        repository.signIn(email, password){
            result.invoke(it)
        }
    }

}