package com.example.sportsapp.presentation.screens.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportsapp.domain.use_cases.UseCases
import com.example.sportsapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val useCases: UseCases
): ViewModel() {

    private val _state = MutableStateFlow(RegisterViewState())
    val state: StateFlow<RegisterViewState> = _state.asStateFlow()

    private val _effect = Channel< RegisterEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: RegisterIntent){
        when (intent){
            is RegisterIntent.RegisterClicked -> register(intent.name, intent.email, intent.password)
            is RegisterIntent.UpdateEmail -> updateEmail(intent.email)
            is RegisterIntent.UpdatePassword -> updatePassword(intent.password)
            is RegisterIntent.UpdateName -> updateName(intent.name)
            is RegisterIntent.SignInBtnClicked -> toSignInScreen()
        }
    }

    private fun updateEmail(email: String){
        _state.update { it.copy(email = email) }
    }

    private fun updatePassword(password: String){
        _state.update { it.copy(password = password) }
    }

    private fun updateName(name: String){
        _state.update { it.copy(name = name) }
    }

    private fun toSignInScreen(){
        viewModelScope.launch {
            _effect.send(RegisterEffect.NavigateToSignInScreen)
        }
    }

    private fun register(name: String, email: String, password: String){
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _effect.send(RegisterEffect.ShowToast("Missing Fields"))
            }
            return
        }
        useCases.register(name, email, password){ result ->
            viewModelScope.launch {
                when(result){
                    is UiState.Success -> {
                        _effect.send(RegisterEffect.NavigateToSportListScreen)
                        _effect.send(RegisterEffect.ShowToast(result.data.toString()))
                    }
                    is UiState.Error -> {
                        _effect.send(RegisterEffect.ShowToast(result.errorMessage.toString()))
                    }
                    is UiState.Loading -> {}
                }
            }
        }
    }

}