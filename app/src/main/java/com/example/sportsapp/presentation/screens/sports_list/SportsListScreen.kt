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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.domain.model.Sports
import com.example.sportsapp.presentation.screens.sports_list.components.ExperienceFilterItem
import com.example.sportsapp.presentation.screens.sports_list.components.FilterItem
import com.example.sportsapp.presentation.screens.sports_list.components.SportFilterItem
import com.example.sportsapp.presentation.screens.sports_list.components.SportsListItem
import com.example.sportsapp.util.UiState
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

    val sportEventsList by viewModel.sportsEventList.collectAsStateWithLifecycle()

    var showExperienceFilter by remember { mutableStateOf(false) }
    var showDateFilter by remember { mutableStateOf(false) }
    var showSportFilter by remember { mutableStateOf(false) }
    var showLocationFilter by remember { mutableStateOf(false) }
    var showAddSportEventFilter by remember { mutableStateOf(false) }

    var eventTitle by remember { mutableStateOf("") }
    var eventLocation by remember { mutableStateOf("") }
    var selectedSport by remember { mutableStateOf<Sports?>(null) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var selectedTime by remember { mutableStateOf<Time?>(null) }
    var eventExperience by remember { mutableStateOf("") }
    var eventHost by remember { mutableStateOf("") }

    var selectedExperienceFilter by remember { mutableStateOf("") }
    var selectedSportFilter by remember { mutableStateOf("") }
    var selectedDateFilter by remember { mutableStateOf<Date?>(null) }
    var isFilteringDate by remember { mutableStateOf(false) }

    var expandedSportMenu by remember { mutableStateOf(false) }
    var expandedExperienceMenu by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
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
                if (showDatePicker){
                    DatePickerDialog(
                        onDismissRequest = {
                            showDatePicker = false
                            if (isFilteringDate){
                                showDateFilter = true
                                isFilteringDate = false
                            }},
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        if (isFilteringDate){
                                            selectedDateFilter= Date(millis)
                                            showDatePicker = false
                                            showDateFilter = true
                                            isFilteringDate = false
                                        } else {
                                            selectedDate = Date(millis)
                                            showDatePicker = false
                                        }
                                    }
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showDatePicker = false
                                if (isFilteringDate){
                                    showDateFilter = true
                                    isFilteringDate = false
                                }
                            }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                if (showTimePicker){
                    AlertDialog(
                        onDismissRequest = { showTimePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val hour = timePickerState.hour
                                    val minute = timePickerState.minute
                                    selectedTime = Time.valueOf(String.format(Locale.getDefault(),"%02d:%02d:00", hour, minute))
                                    showTimePicker = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTimePicker = false }) {
                                Text("Cancel")
                            }
                        },
                        text = {
                            TimePicker(state = timePickerState)
                        }
                    )
                }
                if (showAddSportEventFilter) {
                    AlertDialog(
                        onDismissRequest = { showAddSportEventFilter = false },
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
                                    value = eventTitle,
                                    onValueChange = { eventTitle = it },
                                    label = {
                                        Text(
                                            "Event Title",
                                            color = Color(0xFFAAAAAA)
                                        )},
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
                                        value = selectedSport?.name ?: "",
                                        onValueChange = {},
                                        label = { Text(
                                            "Sport",
                                            color = Color(0xFFAAAAAA)
                                        )},
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { expandedSportMenu = true },
                                        readOnly = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.White,
                                            disabledBorderColor = Color(0xFF666666),
                                            disabledLabelColor = Color(0xFFAAAAAA)
                                        ),
                                        enabled = false
                                    )
                                    DropdownMenu(
                                        expanded = expandedSportMenu,
                                        onDismissRequest = { expandedSportMenu = false },
                                        modifier = Modifier.background(Color(0xFF3C3C3E))
                                    ) {
                                        Sports.entries.forEach { sport ->
                                            DropdownMenuItem(
                                                text = { Text(
                                                    sport.displayName,
                                                    color = Color.White
                                                )},
                                                onClick = {
                                                    selectedSport = sport
                                                    expandedSportMenu = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // Location
                                OutlinedTextField(
                                    value = eventLocation,
                                    onValueChange = { eventLocation = it },
                                    label = { Text(
                                        "Location",
                                        color = Color(0xFFAAAAAA)
                                    )},
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF0A84FF),
                                        unfocusedBorderColor = Color(0xFF666666),
                                    )
                                )

                                // Date Picker
                                OutlinedTextField(
                                    value = selectedDate?.let { addDateFormatter.format(it) } ?: "",
                                    onValueChange = {},
                                    label = { Text(
                                        "Date",
                                        color = Color(0xFFAAAAAA)
                                    )},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showDatePicker = true },
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
                                            modifier = Modifier.clickable { showDatePicker = true }
                                        )
                                    }
                                )

                                // Time Picker
                                OutlinedTextField(
                                    value = selectedTime?.let { timeFormatter.format(it) } ?: "",
                                    onValueChange = {},
                                    label = { Text(
                                        "Time",
                                        color = Color(0xFFAAAAAA)
                                    )},
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showTimePicker = true },
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
                                            modifier = Modifier.clickable { showTimePicker = true }
                                        )
                                    }
                                )

                                // Experience Dropdown
                                Box {
                                    OutlinedTextField(
                                        value = eventExperience,
                                        onValueChange = {},
                                        label = { Text(
                                            "Experience Level",
                                            color = Color(0xFFAAAAAA)
                                        )},
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { expandedExperienceMenu = true },
                                        readOnly = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = Color.White,
                                            disabledBorderColor = Color(0xFF666666),
                                            disabledLabelColor = Color(0xFFAAAAAA)
                                        ),
                                        enabled = false
                                    )
                                    DropdownMenu(
                                        expanded = expandedExperienceMenu,
                                        onDismissRequest = { expandedExperienceMenu = false },
                                        modifier = Modifier.background(Color(0xFF3C3C3E))
                                    ) {
                                        listOf("Beginner", "Intermediate", "Advanced", "Expert").forEach { experienceLevel ->
                                            DropdownMenuItem(
                                                text = { Text(
                                                    experienceLevel,
                                                    color = Color.White
                                                ) },
                                                onClick = {
                                                    eventExperience = experienceLevel
                                                    expandedExperienceMenu = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // Host
                                OutlinedTextField(
                                    value = eventHost,
                                    onValueChange = { eventHost = it },
                                    label = { Text(
                                        "Host Name",
                                        color = Color(0xFFAAAAAA)
                                    )},
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
                                if (eventTitle.isNotEmpty() &&
                                    selectedSport != null &&
                                    eventLocation.isNotEmpty() &&
                                    selectedDate != null &&
                                    selectedTime != null &&
                                    eventExperience.isNotEmpty() &&
                                    eventHost.isNotEmpty())
                                {
                                    val sportEvent = SportEvent(
                                        title = eventTitle,
                                        sports = selectedSport!!,
                                        location = eventLocation,
                                        date = selectedDate!!,
                                        time = selectedTime!!,
                                        experience = eventExperience,
                                        host = eventHost
                                    )
                                    viewModel.addSportEvent(sportEvent)

                                    eventTitle = ""
                                    selectedSport = null
                                    eventLocation = ""
                                    selectedDate = null
                                    selectedTime = null
                                    eventExperience = ""
                                    eventHost = ""

                                    showAddSportEventFilter = false
                                }
                                else{
                                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Text(
                                    "Add",
                                    color = Color(0xFF0A84FF)
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                eventTitle = ""
                                selectedSport = null
                                eventLocation = ""
                                selectedDate = null
                                selectedTime = null
                                eventExperience = ""
                                eventHost = ""

                                showAddSportEventFilter = false
                            }) {
                                Text(
                                    "Close",
                                    color = Color(0xFF0A84FF)
                                )
                            }
                        },
                        containerColor = Color(0xFF3C3C3E)
                    )
                }
                if (showLocationFilter){

                }
                if (showDateFilter){
                    AlertDialog(
                        onDismissRequest = { showDateFilter = false },
                        title = {
                            Text(
                                text = "Filter by Date",
                                color = Color.White
                            )
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = selectedDateFilter?.let { dateFormatter.format(it) } ?: "",
                                    onValueChange = {},
                                    label = {
                                        Text(
                                            "Select Date",
                                            color = Color(0xFFAAAAAA)
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable{
                                            showDateFilter = false
                                            isFilteringDate = true
                                            showDatePicker = true
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
                                            modifier = Modifier.clickable {
                                                showDateFilter = false
                                                isFilteringDate = true
                                                showDatePicker = true
                                            }
                                        )
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    selectedDateFilter?.let { date ->
                                        viewModel.setDateFilter(date)
                                    }
                                    showDateFilter = false
                                },
                                enabled = selectedDateFilter != null
                            ) {
                                Text(
                                    "Apply",
                                    color = if (selectedDateFilter != null) Color(0xFF0A84FF) else Color(0xFF666666)
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                selectedDateFilter = null
                                viewModel.clearDateFilter()
                                showDateFilter = false
                            }) {
                                Text(
                                    "Clear",
                                    color = Color(0xFF0A84FF)
                                )
                            }
                        },
                        containerColor = Color(0xFF2C2C2E)
                    )
                }
                if (showExperienceFilter) {
                    AlertDialog(
                        onDismissRequest = { showExperienceFilter = false },
                        title = {
                            Text(
                                text = "Experience Level",
                                color = Color.White
                            )
                        },
                        text = {
                            Column {
                                ExperienceFilterItem(
                                    "Beginner",
                                    isSelected = selectedExperienceFilter == "Beginner",
                                    onItemClick = {
                                        selectedExperienceFilter = "Beginner"
                                    }
                                )
                                ExperienceFilterItem(
                                    "Intermediate",
                                    isSelected = selectedExperienceFilter == "Intermediate",
                                    onItemClick = {
                                        selectedExperienceFilter = "Intermediate"
                                    }
                                )
                                ExperienceFilterItem(
                                    "Advanced",
                                    isSelected = selectedExperienceFilter == "Advanced",
                                    onItemClick = {
                                        selectedExperienceFilter = "Advanced"
                                    }
                                )
                                ExperienceFilterItem(
                                    "Expert",
                                    isSelected = selectedExperienceFilter == "Expert",
                                    onItemClick = {
                                        selectedExperienceFilter = "Expert"
                                    }
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.setExperienceFilter(selectedExperienceFilter)
                                showExperienceFilter = false
                            }) {
                                Text(
                                    "Apply",
                                    color = Color(0xFF0A84FF)
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                selectedExperienceFilter = ""
                                viewModel.clearExperienceFilter()
                                showExperienceFilter = false
                            }) {
                                Text(
                                    "Clear",
                                    color = Color(0xFF0A84FF)
                                )
                            }
                        },
                        containerColor = Color(0xFF2C2C2E)
                    )
                }
                if (showSportFilter) {
                    AlertDialog(
                        onDismissRequest = { showSportFilter = false },
                        title = {
                            Text(
                                text = "Sport",
                                color = Color.White
                            )
                        },
                        text = {
                            Column {
                                Sports.entries.forEach { sport ->
                                    SportFilterItem(
                                        sport = sport.displayName,
                                        isSelected = selectedSportFilter == sport.displayName,
                                        onItemClick = {
                                            selectedSportFilter = sport.displayName
                                        }
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.setSportsFilter(selectedSportFilter)
                                showSportFilter = false
                            }) {
                                Text(
                                    "Apply",
                                    color = Color(0xFF0A84FF)
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                selectedSportFilter = ""
                                viewModel.clearSportFilter()
                                showSportFilter = false
                            }) {
                                Text(
                                    "Clear",
                                    color = Color(0xFF0A84FF)
                                )
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
                            .clickable{showAddSportEventFilter = !showAddSportEventFilter},
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
                        isActive = false,
                        onFilterClick = {
                            showLocationFilter = !showLocationFilter
                        }
                    )
                    FilterItem(
                        "Date",
                        R.drawable.calendar,
                        isActive = selectedDateFilter != null,
                        onFilterClick = {
                            showDateFilter = !showDateFilter
                        }
                    )
                    FilterItem(
                        "Experience",
                        R.drawable.volume,
                        isActive = selectedExperienceFilter.isNotEmpty(),
                        onFilterClick = {
                            showExperienceFilter = !showExperienceFilter
                        }
                    )
                    FilterItem(
                        "Sport",
                        R.drawable.sports,
                        isActive = selectedSportFilter.isNotEmpty(),
                        onFilterClick = {
                            showSportFilter = !showExperienceFilter
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
                when(sportEventsList){
                    is UiState.Success ->{
                        sportEventsList.data?.let { listOfSportEvents ->
                            if (listOfSportEvents.isEmpty()){
                                item {
                                    Box(
                                        modifier = Modifier.fillParentMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ){
                                        Text(
                                            text = "No events in this location",
                                            modifier = Modifier.align(Alignment.Center),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontSize = 20.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            else{
                                items(
                                    items = listOfSportEvents,
                                    key = {it.id}
                                ){ sportEvent ->
                                    SportsListItem(
                                        event =  sportEvent,
                                        onItemClick = {
                                            viewModel.joinSportEvent(sportEvent)
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        item {
                            sportEventsList.errorMessage?.let {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ){
                                    Text(
                                        text = it,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                        Toast.makeText(context, "${sportEventsList.errorMessage}", Toast.LENGTH_SHORT).show()
                    }
                    is UiState.Loading ->{
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ){
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.addSportEvent.collectLatest { response ->
            when(response){
                is UiState.Success ->{
                    Toast.makeText(context, "${response.data}", Toast.LENGTH_SHORT).show()
                }
                is UiState.Error -> {
                    Toast.makeText(context, "${response.errorMessage}", Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {}
            }
        }
    }

    LaunchedEffect(true) {
        viewModel.joinSportEvent.collectLatest { response ->
            when(response){
                is UiState.Success ->{
                    Toast.makeText(context, "${response.data}", Toast.LENGTH_SHORT).show()
                }
                is UiState.Error -> {
                    Toast.makeText(context, "${response.errorMessage}", Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {}
            }
        }
    }
}