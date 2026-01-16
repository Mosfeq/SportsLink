package com.example.sportsapp.presentation.screens.auth.sign_in

import com.example.sportsapp.domain.model.User

data class SignInViewState(
    var email: String = "",
    var password: String = ""
)

sealed class SignInIntent{
    data class SignInClicked (val email: String, val password: String): SignInIntent()
    data class UpdateEmail(val email: String): SignInIntent()
    data class UpdatePassword(val password: String): SignInIntent()
    object ClearEmail: SignInIntent()
    object ClearPassword: SignInIntent()
    object RegisterBtnClicked: SignInIntent()
}

sealed class SignInEffect {
    data class ShowToast(val message: String): SignInEffect()
    data class NavigateToSportListScreen(val userEmail: String): SignInEffect()
    object NavigateToRegisterScreen: SignInEffect()
}