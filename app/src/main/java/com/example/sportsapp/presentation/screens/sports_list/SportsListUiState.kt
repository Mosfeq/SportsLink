package com.example.sportsapp.presentation.screens.sports_list

import com.example.sportsapp.domain.model.SportEvent
import java.sql.Date

data class SportsListUiState(
    val isLoading: Boolean = false,
    val events: List<SportEvent> = emptyList(),
    val errorMessage: String? = null,
    val filters: FilterState = FilterState(),
    val addEventDialog: AddEventDialogState = AddEventDialogState()
)

data class FilterState(
    val selectedExperience: String? = null,
    val selectedSport: String? = null,
    val selectedDate: Date? = null,
    val isExperienceDialogOpen: Boolean = false,
    val isSportDialogOpen: Boolean = false,
    val isDateDialogOpen: Boolean = false
)

data class AddEventDialogState(
    val isOpen: Boolean = false,
    val title: String = "",
    val sport: String? = null,
    val location: String = "",
    val date: Date? = null,
    val time: java.sql.Time? = null,
    val experience: String = "",
    val host: String = "",
    val isSportDropdownOpen: Boolean = false,
    val isExperienceDropdownOpen: Boolean = false,
    val isDatePickerOpen: Boolean = false,
    val isTimePickerOpen: Boolean = false
)

// User Intents
sealed class SportsListIntent {
    data object LoadEvents : SportsListIntent()
    data object RefreshEvents : SportsListIntent()

    data class SetExperienceFilter(val experience: String) : SportsListIntent()
    data class SetSportFilter(val sport: String) : SportsListIntent()
    data class SetDateFilter(val date: Date) : SportsListIntent()
    data object ClearExperienceFilter : SportsListIntent()
    data object ClearSportFilter : SportsListIntent()
    data object ClearDateFilter : SportsListIntent()
    data object OpenExperienceFilter : SportsListIntent()
    data object OpenSportFilter : SportsListIntent()
    data object OpenDateFilter : SportsListIntent()
    data object CloseExperienceFilter : SportsListIntent()
    data object CloseSportFilter : SportsListIntent()
    data object CloseDateFilter : SportsListIntent()

    data object OpenAddEventDialog : SportsListIntent()
    data object CloseAddEventDialog : SportsListIntent()
    data class UpdateEventTitle(val title: String) : SportsListIntent()
    data class UpdateEventSport(val sport: String) : SportsListIntent()
    data class UpdateEventLocation(val location: String) : SportsListIntent()
    data class UpdateEventDate(val date: Date) : SportsListIntent()
    data class UpdateEventTime(val time: java.sql.Time) : SportsListIntent()
    data class UpdateEventExperience(val experience: String) : SportsListIntent()
    data class UpdateEventHost(val host: String) : SportsListIntent()
    data object ToggleSportDropdown : SportsListIntent()
    data object ToggleExperienceDropdown : SportsListIntent()
    data object OpenDatePicker : SportsListIntent()
    data object CloseDatePicker : SportsListIntent()
    data object OpenTimePicker : SportsListIntent()
    data object CloseTimePicker : SportsListIntent()
    data object SubmitNewEvent : SportsListIntent()

    data class JoinEvent(val event: SportEvent) : SportsListIntent()
}

sealed class SportsListEffect {
    data class ShowToast(val message: String) : SportsListEffect()
    data class ShowError(val error: String) : SportsListEffect()
    data object EventAddedSuccessfully : SportsListEffect()
    data object EventJoinedSuccessfully : SportsListEffect()
}
