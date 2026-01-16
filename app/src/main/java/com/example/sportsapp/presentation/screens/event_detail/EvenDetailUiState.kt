package com.example.sportsapp.presentation.screens.event_detail

import com.example.sportsapp.domain.model.SportEvent

data class EventDetailViewState(
    val isLoading: Boolean = false,
    val event: SportEvent? = null,
    val errorMessage: String? = null
)

sealed class EventDetailIntent{
    data class LoadEvent(val eventId: String) : EventDetailIntent()
    data class OnJoinEventClick(val event: SportEvent) : EventDetailIntent()
}

sealed class EventDetailEffect(){
    data class ShowToast(val message: String): EventDetailEffect()
}
