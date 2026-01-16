package com.example.sportsapp.presentation.screens.auth.register
data class RegisterViewState(
    var name: String = "",
    var email: String = "",
    var password: String = ""
)

sealed class RegisterIntent{
    data class RegisterClicked (val name: String, val email: String, val password: String): RegisterIntent()
    data class UpdateName(val name: String): RegisterIntent()
    data class UpdateEmail(val email: String): RegisterIntent()
    data class UpdatePassword(val password: String): RegisterIntent()

    object SignInBtnClicked: RegisterIntent()
}

sealed class RegisterEffect {
    data class ShowToast(val message: String): RegisterEffect()
    object NavigateToSportListScreen: RegisterEffect()
    object NavigateToSignInScreen: RegisterEffect()
}