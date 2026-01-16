package com.example.sportsapp.presentation.screens.event_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportsapp.domain.model.SportEvent
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
class EventDetailViewModel @Inject constructor(
    private val useCases: UseCases
): ViewModel() {

    private val _state = MutableStateFlow(EventDetailViewState())
    val state: StateFlow<EventDetailViewState> = _state.asStateFlow()

    private val _effect = Channel<EventDetailEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: EventDetailIntent){
        when (intent){
            is EventDetailIntent.LoadEvent -> loadEvent(intent.eventId)
            is EventDetailIntent.OnJoinEventClick -> onJoinEventClicked(intent.event)
        }
    }

    private fun loadEvent(eventId: String){
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        useCases.getSportEventList { response ->
            viewModelScope.launch {
                when (response) {
                    is UiState.Success -> {
                        val event = response.data?.find { it.id == eventId }
                        if (event != null) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    event = event,
                                    errorMessage = null
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    event = null,
                                    errorMessage = "Event not found"
                                )
                            }
                        }
                    }
                    is UiState.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = response.errorMessage ?: "Failed to load event"
                            )
                        }
                    }
                    is UiState.Loading -> {}
                }
            }
        }
    }

    private fun onJoinEventClicked(sportEvent: SportEvent){
        useCases.joinSportEventUseCase(sportEvent){ response ->
            viewModelScope.launch {
                when (response){
                    is UiState.Success -> _effect.send(EventDetailEffect.ShowToast(response.data.toString()))
                    is UiState.Error -> _effect.send(EventDetailEffect.ShowToast(response.errorMessage.toString()))
                    is UiState.Loading -> {}
                }
            }
        }
    }

}