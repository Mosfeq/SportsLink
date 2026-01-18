package com.example.sportsapp.presentation.screens.my_events

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.presentation.navigation.Screens
import com.example.sportsapp.presentation.screens.my_events.components.EventItem
import kotlinx.coroutines.flow.collectLatest
import com.example.sportsapp.R

@Composable
fun MyEventsScreen(
    navController: NavController,
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
                    val route = if (state.isShowingHosted){
                        Screens.EventDetailScreen.createRoute(effect.eventId)
                    } else {
                        Screens.EventDetailScreen.createRouteFromJoined(effect.eventId)
                    }
                    navController.navigate(route)
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
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    spotColor = Color.Black.copy(alpha = 0.08f)
                )
                .background(Color.White)
                .padding(20.dp)
        ){
            Text(
                text = "My Events",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1E)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${state.displayedEvents.size} " +
                        if (state.isShowingHosted) "Hosted" else "Joined",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = Color(0xFF8E8E93)
            )
            Spacer(modifier = Modifier.height(20.dp))

            SegmentedControl(
                isHostedSelected = state.isShowingHosted,
                onToggle = { onIntent(MyEventsIntent.ToggleEventType) }
            )
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator(color = Color(0xFF007AFF))
                }
            }
            state.errorMessage != null -> {
                EmptyState(
                    icon = R.drawable.group,
                    title = "Error",
                    message = state.errorMessage,
                    iconColor = Color(0xFFFF3B30)
                )
            }
            state.displayedEvents.isEmpty() -> {
                EmptyState(
                    icon = if (state.isShowingHosted) R.drawable.person else R.drawable.group,
                    title = if (state.isShowingHosted) "No Events Hosted" else "No Events Joined",
                    message = if (state.isShowingHosted)
                        "You haven't created any events yet. Start by creating one!"
                    else
                        "You haven't joined any events yet. Browse available events to join!",
                    iconColor = Color(0xFF007AFF)
                )
            }
            else -> {
                EventsList(
                    events = state.displayedEvents,
                    isShowingHosted = state.isShowingHosted,
                    onEventClick = { event ->
                        onIntent(MyEventsIntent.OnEventClick(event))
                    },
                    onRemoveClick = { event ->
                        onIntent(MyEventsIntent.OnRemoveClick(event))
                    }
                )
            }
        }
    }
}

@Composable
private fun SegmentedControl(
    isHostedSelected: Boolean,
    onToggle: () -> Unit
){
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .background(
                color = Color(0xFFF2F2F7),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(4.dp)
    ){
        val offsetX by animateDpAsState(
            targetValue = if (isHostedSelected) 0.dp else with(LocalContext.current.resources.displayMetrics) {
                ((widthPixels / density) - 40.dp.value) / 2
            }.dp,
            animationSpec = tween(durationMillis = 300),
            label = "offset"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(40.dp)
                .offset(x = offsetX)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(10.dp),
                    spotColor = Color(0xFF007AFF).copy(alpha = 0.2f)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        if (!isHostedSelected) onToggle()
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.person),
                        contentDescription = null,
                        tint = if (isHostedSelected) Color(0xFF007AFF) else Color(0xFF8E8E93),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Hosted",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 15.sp,
                        fontWeight = if (isHostedSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isHostedSelected) Color(0xFF007AFF) else Color(0xFF8E8E93)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        if (isHostedSelected) onToggle()
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.group),
                        contentDescription = null,
                        tint = if (!isHostedSelected) Color(0xFF007AFF) else Color(0xFF8E8E93),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Joined",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 15.sp,
                        fontWeight = if (!isHostedSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (!isHostedSelected) Color(0xFF007AFF) else Color(0xFF8E8E93)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    @DrawableRes icon: Int,
    title: String,
    message: String,
    iconColor: Color
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = iconColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1E),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 15.sp,
                color = Color(0xFF8E8E93),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun EventsList(
    events: List<SportEvent>,
    isShowingHosted: Boolean,
    onEventClick: (SportEvent) -> Unit,
    onRemoveClick: (SportEvent) -> Unit
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(
            top = 20.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        items(
            items = events,
            key = {it.id}
        ){ event ->
            EventItem(
                event = event,
                isHostedEvent = isShowingHosted,
                onItemClick = onEventClick,
                onRemoveClick = if (isShowingHosted) onRemoveClick else null
            )
        }
    }
}