package com.example.sportsapp.presentation.screens.auth.sign_in

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
class SignInViewModel @Inject constructor(
    private val useCases: UseCases
): ViewModel() {

    private val _state = MutableStateFlow(SignInViewState())
    val state: StateFlow<SignInViewState> = _state.asStateFlow()

    private val _effect = Channel<SignInEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: SignInIntent){
        when (intent){
            is SignInIntent.SignInClicked -> signIn(intent.email, intent.password)
            is SignInIntent.UpdateEmail -> updateEmail(intent.email)
            is SignInIntent.UpdatePassword -> updatePassword(intent.password)
            is SignInIntent.ClearEmail -> clearEmail()
            is SignInIntent.ClearPassword -> clearPassword()
            is SignInIntent.RegisterBtnClicked -> toRegisterScreen()
        }
    }

    private fun clearEmail(){
        _state.update { it.copy(email = "") }
    }

    private fun clearPassword(){
        _state.update { it.copy(password = "") }
    }

    private fun updateEmail(email: String){
        _state.update { it.copy(email = email) }
    }

    private fun updatePassword(password: String){
        _state.update { it.copy(password = password) }
    }

    private fun toRegisterScreen(){
        viewModelScope.launch {
            _effect.send(SignInEffect.NavigateToRegisterScreen)
        }
    }

    private fun signIn(email: String, password: String){
        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _effect.send(SignInEffect.ShowToast("Enter email or password"))
            }
            return
        }
        useCases.signIn(email, password){ result ->
            viewModelScope.launch {
                when(result){
                    is UiState.Success -> {
                        _effect.send(SignInEffect.NavigateToSportListScreen(email))
                        _effect.send(SignInEffect.ShowToast(result.data.toString()))
                    }
                    is UiState.Error -> {
                        _effect.send(SignInEffect.ShowToast(result.errorMessage.toString()))
                    }
                    is UiState.Loading -> {}
                }
            }
        }
    }

}