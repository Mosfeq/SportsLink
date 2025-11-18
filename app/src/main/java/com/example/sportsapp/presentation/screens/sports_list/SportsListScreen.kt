package com.example.sportsapp.presentation.screens.sports_list

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sportsapp.R
import com.example.sportsapp.domain.model.Sports
import com.example.sportsapp.presentation.screens.sports_list.components.ExperienceFilterItem
import com.example.sportsapp.presentation.screens.sports_list.components.FilterItem
import com.example.sportsapp.presentation.screens.sports_list.components.SportFilterItem
import com.example.sportsapp.presentation.screens.sports_list.components.SportsListItem
import kotlinx.coroutines.flow.collectLatest
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsListScreen(
    viewModel: SportsListViewModel = hiltViewModel()
){
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val dateFontSize = when {
        screenWidth < 360.dp -> 17.sp
        screenWidth < 400.dp -> 19.sp
        else -> 21.sp
    }

    val locationFontSize = when {
        screenWidth < 360.dp -> 13.sp
        else -> 15.sp
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    val dateFormatter = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault())
    val addDateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFedfffe))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ){
                if (state.addEventDialog.isDatePickerOpen || state.filters.isDateDialogOpen) {
                    DatePickerDialog(
                        onDismissRequest = {
                            if (state.addEventDialog.isDatePickerOpen) {
                                viewModel.handleIntent(SportsListIntent.CloseDatePicker)
                            } else {
                                viewModel.handleIntent(SportsListIntent.CloseDateFilter)
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        if (state.addEventDialog.isDatePickerOpen) {
                                            viewModel.handleIntent(
                                                SportsListIntent.UpdateEventDate(Date(millis))
                                            )
                                            viewModel.handleIntent(SportsListIntent.CloseDatePicker)
                                        } else {
                                            viewModel.handleIntent(
                                                SportsListIntent.SetDateFilter(Date(millis))
                                            )
                                            viewModel.handleIntent(SportsListIntent.CloseDateFilter)
                                        }
                                    }
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                if (state.addEventDialog.isDatePickerOpen) {
                                    viewModel.handleIntent(SportsListIntent.CloseDatePicker)
                                } else {
                                    viewModel.handleIntent(SportsListIntent.CloseDateFilter)
                                }
                            }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                if (state.addEventDialog.isTimePickerOpen) {
                    AlertDialog(
                        onDismissRequest = {
                            viewModel.handleIntent(SportsListIntent.CloseTimePicker)
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val hour = timePickerState.hour
                                    val minute = timePickerState.minute
                                    val time = Time.valueOf(
                                        String.format(Locale.getDefault(), "%02d:%02d:00", hour, minute)
                                    )
                                    viewModel.handleIntent(SportsListIntent.UpdateEventTime(time))
                                    viewModel.handleIntent(SportsListIntent.CloseTimePicker)
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                viewModel.handleIntent(SportsListIntent.CloseTimePicker)
                            }) {
                                Text("Cancel")
                            }
                        },
                        text = {
                            TimePicker(state = timePickerState)
                        }
                    )
                }
                if (state.addEventDialog.isOpen) {
                    AlertDialog(
                        onDismissRequest = {
                            viewModel.handleIntent(SportsListIntent.CloseAddEventDialog)
                        },
                        title = {
                            Text(
                                text = "Add Event",
                                color = Color.White
                            )
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = state.addEventDialog.title,
                                    onValueChange = {
                                        viewModel.handleIntent(SportsListIntent.UpdateEventTitle(it))
                                    },
                                    label = {
                                        Text("Event Title", color = Color(0xFFAAAAAA))
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF0A84FF),
                                        unfocusedBorderColor = Color(0xFF666666),
                                    )
                                )

                                Box {
                                    OutlinedTextField(
                                        value = state.addEventDialog.sport ?: "",
                                        onValueChange = {},
                                        label = { Text("Sport", color = Color(0xFFAAAAAA)) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.handleIntent(SportsListIntent.ToggleSportDropdown)
                                            },
                                        readOnly = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.White,
                                            disabledBorderColor = Color(0xFF666666),
                                            disabledLabelColor = Color(0xFFAAAAAA)
                                        ),
                                        enabled = false
                                    )
                                    DropdownMenu(
                                        expanded = state.addEventDialog.isSportDropdownOpen,
                                        onDismissRequest = {
                                            viewModel.handleIntent(SportsListIntent.ToggleSportDropdown)
                                        },
                                        modifier = Modifier.background(Color(0xFF3C3C3E))
                                    ) {
                                        Sports.entries.forEach { sport ->
                                            DropdownMenuItem(
                                                text = { Text(sport.displayName, color = Color.White) },
                                                onClick = {
                                                    viewModel.handleIntent(
                                                        SportsListIntent.UpdateEventSport(sport.displayName)
                                                    )
                                                    viewModel.handleIntent(SportsListIntent.ToggleSportDropdown)
                                                }
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = state.addEventDialog.location,
                                    onValueChange = {
                                        viewModel.handleIntent(SportsListIntent.UpdateEventLocation(it))
                                    },
                                    label = { Text("Location", color = Color(0xFFAAAAAA)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF0A84FF),
                                        unfocusedBorderColor = Color(0xFF666666),
                                    )
                                )

                                OutlinedTextField(
                                    value = state.addEventDialog.date?.let { addDateFormatter.format(it) } ?: "",
                                    onValueChange = {},
                                    label = { Text("Date", color = Color(0xFFAAAAAA)) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.handleIntent(SportsListIntent.OpenDatePicker)
                                        },
                                    readOnly = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = Color.White,
                                        disabledBorderColor = Color(0xFF666666),
                                        disabledLabelColor = Color(0xFFAAAAAA)
                                    ),
                                    enabled = false,
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.calendar),
                                            contentDescription = "Select date",
                                            modifier = Modifier.clickable {
                                                viewModel.handleIntent(SportsListIntent.OpenDatePicker)
                                            }
                                        )
                                    }
                                )

                                OutlinedTextField(
                                    value = state.addEventDialog.time?.let { timeFormatter.format(it) } ?: "",
                                    onValueChange = {},
                                    label = { Text("Time", color = Color(0xFFAAAAAA)) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.handleIntent(SportsListIntent.OpenTimePicker)
                                        },
                                    readOnly = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = Color.White,
                                        disabledBorderColor = Color(0xFF666666),
                                        disabledLabelColor = Color(0xFFAAAAAA)
                                    ),
                                    enabled = false,
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.calendar),
                                            contentDescription = "Select time",
                                            modifier = Modifier.clickable {
                                                viewModel.handleIntent(SportsListIntent.OpenTimePicker)
                                            }
                                        )
                                    }
                                )

                                Box {
                                    OutlinedTextField(
                                        value = state.addEventDialog.experience,
                                        onValueChange = {},
                                        label = { Text("Experience Level", color = Color(0xFFAAAAAA)) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.handleIntent(SportsListIntent.ToggleExperienceDropdown)
                                            },
                                        readOnly = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.White,
                                            disabledBorderColor = Color(0xFF666666),
                                            disabledLabelColor = Color(0xFFAAAAAA)
                                        ),
                                        enabled = false
                                    )
                                    DropdownMenu(
                                        expanded = state.addEventDialog.isExperienceDropdownOpen,
                                        onDismissRequest = {
                                            viewModel.handleIntent(SportsListIntent.ToggleExperienceDropdown)
                                        },
                                        modifier = Modifier.background(Color(0xFF3C3C3E))
                                    ) {
                                        listOf("Beginner", "Intermediate", "Advanced", "Expert").forEach { level ->
                                            DropdownMenuItem(
                                                text = { Text(level, color = Color.White) },
                                                onClick = {
                                                    viewModel.handleIntent(
                                                        SportsListIntent.UpdateEventExperience(level)
                                                    )
                                                    viewModel.handleIntent(SportsListIntent.ToggleExperienceDropdown)
                                                }
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = state.addEventDialog.host,
                                    onValueChange = {
                                        viewModel.handleIntent(SportsListIntent.UpdateEventHost(it))
                                    },
                                    label = { Text("Host Name", color = Color(0xFFAAAAAA)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF0A84FF),
                                        unfocusedBorderColor = Color(0xFF666666),
                                    )
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.handleIntent(SportsListIntent.AddSportEvent)
                            }) {
                                Text("Add", color = Color(0xFF0A84FF))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                viewModel.handleIntent(SportsListIntent.CloseAddEventDialog)
                            }) {
                                Text("Close", color = Color(0xFF0A84FF))
                            }
                        },
                        containerColor = Color(0xFF3C3C3E)
                    )
                }
                if (state.filters.isLocationDialogOpen){}
                if (state.filters.isDateDialogOpen) {
                    AlertDialog(
                        onDismissRequest = {
                            viewModel.handleIntent(SportsListIntent.CloseDateFilter)
                        },
                        title = {
                            Text(text = "Filter by Date", color = Color.White)
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = state.filters.selectedDate?.let { dateFormatter.format(it) } ?: "",
                                    onValueChange = {},
                                    label = { Text("Select Date", color = Color(0xFFAAAAAA)) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.handleIntent(SportsListIntent.CloseDateFilter)
                                            viewModel.handleIntent(SportsListIntent.OpenDateFilter)
                                        },
                                    readOnly = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledTextColor = Color.White,
                                        disabledBorderColor = Color(0xFF666666),
                                        disabledLabelColor = Color(0xFFAAAAAA)
                                    ),
                                    enabled = false,
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.calendar),
                                            contentDescription = "",
                                            tint = Color(0xFFAAAAAA)
                                        )
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.handleIntent(SportsListIntent.CloseDateFilter)
                                },
                                enabled = state.filters.selectedDate != null
                            ) {
                                Text(
                                    "Apply",
                                    color = if (state.filters.selectedDate != null)
                                        Color(0xFF0A84FF) else Color(0xFF666666)
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                viewModel.handleIntent(SportsListIntent.ClearDateFilter)
                                viewModel.handleIntent(SportsListIntent.CloseDateFilter)
                            }) {
                                Text("Clear", color = Color(0xFF0A84FF))
                            }
                        },
                        containerColor = Color(0xFF2C2C2E)
                    )
                }
                if (state.filters.isExperienceDialogOpen) {
                    AlertDialog(
                        onDismissRequest = {
                            viewModel.handleIntent(SportsListIntent.CloseExperienceFilter)
                        },
                        title = {
                            Text(text = "Experience Level", color = Color.White)
                        },
                        text = {
                            Column {
                                ExperienceFilterItem(
                                    "Beginner",
                                    isSelected = state.filters.selectedExperience == "Beginner",
                                    onItemClick = {
                                        viewModel.handleIntent(
                                            SportsListIntent.SetExperienceFilter("Beginner")
                                        )
                                    }
                                )
                                ExperienceFilterItem(
                                    "Intermediate",
                                    isSelected = state.filters.selectedExperience == "Intermediate",
                                    onItemClick = {
                                        viewModel.handleIntent(
                                            SportsListIntent.SetExperienceFilter("Intermediate")
                                        )
                                    }
                                )
                                ExperienceFilterItem(
                                    "Advanced",
                                    isSelected = state.filters.selectedExperience == "Advanced",
                                    onItemClick = {
                                        viewModel.handleIntent(
                                            SportsListIntent.SetExperienceFilter("Advanced")
                                        )
                                    }
                                )
                                ExperienceFilterItem(
                                    "Expert",
                                    isSelected = state.filters.selectedExperience == "Expert",
                                    onItemClick = {
                                        viewModel.handleIntent(
                                            SportsListIntent.SetExperienceFilter("Expert")
                                        )
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.handleIntent(SportsListIntent.CloseExperienceFilter)
                            }) {
                                Text("Apply", color = Color(0xFF0A84FF))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                viewModel.handleIntent(SportsListIntent.ClearExperienceFilter)
                                viewModel.handleIntent(SportsListIntent.CloseExperienceFilter)
                            }) {
                                Text("Clear", color = Color(0xFF0A84FF))
                            }
                        },
                        containerColor = Color(0xFF2C2C2E)
                    )
                }
                if (state.filters.isSportDialogOpen) {
                    AlertDialog(
                        onDismissRequest = {
                            viewModel.handleIntent(SportsListIntent.CloseSportFilter)
                        },
                        title = {
                            Text(text = "Sport", color = Color.White)
                        },
                        text = {
                            Column {
                                Sports.entries.forEach { sport ->
                                    SportFilterItem(
                                        sport = sport.displayName,
                                        isSelected = state.filters.selectedSport == sport.displayName,
                                        onItemClick = {
                                            viewModel.handleIntent(
                                                SportsListIntent.SetSportFilter(sport.displayName)
                                            )
                                        }
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.handleIntent(SportsListIntent.CloseSportFilter)
                            }) {
                                Text("Apply", color = Color(0xFF0A84FF))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                viewModel.handleIntent(SportsListIntent.ClearSportFilter)
                                viewModel.handleIntent(SportsListIntent.CloseSportFilter)
                            }) {
                                Text("Clear", color = Color(0xFF0A84FF))
                            }
                        },
                        containerColor = Color(0xFF2C2C2E)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = dateFormatter.format(java.util.Date()),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = dateFontSize,
                            color = Color(0xFF1C1C1E),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Queensway, London",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = locationFontSize,
                            color = Color(0xFF8E8E93),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(22.dp)
                            )
                            .background(
                                color = Color(0xFF007AFF),
                                shape = RoundedCornerShape(22.dp)
                            )
                            .padding(10.dp)
                            .clickable{ viewModel.handleIntent(SportsListIntent.OpenAddEventDialog) },
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.plus),
                            tint = Color.White,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp,bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    FilterItem(
                        "Location",
                        R.drawable.location,
                        isActive = state.filters.selectedLocation != null,
                        onFilterClick = {
                            viewModel.handleIntent(SportsListIntent.OpenLocationFilter)
                        }
                    )
                    FilterItem(
                        "Date",
                        R.drawable.calendar,
                        isActive = state.filters.selectedDate != null,
                        onFilterClick = {
                            viewModel.handleIntent(SportsListIntent.OpenDateFilter)
                        }
                    )
                    FilterItem(
                        "Experience",
                        R.drawable.volume,
                        isActive = state.filters.selectedExperience != null,
                        onFilterClick = {
                            viewModel.handleIntent(SportsListIntent.OpenExperienceFilter)
                        }
                    )
                    FilterItem(
                        "Sport",
                        R.drawable.sports,
                        isActive = state.filters.selectedSport != null,
                        onFilterClick = {
                            viewModel.handleIntent(SportsListIntent.OpenSportFilter)
                        }
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                when {
                    state.errorMessage != null -> {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ){
                                Text(
                                    text = state.errorMessage!!,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                    state.isLoading -> {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ){
                                CircularProgressIndicator()
                            }
                        }
                    }
                    state.events.isEmpty() ->{
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No events in this location",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    else -> {
                        items(
                            items = state.events,
                            key = { it.id }
                        ) { sportEvent ->
                            SportsListItem(
                                event = sportEvent,
                                onItemClick = {
                                    viewModel.handleIntent(SportsListIntent.JoinEvent(sportEvent))
                                }
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect){
                is SportsListEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is SportsListEffect.ShowError -> {
                    Toast.makeText(context, effect.error, Toast.LENGTH_SHORT).show()
                }
                is SportsListEffect.EventAddedSuccessfully -> {
                    Toast.makeText(context, "Event added successfully", Toast.LENGTH_SHORT).show()
                }
                is SportsListEffect.EventJoinedSuccessfully -> {
                    Toast.makeText(context, "Event joined successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}