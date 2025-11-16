package com.example.sportsapp.presentation.screens.my_events

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.presentation.screens.my_events.components.EventItem
import com.example.sportsapp.presentation.screens.sports_list.components.SportsListItem
import com.example.sportsapp.util.UiState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MyEventsScreen(
    viewModel: MyEventsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is MyEventsEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is MyEventsEffect.NavigateToEventDetail -> {
                    Toast.makeText(
                        context,
                        "Event clicked: ${effect.event.title}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    MyEventsContent(
        state = state,
        onIntent = viewModel::handleIntent
    )
}

@Composable
private fun MyEventsContent(
    state: MyEventsViewState,
    onIntent: (MyEventsIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFedfffe))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = if (state.isShowingHosted) "Hosted Events" else "Joined Events",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 17.sp,
                    color = Color(0xFF1C1C1E),
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF007AFF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable {
                            onIntent(MyEventsIntent.ToggleEventType)
                        }
                ) {
                    Text(
                        text = if (state.isShowingHosted) "Joined" else "Hosted",
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            when {
                state.isLoading -> {
                    LoadingContent()
                }
                state.errorMessage != null -> {
                    ErrorContent(errorMessage = state.errorMessage)
                }
                state.displayedEvents.isEmpty() -> {
                    EmptyContent(isShowingHosted = state.isShowingHosted)
                }
                else -> {
                    EventsList(
                        events = state.displayedEvents,
                        onEventClick = { event ->
                            onIntent(MyEventsIntent.OnEventClick(event))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(errorMessage: String){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun EmptyContent(isShowingHosted: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isShowingHosted) "No events hosted" else "No events joined",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun EventsList(
    events: List<SportEvent>,
    onEventClick: (SportEvent) -> Unit
){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ){
        items(
            items = events,
            key = {it.id}
        ){ event ->
            EventItem(
                event = event,
                onItemClick = onEventClick
            )
        }
    }
}