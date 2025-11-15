package com.example.sportsapp.util

sealed class UiState<T> (val data: T? = null, val errorMessage: String? = null){
    class Success<T> (data: T): UiState<T>(data)
    class Error<T>(errorMessage: String): UiState<T>(errorMessage = errorMessage)
    class Loading<T>: UiState<T>()
}
