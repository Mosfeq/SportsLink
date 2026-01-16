package com.example.sportsapp.domain.use_cases

import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.domain.repository.SportEventRepository
import com.example.sportsapp.util.UiState
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: SportEventRepository
) {

    operator fun invoke(name: String, email: String, password: String, result: (UiState<String>) -> Unit){
        repository.register(name, email, password){
            result.invoke(it)
        }
    }

}