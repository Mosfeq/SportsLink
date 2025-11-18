package com.example.sportsapp.presentation.screens.sports_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.domain.model.Sports
import com.example.sportsapp.domain.use_cases.UseCases
import com.example.sportsapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.sql.Date
import java.sql.Time
import javax.inject.Inject

@HiltViewModel
class SportsListViewModel @Inject constructor(
    private val useCases: UseCases
): ViewModel() {

    private val _state = MutableStateFlow(SportsListUiState())
    val state: StateFlow<SportsListUiState> = _state.asStateFlow()

    private val _effect = Channel<SportsListEffect>()
    val effect = _effect.receiveAsFlow()

    private var allEvents = listOf<SportEvent>()

    init {
        handleIntent(SportsListIntent.LoadEvents)
    }

    fun handleIntent(intent: SportsListIntent){
        when(intent){
            is SportsListIntent.LoadEvents -> getSportsEventsList()
            is SportsListIntent.RefreshEvents -> getSportsEventsList()

            is SportsListIntent.SetExperienceFilter -> setExperienceFilter(intent.experience)
            is SportsListIntent.SetSportFilter -> setSportsFilter(intent.sport)
            is SportsListIntent.SetDateFilter -> setDateFilter(intent.date)
            is SportsListIntent.SetLocationFilter -> {}
            is SportsListIntent.ClearExperienceFilter -> clearExperienceFilter()
            is SportsListIntent.ClearSportFilter -> clearSportFilter()
            is SportsListIntent.ClearDateFilter -> clearDateFilter()
            is SportsListIntent.ClearLocationFilter -> {}
            is SportsListIntent.ApplyExperienceFilter -> applyExperienceFilter()
            is SportsListIntent.ApplySportFilter -> applySportFilter()
            is SportsListIntent.ApplyDateFilter -> applyDateFilter()
            is SportsListIntent.ApplyLocationFilter -> {}

            is SportsListIntent.OpenExperienceFilter -> openExperienceFilter()
            is SportsListIntent.OpenSportFilter -> openSportFilter()
            is SportsListIntent.OpenDateFilter -> openDateFilter()
            is SportsListIntent.OpenLocationFilter -> {}
            is SportsListIntent.OpenFilterDatePicker -> openFilterDatePicker()

            is SportsListIntent.CloseExperienceFilter -> closeExperienceFilter()
            is SportsListIntent.CloseSportFilter -> closeSportFilter()
            is SportsListIntent.CloseDateFilter -> closeDateFilter()
            is SportsListIntent.CloseLocationFilter -> {}
            is SportsListIntent.CloseFilterDatePicker -> closeFilterDatePicker()

            is SportsListIntent.OpenAddEventDialog -> openAddEventDialog()
            is SportsListIntent.CloseAddEventDialog -> closeAddEventDialog()
            is SportsListIntent.UpdateEventTitle -> updateEventTitle(intent.title)
            is SportsListIntent.UpdateEventSport -> updateEventSport(intent.sport)
            is SportsListIntent.UpdateEventLocation -> updateEventLocation(intent.location)
            is SportsListIntent.UpdateEventDate -> updateEventDate(intent.date)
            is SportsListIntent.UpdateEventTime -> updateEventTime(intent.time)
            is SportsListIntent.UpdateEventExperience -> updateEventExperience(intent.experience)
            is SportsListIntent.UpdateEventHost -> updateEventHost(intent.host)
            is SportsListIntent.ToggleSportDropdown -> toggleSportDropdown()
            is SportsListIntent.ToggleExperienceDropdown -> toggleExperienceDropdown()
            is SportsListIntent.OpenDatePicker -> openDatePicker()
            is SportsListIntent.CloseDatePicker -> closeDatePicker()
            is SportsListIntent.OpenTimePicker -> openTimePicker()
            is SportsListIntent.CloseTimePicker -> closeTimePicker()
            is SportsListIntent.AddSportEvent -> addSportEvent()

            is SportsListIntent.JoinEvent -> joinSportEvent(intent.event)
        }
    }

    private fun getSportsEventsList(){
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        useCases.getSportEventList{ response ->
            viewModelScope.launch {
                when (response){
                    is UiState.Success ->{
                        response.data?.let { it ->
                            allEvents = it
                            applyFilters()
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

    private fun addSportEvent(){
        val dialog = _state.value.addEventDialog

        if (dialog.title.isEmpty() || dialog.sport == null || dialog.location.isEmpty() ||
            dialog.date == null || dialog.time == null || dialog.experience.isEmpty() ||
            dialog.host.isEmpty()) {
            viewModelScope.launch {
                _effect.send(SportsListEffect.ShowToast("Please fill in all fields"))
            }
            return
        }

        val sportEnum = try {
            Sports.valueOf(dialog.sport)
        } catch (e: Exception){
            Sports.entries.find { it.displayName == dialog.sport } ?: Sports.Football
        }

        val sportEvent = SportEvent(
            title = dialog.title,
            sports = sportEnum,
            location = dialog.location,
            date = dialog.date,
            time = dialog.time,
            experience = dialog.experience,
            host = dialog.host
        )

        useCases.addSportEvent(sportEvent){ response ->
            viewModelScope.launch (Dispatchers.IO) {
                when(response){
                    is UiState.Success -> {
                        _effect.send(SportsListEffect.EventAddedSuccessfully)
                        closeAddEventDialog()
                        getSportsEventsList()
                    }
                    is UiState.Error -> {
                        _effect.send(
                            SportsListEffect.ShowError(
                                response.errorMessage ?: "Failed to add event"
                            )
                        )
                    }
                    is UiState.Loading -> {}
                }
            }
        }
    }

    private fun joinSportEvent(sportEvent: SportEvent){
        useCases.joinSportEventUseCase(sportEvent) { response ->
            viewModelScope.launch(Dispatchers.IO) {
                when (response) {
                    is UiState.Success -> {
                        _effect.send(SportsListEffect.EventJoinedSuccessfully)
                        _effect.send(SportsListEffect.ShowToast(response.data ?: "Event joined"))
                    }
                    is UiState.Error -> {
                        _effect.send(
                            SportsListEffect.ShowError(
                                response.errorMessage ?: "Failed to join event"
                            )
                        )
                    }
                    is UiState.Loading -> {}
                }
            }
        }
    }


    fun setExperienceFilter(experienceLevel: String){
        _state.update {
            it.copy(
                filters = it.filters.copy(tempExperience = experienceLevel)
            )
        }
    }

    fun setSportsFilter(sport: String){
        _state.update {
            it.copy(
                filters = it.filters.copy(tempSport = sport)
            )
        }
    }

    fun setDateFilter(date: Date){
        _state.update {
            it.copy(
                filters = it.filters.copy(tempDate = date)
            )
        }
    }

    fun clearExperienceFilter() {
        _state.update {
            it.copy(
                filters = it.filters.copy(
                    selectedExperience = null,
                    tempExperience = null
                )
            )
        }
        applyFilters()
    }

    private fun clearSportFilter() {
        _state.update {
            it.copy(
                filters = it.filters.copy(
                    selectedSport = null,
                    tempSport = null
                )
            )
        }
        applyFilters()
    }

    private fun clearDateFilter() {
        _state.update {
            it.copy(
                filters = it.filters.copy(
                    selectedDate = null,
                    tempDate = null
                )
            )
        }
        applyFilters()
    }

    private fun applyExperienceFilter(){
        _state.update {
            it.copy(
                filters = it.filters.copy(
                    selectedExperience = it.filters.tempExperience
                )
            )
        }
        applyFilters()
    }

    private fun applySportFilter(){
        _state.update {
            it.copy(
                filters = it.filters.copy(
                    selectedSport = it.filters.tempSport
                )
            )
        }
        applyFilters()
    }

    private fun applyDateFilter(){
        _state.update {
            it.copy(
                filters = it.filters.copy(
                    selectedDate = it.filters.tempDate
                )
            )
        }
        applyFilters()
    }

    private fun openExperienceFilter() {
        _state.update {
            it.copy(
                filters = it.filters.copy(
                    isExperienceDialogOpen = true,
                    tempExperience = it.filters.selectedExperience
                )
            )
        }
    }

    private fun openSportFilter() {
        _state.update {
            it.copy(
                filters = it.filters.copy(
                    isSportDialogOpen = true,
                    tempSport = it.filters.selectedSport
                )
            )
        }
    }

    private fun openDateFilter() {
        _state.update {
            it.copy(
                filters = it.filters.copy(
                    isDateDialogOpen = true,
                    isFilterDatePickerOpen = false,
                    tempDate = it.filters.selectedDate
                )
            )
        }
    }

    private fun openFilterDatePicker(){
        _state.update {
            it.copy(
                filters = it.filters.copy(isFilterDatePickerOpen = true)
            )
        }
    }

    private fun closeExperienceFilter() {
        _state.update {
            it.copy(
                filters = it.filters.copy(isExperienceDialogOpen = false)
            )
        }
    }

    private fun closeSportFilter() {
        _state.update {
            it.copy(
                filters = it.filters.copy(isSportDialogOpen = false)
            )
        }
    }

    private fun closeDateFilter() {
        _state.update {
            it.copy(
                filters = it.filters.copy(
                    isDateDialogOpen = false,
                    isFilterDatePickerOpen = false
                )
            )
        }
    }

    private fun closeFilterDatePicker() {
        _state.update {
            it.copy(
                filters = it.filters.copy(isFilterDatePickerOpen = false)
            )
        }
    }

    private fun applyFilters(){
        val currentFilters = _state.value.filters
        var filteredList = allEvents

        currentFilters.selectedExperience?.let { experience ->
            filteredList = filteredList.filter { it.experience == experience }
        }

        currentFilters.selectedSport?.let { sport ->
            filteredList = filteredList.filter { it.sports == sport }
        }

        currentFilters.selectedDate?.let { filterDate ->
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

        _state.update {
            it.copy(
                isLoading = false,
                events = filteredList,
                errorMessage = null
            )
        }
    }

    private fun openAddEventDialog() {
        _state.update {
            it.copy(
                addEventDialog = AddEventDialogState(isOpen = true)
            )
        }
    }

    private fun closeAddEventDialog() {
        _state.update {
            it.copy(
                addEventDialog = AddEventDialogState(isOpen = false)
            )
        }
    }

    private fun updateEventTitle(title: String) {
        _state.update {
            it.copy(
                addEventDialog = it.addEventDialog.copy(title = title)
            )
        }
    }

    private fun updateEventSport(sport: String) {
        _state.update {
            it.copy(
                addEventDialog = it.addEventDialog.copy(sport = sport)
            )
        }
    }

    private fun updateEventLocation(location: String) {
        _state.update {
            it.copy(
                addEventDialog = it.addEventDialog.copy(location = location)
            )
        }
    }

    private fun updateEventDate(date: Date) {
        _state.update {
            it.copy(
                addEventDialog = it.addEventDialog.copy(date = date)
            )
        }
    }

    private fun updateEventTime(time: Time) {
        _state.update {
            it.copy(
                addEventDialog = it.addEventDialog.copy(time = time)
            )
        }
    }

    private fun updateEventExperience(experience: String) {
        _state.update {
            it.copy(
                addEventDialog = it.addEventDialog.copy(experience = experience)
            )
        }
    }

    private fun updateEventHost(host: String) {
        _state.update {
            it.copy(
                addEventDialog = it.addEventDialog.copy(host = host)
            )
        }
    }

    private fun toggleSportDropdown() {
        _state.update {
            it.copy(
                addEventDialog = it.addEventDialog.copy(
                    isSportDropdownOpen = !it.addEventDialog.isSportDropdownOpen
                )
            )
        }
    }

    private fun toggleExperienceDropdown() {
        _state.update {
            it.copy(
                addEventDialog = it.addEventDialog.copy(
                    isExperienceDropdownOpen = !it.addEventDialog.isExperienceDropdownOpen
                )
            )
        }
    }

    private fun openDatePicker() {
        _state.update {
            it.copy(
                addEventDialog = it.addEventDialog.copy(isDatePickerOpen = true)
            )
        }
    }

    private fun closeDatePicker() {
        _state.update {
            it.copy(
                addEventDialog = it.addEventDialog.copy(isDatePickerOpen = false)
            )
        }
    }

    private fun openTimePicker() {
        _state.update {
            it.copy(
                addEventDialog = it.addEventDialog.copy(isTimePickerOpen = true)
            )
        }
    }

    private fun closeTimePicker() {
        _state.update {
            it.copy(
                addEventDialog = it.addEventDialog.copy(isTimePickerOpen = false)
            )
        }
    }

    private fun removeSportEvent(sportEventTitle: String){
        useCases.removeSportEvent(sportEventTitle)
    }

}