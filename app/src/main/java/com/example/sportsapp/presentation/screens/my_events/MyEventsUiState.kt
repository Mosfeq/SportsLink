package com.example.sportsapp.presentation.screens.my_events

import com.example.sportsapp.domain.model.SportEvent

sealed class MyEventsUiState {
    data object Initial: MyEventsUiState()
    data object Loading: MyEventsUiState()
    data class Success(
        val hostedEvents: List<SportEvent>,
        val joinedEvents: List<SportEvent>,
        val isShowingHosted: Boolean
    ): MyEventsUiState()
    data class Error(val message: String): MyEventsUiState()
}

sealed class MyEventsIntent {
    data object LoadEvents: MyEventsIntent()
    data object ToggleEventType: MyEventsIntent()
    data object SwitchToHosted: MyEventsIntent()
    data object SwitchToJoined: MyEventsIntent()
    data class OnEventClick(val event: SportEvent): MyEventsIntent()
}

sealed class MyEventsEffect {
    data class ShowToast(val message: String): MyEventsEffect()
    data class NavigateToEventDetail(val event: SportEvent): MyEventsEffect()
}

data class MyEventsViewState(
    val isLoading: Boolean = false,
    val displayedEvents: List<SportEvent> = emptyList(),
    val isShowingHosted: Boolean = true,
    val errorMessage: String? = null
)