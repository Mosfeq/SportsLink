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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.sportsapp.R
import com.example.sportsapp.domain.model.Sports
import com.example.sportsapp.presentation.navigation.Screens
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
    navController: NavController,
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
                is SportsListEffect.NavigateToEventDetail -> {
                    navController.navigate(Screens.EventDetailScreen.createRoute(effect.eventId))
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        spotColor = Color.Black.copy(alpha = 0.08f)
                    )
                    .background(Color(0xFFedfffe))
                    .padding(14.dp)
            ){
                if (state.addEventDialog.isDatePickerOpen || state.filters.isFilterDatePickerOpen) {
                    DatePickerDialog(
                        onDismissRequest = {
                            if (state.addEventDialog.isDatePickerOpen) {
                                viewModel.handleIntent(SportsListIntent.CloseDatePicker)
                            } else {
                                viewModel.handleIntent(SportsListIntent.CloseFilterDatePicker)
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
                                            viewModel.handleIntent(SportsListIntent.CloseFilterDatePicker)
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
                                    viewModel.handleIntent(SportsListIntent.CloseFilterDatePicker)
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
                    AddSportDialog(state, viewModel)
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
                                    value = state.filters.tempDate?.let { dateFormatter.format(it) } ?: "",
                                    onValueChange = {},
                                    label = { Text("Select Date", color = Color(0xFFAAAAAA)) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.handleIntent(SportsListIntent.OpenFilterDatePicker)
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
                                            tint = Color(0xFFAAAAAA),
                                            modifier = Modifier.clickable{
                                                viewModel.handleIntent(SportsListIntent.OpenFilterDatePicker)
                                            }
                                        )
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.handleIntent(SportsListIntent.ApplyDateFilter)
                                    viewModel.handleIntent(SportsListIntent.CloseDateFilter)
                                },
                                enabled = state.filters.tempDate != null
                            ) {
                                Text(
                                    "Apply",
                                    color = if (state.filters.tempDate != null)
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
                                    isSelected = state.filters.tempExperience == "Beginner",
                                    onItemClick = {
                                        viewModel.handleIntent(
                                            SportsListIntent.SetExperienceFilter("Beginner")
                                        )
                                    }
                                )
                                ExperienceFilterItem(
                                    "Intermediate",
                                    isSelected = state.filters.tempExperience == "Intermediate",
                                    onItemClick = {
                                        viewModel.handleIntent(
                                            SportsListIntent.SetExperienceFilter("Intermediate")
                                        )
                                    }
                                )
                                ExperienceFilterItem(
                                    "Advanced",
                                    isSelected = state.filters.tempExperience == "Advanced",
                                    onItemClick = {
                                        viewModel.handleIntent(
                                            SportsListIntent.SetExperienceFilter("Advanced")
                                        )
                                    }
                                )
                                ExperienceFilterItem(
                                    "Expert",
                                    isSelected = state.filters.tempExperience == "Expert",
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
                                viewModel.handleIntent(SportsListIntent.ApplyExperienceFilter)
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
                                        isSelected = state.filters.tempSport == sport.displayName,
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
                                viewModel.handleIntent(SportsListIntent.ApplySportFilter)
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
                        .padding(bottom = 24.dp),
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
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Queensway, London",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = locationFontSize,
                            color = Color(0xFF8E8E93),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
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
                            .padding(12.dp)
                            .clickable{ viewModel.handleIntent(SportsListIntent.OpenAddEventDialog) },
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.plus),
                            tint = Color.White,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp,bottom = 4.dp),
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
                    .padding(horizontal = 20.dp)
                    .background(Color.White),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                    text = "Be the first to create an event!",
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
                                    viewModel.handleIntent(SportsListIntent.OnEventClick(sportEvent))
                                }
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddSportDialog(
    state: SportsListUiState,
    viewModel: SportsListViewModel
){
    val addDateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    AlertDialog(
        onDismissRequest = {
            viewModel.handleIntent(SportsListIntent.CloseAddEventDialog)
        },
        title = {
            Column {
                Text(
                    text = "Create New Event",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fill in the details below",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFAAAAAA),
                    fontSize = 14.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFF0A84FF).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sports),
                            contentDescription = null,
                            tint = Color(0xFF0A84FF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = state.addEventDialog.title,
                        onValueChange = {
                            viewModel.handleIntent(SportsListIntent.UpdateEventTitle(it))
                        },
                        label = {
                            Text("Event Title", color = Color(0xFFAAAAAA))
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF0A84FF),
                            unfocusedBorderColor = Color(0xFF4A4A4C)
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFF34C759).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sports),
                            contentDescription = null,
                            tint = Color(0xFF34C759),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(1f)) {
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
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.White,
                                disabledBorderColor = Color(0xFF4A4A4C),
                                disabledLabelColor = Color(0xFFAAAAAA)
                            ),
                            enabled = false,
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(
                                        id = if (state.addEventDialog.isSportDropdownOpen)
                                            android.R.drawable.arrow_up_float
                                        else
                                            android.R.drawable.arrow_down_float
                                    ),
                                    contentDescription = null,
                                    tint = Color(0xFFAAAAAA)
                                )
                            }
                        )
                        DropdownMenu(
                            expanded = state.addEventDialog.isSportDropdownOpen,
                            onDismissRequest = {
                                viewModel.handleIntent(SportsListIntent.ToggleSportDropdown)
                            },
                            modifier = Modifier
                                .background(Color(0xFF3C3C3E))
                                .fillMaxWidth(0.7f)
                        ) {
                            Sports.entries.forEach { sport ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            sport.displayName,
                                            color = if (state.addEventDialog.sport == sport.displayName)
                                                Color(0xFF0A84FF)
                                            else
                                                Color.White
                                        )
                                    },
                                    onClick = {
                                        viewModel.handleIntent(
                                            SportsListIntent.UpdateEventSport(sport.displayName)
                                        )
                                        viewModel.handleIntent(SportsListIntent.ToggleSportDropdown)
                                    },
                                    modifier = Modifier.background(
                                        if (state.addEventDialog.sport == sport.displayName)
                                            Color(0xFF0A84FF).copy(alpha = 0.1f)
                                        else
                                            Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFFFF3B30).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.location),
                            contentDescription = null,
                            tint = Color(0xFFFF3B30),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = state.addEventDialog.location,
                        onValueChange = {
                            viewModel.handleIntent(SportsListIntent.UpdateEventLocation(it))
                        },
                        label = { Text("Location", color = Color(0xFFAAAAAA)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF0A84FF),
                            unfocusedBorderColor = Color(0xFF4A4A4C)
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = Color(0xFFFF9500).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.calendar),
                                    contentDescription = null,
                                    tint = Color(0xFFFF9500),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Date",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        OutlinedTextField(
                            value = state.addEventDialog.date?.let { addDateFormatter.format(it) } ?: "",
                            onValueChange = {},
                            placeholder = { Text("Select", color = Color(0xFF666666), fontSize = 13.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.handleIntent(SportsListIntent.OpenDatePicker)
                                },
                            readOnly = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.White,
                                disabledBorderColor = Color(0xFF4A4A4C),
                            ),
                            enabled = false,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = Color(0xFF5856D6).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.calendar),
                                    contentDescription = null,
                                    tint = Color(0xFF5856D6),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Time",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        OutlinedTextField(
                            value = state.addEventDialog.time?.let { timeFormatter.format(it) } ?: "",
                            onValueChange = {},
                            placeholder = { Text("Select", color = Color(0xFF666666), fontSize = 13.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.handleIntent(SportsListIntent.OpenTimePicker)
                                },
                            readOnly = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.White,
                                disabledBorderColor = Color(0xFF4A4A4C),
                            ),
                            enabled = false,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFFFFCC00).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.volume),
                            contentDescription = null,
                            tint = Color(0xFFFFCC00),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(1f)) {
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
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.White,
                                disabledBorderColor = Color(0xFF4A4A4C),
                                disabledLabelColor = Color(0xFFAAAAAA)
                            ),
                            enabled = false,
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(
                                        id = if (state.addEventDialog.isExperienceDropdownOpen)
                                            android.R.drawable.arrow_up_float
                                        else
                                            android.R.drawable.arrow_down_float
                                    ),
                                    contentDescription = null,
                                    tint = Color(0xFFAAAAAA)
                                )
                            }
                        )
                        DropdownMenu(
                            expanded = state.addEventDialog.isExperienceDropdownOpen,
                            onDismissRequest = {
                                viewModel.handleIntent(SportsListIntent.ToggleExperienceDropdown)
                            },
                            modifier = Modifier
                                .background(Color(0xFF3C3C3E))
                                .fillMaxWidth(0.7f)
                        ) {
                            listOf("Beginner", "Intermediate", "Advanced", "Expert").forEach { level ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            level,
                                            color = if (state.addEventDialog.experience == level)
                                                Color(0xFF0A84FF)
                                            else
                                                Color.White
                                        )
                                    },
                                    onClick = {
                                        viewModel.handleIntent(
                                            SportsListIntent.UpdateEventExperience(level)
                                        )
                                        viewModel.handleIntent(SportsListIntent.ToggleExperienceDropdown)
                                    },
                                    modifier = Modifier.background(
                                        if (state.addEventDialog.experience == level)
                                            Color(0xFF0A84FF).copy(alpha = 0.1f)
                                        else
                                            Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFF00C7BE).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF00C7BE),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = state.addEventDialog.numberOfPlayers,
                        onValueChange = {
                            viewModel.handleIntent(SportsListIntent.UpdateEventNumberOfPlayers(it))
                        },
                        label = { Text("Number of Players", color = Color(0xFFAAAAAA)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF0A84FF),
                            unfocusedBorderColor = Color(0xFF4A4A4C)
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                viewModel.handleIntent(SportsListIntent.AddSportEvent)
            }) {
                Text("Create", color = Color(0xFF0A84FF))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                viewModel.handleIntent(SportsListIntent.CloseAddEventDialog)
            }) {
                Text("Close", color = Color(0xFF0A84FF))
            }
        },
        containerColor = Color(0xFF3C3C3E),
        shape = RoundedCornerShape(20.dp)
    )
}