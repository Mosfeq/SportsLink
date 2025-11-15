package com.example.sportsapp.presentation.screens.my_events

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sportsapp.presentation.screens.my_events.components.EventItem
import com.example.sportsapp.presentation.screens.sports_list.components.SportsListItem
import com.example.sportsapp.util.UiState

@Composable
fun MyEventsScreen(
    viewModel: MyEventsViewModel = hiltViewModel()
){
    val hostedEventsList by viewModel.hostedSportEventList.collectAsStateWithLifecycle()
    val joinedSportsEventsList by viewModel.joinedSportEventList.collectAsStateWithLifecycle()

    var hostedListActive by remember { mutableStateOf(true) }
    val currentEventList = if (hostedListActive) hostedEventsList else joinedSportsEventsList

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFedfffe))
            .padding(20.dp)
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = if (hostedListActive) "Hosted Events" else "Joined Events",
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
                        .clickable{
                            hostedListActive = !hostedListActive
                            if (hostedListActive) viewModel.getJoinedEventsList() else viewModel.getHostedEventsList()
                        }
                ){
                    Text(
                        text = if (hostedListActive) "Joined" else "Hosted",
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                when (currentEventList){
                    is UiState.Success -> {
                        currentEventList.data?.let { events ->
                            if (events.isEmpty()){
                                item {
                                    Box(
                                        modifier = Modifier.fillParentMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ){
                                        Text(
                                            text = if (hostedListActive) "No events hosted" else "No events joined",
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
                                    items = events,
                                    key = {it.id}
                                ){ event ->
                                    EventItem(
                                        event =  event,
                                        onItemClick = {

                                        }
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        item {
                            currentEventList.errorMessage?.let {
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
                    }
                    is UiState.Loading-> {
                        item{
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

}