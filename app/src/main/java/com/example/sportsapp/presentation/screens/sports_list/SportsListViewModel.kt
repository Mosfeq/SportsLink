package com.example.sportsapp.presentation.screens.sports_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.domain.use_cases.UseCases
import com.example.sportsapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.sql.Date
import javax.inject.Inject

@HiltViewModel
class SportsListViewModel @Inject constructor(
    private val useCases: UseCases
): ViewModel() {
    private var allEvents = listOf<SportEvent>()
    private var _sportsEventsList = MutableStateFlow<UiState<List<SportEvent>>>(
        UiState.Loading()
    )
    val sportsEventList = _sportsEventsList.asStateFlow()

    private var _addSportEvent = MutableSharedFlow<UiState<String>>()
    val addSportEvent = _addSportEvent.asSharedFlow()

    private var _joinSportEvent = MutableSharedFlow<UiState<String>>()
    val joinSportEvent = _joinSportEvent.asSharedFlow()

    private var activeExperienceFilter: String? = null
    private var activeSportFilter: String? = null
    private var activeDateFilter: Date? = null

    init {
        getSportsEventsList()
    }

    private fun getSportsEventsList(){
        useCases.getSportEventList{ response ->
            viewModelScope.launch {
                when (response){
                    is UiState.Success ->{
                        response.data?.let { it ->
                            allEvents = it
                            _sportsEventsList.emit(UiState.Success(it))
                        }
                    }
                    is UiState.Error -> {
                        response.errorMessage?.let { it ->
                            _sportsEventsList.emit(UiState.Error(it))
                        }
                    }
                    is UiState.Loading -> _sportsEventsList.emit(UiState.Loading())
                }
            }
        }
    }

    fun addSportEvent(sportEvent: SportEvent){
        useCases.addSportEvent(sportEvent){ response ->
            viewModelScope.launch (Dispatchers.IO) {
                _addSportEvent.emit(response)
            }
        }
    }

    fun joinSportEvent(sportEvent: SportEvent){
        useCases.joinSportEventUseCase(sportEvent){ response ->
            viewModelScope.launch (Dispatchers.IO) {
                _joinSportEvent.emit(response)
            }
        }
    }

    fun removeSportEvent(sportEventTitle: String){
        useCases.removeSportEvent(sportEventTitle)
    }

    fun setExperienceFilter(experienceLevel: String){
        activeExperienceFilter = experienceLevel
        applyFilters()
    }

    fun setSportsFilter(sport: String){
        activeSportFilter = sport
        applyFilters()
    }

    fun setDateFilter(date: Date){
        activeDateFilter = date
        applyFilters()
    }

    fun clearExperienceFilter() {
        activeExperienceFilter = null
        applyFilters()
    }

    fun clearSportFilter() {
        activeSportFilter = null
        applyFilters()
    }

    fun clearDateFilter(){
        activeDateFilter = null
        applyFilters()
    }

    private fun applyFilters(){
        viewModelScope.launch {
            var filteredList = allEvents

            activeExperienceFilter?.let { experience ->
                filteredList = filteredList.filter { event ->
                    event.experience == experience
                }
            }

            activeSportFilter?.let { sport ->
                filteredList = filteredList.filter { event ->
                    event.sports == sport
                }
            }

            activeDateFilter?.let { filterDate ->
                filteredList = filteredList.filter { event ->
                    val eventCalendar = java.util.Calendar.getInstance().apply {
                        time = Date(event.date)
                    }
                    val filterCalendar = java.util.Calendar.getInstance().apply {
                        time = filterDate
                    }
                    eventCalendar.get(java.util.Calendar.YEAR) == filterCalendar.get(java.util.Calendar.YEAR) &&
                            eventCalendar.get(java.util.Calendar.MONTH) == filterCalendar.get(java.util.Calendar.MONTH) &&
                            eventCalendar.get(java.util.Calendar.DAY_OF_MONTH) == filterCalendar.get(java.util.Calendar.DAY_OF_MONTH)
                }
            }
            _sportsEventsList.emit(UiState.Success(filteredList))
        }
    }

}