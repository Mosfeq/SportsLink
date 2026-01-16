package com.example.sportsapp.presentation.screens.my_events

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
class MyEventsViewModel @Inject constructor(
    private val useCases: UseCases
): ViewModel() {

    private val _state = MutableStateFlow(MyEventsViewState())
    val state: StateFlow<MyEventsViewState> = _state.asStateFlow()

    private val _effect = Channel<MyEventsEffect>()
    val effect = _effect.receiveAsFlow()

    private var hostedEvents: List<SportEvent> = emptyList()
    private var joinedEvents: List<SportEvent> = emptyList()

    init {
        handleIntent(MyEventsIntent.LoadEvents)
    }

    fun handleIntent(intent: MyEventsIntent){
        when (intent){
            is MyEventsIntent.LoadEvents -> loadEvents()
            is MyEventsIntent.ToggleEventType -> toggleEventType()
            is MyEventsIntent.SwitchToHosted -> switchToHosted()
            is MyEventsIntent.SwitchToJoined -> switchToJoined()
            is MyEventsIntent.OnEventClick -> onEventClick(intent.event)
        }
    }

    private fun loadEvents(){
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, errorMessage = null)
            }
            getHostedEventsList()
            getJoinedEventsList()
        }
    }

    fun getHostedEventsList(){
        useCases.getHostedSportEventList{ response ->
            viewModelScope.launch {
                when (response){
                    is UiState.Success ->{
                        response.data?.let { it ->
                            hostedEvents = it
                            updateDisplayedEvents()
                        }
                    }
                    is UiState.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = response.errorMessage
                            )
                        }
                    }
                    is UiState.Loading -> {}
                }
            }
        }
    }

    fun getJoinedEventsList(){
        useCases.getJoinedSportEventList{ response ->
            viewModelScope.launch {
                when (response){
                    is UiState.Success ->{
                        response.data?.let { it ->
                            joinedEvents = it
                            updateDisplayedEvents()
                        }
                    }
                    is UiState.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = response.errorMessage
                            )
                        }
                    }
                    is UiState.Loading -> {}
                }
            }
        }
    }

    private fun toggleEventType(){
        _state.update { currentState ->
            val isShowingHosted = !currentState.isShowingHosted
            currentState.copy(
                isShowingHosted = isShowingHosted,
                displayedEvents = if (isShowingHosted) hostedEvents else joinedEvents
            )
        }
    }

    private fun switchToHosted(){
        _state.update {
            it.copy(
                isShowingHosted = true,
                displayedEvents = hostedEvents
            )
        }
    }

    private fun switchToJoined(){
        _state.update {
            it.copy(
                isShowingHosted = false,
                displayedEvents = joinedEvents
            )
        }
    }

    private fun updateDisplayedEvents(){
        _state.update { currentState ->
            currentState.copy(
                isLoading = false,
                displayedEvents = if (currentState.isShowingHosted) hostedEvents else joinedEvents,
                errorMessage = null
            )
        }
    }

    private fun onEventClick(event: SportEvent){
        viewModelScope.launch {
            _effect.send(MyEventsEffect.NavigateToEventDetail(event.id))
        }
    }

}