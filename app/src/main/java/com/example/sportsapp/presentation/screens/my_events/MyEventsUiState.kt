package com.example.sportsapp.presentation.screens.my_events

import com.example.sportsapp.domain.model.SportEvent

data class MyEventsViewState(
    val isLoading: Boolean = false,
    val displayedEvents: List<SportEvent> = emptyList(),
    val isShowingHosted: Boolean = true,
    val errorMessage: String? = null
)

sealed class MyEventsIntent {
    data object LoadEvents: MyEventsIntent()
    data object ToggleEventType: MyEventsIntent()
    data object SwitchToHosted: MyEventsIntent()
    data object SwitchToJoined: MyEventsIntent()
    data class OnEventClick(val event: SportEvent): MyEventsIntent()
    data class OnRemoveClick(val event: SportEvent): MyEventsIntent()
}

sealed class MyEventsEffect {
    data class ShowToast(val message: String): MyEventsEffect()
    data class NavigateToEventDetail(val eventId: String): MyEventsEffect()
}