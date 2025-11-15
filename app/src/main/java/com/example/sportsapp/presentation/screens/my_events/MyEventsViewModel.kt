package com.example.sportsapp.presentation.screens.my_events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.domain.use_cases.UseCases
import com.example.sportsapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyEventsViewModel @Inject constructor(
    private val useCases: UseCases
): ViewModel() {

    private var _hostedSportEventsList = MutableStateFlow<UiState<List<SportEvent>>>(
        UiState.Loading()
    )
    val hostedSportEventList = _hostedSportEventsList.asStateFlow()

    private var _joinedSportEventList = MutableStateFlow<UiState<List<SportEvent>>>(
        UiState.Loading()
    )
    val joinedSportEventList = _joinedSportEventList.asStateFlow()

    init {
        getHostedEventsList()
        getJoinedEventsList()
    }

    fun getHostedEventsList(){
        useCases.getSportEventList{ response ->
            viewModelScope.launch {
                when (response){
                    is UiState.Success ->{
                        response.data?.let { it ->
                            var filteredList = it
                            filteredList = filteredList.filter { event ->
                                event.host == "Josh"
                            }
                            _hostedSportEventsList.emit(UiState.Success(filteredList))
                        }
                    }
                    is UiState.Error -> {
                        response.errorMessage?.let { it ->
                            _hostedSportEventsList.emit(UiState.Error(it))
                        }
                    }
                    is UiState.Loading -> _hostedSportEventsList.emit(UiState.Loading())
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
                            _joinedSportEventList.emit(UiState.Success(it))
                        }
                    }
                    is UiState.Error -> {
                        response.errorMessage?.let { it ->
                            _joinedSportEventList.emit(UiState.Error(it))
                        }
                    }
                    is UiState.Loading -> _joinedSportEventList.emit(UiState.Loading())
                }
            }
        }
    }

}