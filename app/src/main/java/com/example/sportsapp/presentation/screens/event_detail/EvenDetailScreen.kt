package com.example.sportsapp.presentation.screens.event_detail

import android.widget.Toast
import com.example.sportsapp.R
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sportsapp.presentation.screens.sports_list.SportsListIntent
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EventDetailScreen(
    eventId: String,
    fromJoined: Boolean = false,
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(eventId) {
        viewModel.handleIntent(EventDetailIntent.LoadEvent(eventId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when(effect){
                is EventDetailEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF007AFF)
                )
            }
            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Icon(
                        painter = painterResource(R.drawable.sports),
                        contentDescription = null,
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.errorMessage ?: "Unknown error",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 18.sp,
                        color = Color(0xFF8E8E93),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            state.event != null -> {
                EventDetailContent(state = state, viewModel, fromJoined)
            }
        }
    }
}

@Composable
private fun EventDetailContent(
    state: EventDetailViewState,
    viewModel: EventDetailViewModel,
    fromJoined: Boolean
) {

    val event = state.event
    val formattedDate = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault()).format(event?.date)
    val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(event?.time)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF007AFF),
                            Color(0xFF0051D5)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = event?.sports ?: "",
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = event?.title ?: "",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color.White,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = getExperienceColor(
                                    event?.experience ?: ""
                                ).copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = event?.experience ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    if (!event?.numberOfPlayers.isNullOrEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${event?.numberOfPlayers} players",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DetailCard(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.calendar),
                        contentDescription = null,
                        tint = Color(0xFFFF9500),
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = "Date & Time",
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = event?.date?.let { formattedDate.format(it) } ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1C1C1E)
                        )
                        Text(
                            text = event?.time?.let { formattedTime.format(it) } ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            color = Color(0xFF8E8E93)
                        )
                    }
                },
                iconBackgroundColor = Color(0xFFFF9500).copy(alpha = 0.12f)
            )

            DetailCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFFF3B30),
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = "Location",
                content = {
                    Text(
                        text = event?.location ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1C1C1E)
                    )
                },
                iconBackgroundColor = Color(0xFFFF3B30).copy(alpha = 0.12f)
            )

            DetailCard(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF007AFF),
                                        Color(0xFF5856D6)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = event?.host?.take(1)?.uppercase() ?: "?",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                title = "Hosted by",
                content = {
                    Text(
                        text = event?.host ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1C1C1E)
                    )
                },
                iconBackgroundColor = Color.Transparent
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = if (fromJoined) Color(0xFF007AFF).copy(alpha = 0.3f) else Color(0xFFFF3B30).copy(alpha = 0.3f)
                    )
                    .background(
                        brush = if (fromJoined){
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF3B30),
                                    Color(0xFFFF2D55)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF007AFF),
                                    Color(0xFF0051D5)
                                )
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        if (fromJoined){
                            event?.let { viewModel.handleIntent(EventDetailIntent.OnLeaveEventClick(it)) }
                        } else {
                            event?.let { viewModel.handleIntent(EventDetailIntent.OnJoinEventClick(it)) }
                        }
                    }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (fromJoined) "Leave Event" else "Join Event",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailCard(
    icon: @Composable () -> Unit,
    title: String,
    content: @Composable () -> Unit,
    iconBackgroundColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = if (iconBackgroundColor != Color.Transparent) {
                Modifier
                    .size(52.dp)
                    .background(
                        color = iconBackgroundColor,
                        shape = RoundedCornerShape(12.dp)
                    )
            } else {
                Modifier.size(52.dp)
            },
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8E8E93)
            )
            content()
        }
    }
}

private fun getExperienceColor(experience: String): Color {
    return when (experience) {
        "Beginner" -> Color(0xFF34C759)
        "Intermediate" -> Color(0xFF007AFF)
        "Advanced" -> Color(0xFFFF9500)
        "Expert" -> Color(0xFFFF3B30)
        else -> Color(0xFF8E8E93)
    }
}

