package com.example.sportsapp.presentation.screens.my_events.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sportsapp.R
import com.example.sportsapp.domain.model.SportEvent
import com.example.sportsapp.domain.model.Sports
import com.example.sportsapp.ui.theme.SportsAppTheme
import java.util.Date
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EventItem(
    event: SportEvent,
    onItemClick: (SportEvent) -> Unit,
) {

    val dateFormatter = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onItemClick(event) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    getSportColor(event.sports),
                                    getSportColor(event.sports).copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sports),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = getExperienceColor(event.experience),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = event.experience.take(3).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFF1C1C1E),
                        lineHeight = 24.sp
                    )

                    Text(
                        text = event.sports,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = getSportColor(event.sports)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.calendar),
                            contentDescription = null,
                            tint = Color(0xFF8E8E93),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = dateFormatter.format(event.date),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = Color(0xFF3C3C43)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.clock),
                            contentDescription = null,
                            tint = Color(0xFF8E8E93),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = timeFormatter.format(event.time),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = Color(0xFF3C3C43)
                        )
                    }

                    if (event.numberOfPlayers.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF8E8E93),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${event.numberOfPlayers} players",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                color = Color(0xFF3C3C43)
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(14.dp)
                    )
                    event.location?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = Color(0xFF3C3C43),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.profile),
                        contentDescription = null,
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = event.host,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF8E8E93)
                    )
                }
            }
        }
    }
}

private fun getSportColor(sport: String): Color {
    return when (sport) {
        "Football" -> Color(0xFF34C759)
        "Tennis" -> Color(0xFFFFCC00)
        "Golf" -> Color(0xFF32ADE6)
        "Basketball" -> Color(0xFFFF9500)
        "Table Tennis" -> Color(0xFFFF3B30)
        "Padel" -> Color(0xFF5856D6)
        "Rugby" -> Color(0xFFAF52DE)
        "Karting" -> Color(0xFFFF2D55)
        "Badminton" -> Color(0xFF00C7BE)
        else -> Color(0xFF007AFF)
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

@Preview
@Composable
private fun SportsItemPreview(){
    SportsAppTheme {
        Column(
            modifier = Modifier
                .background(Color(0xFFF8F9FA))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EventItem(
                event = SportEvent(
                    title = "Weekend Football Match",
                    sports = Sports.Football.displayName,
                    location = "Queensway, Paddington",
                    date = Date().time,
                    time = Time(System.currentTimeMillis()).time,
                    experience = "Intermediate",
                    numberOfPlayers = "10",
                    host = "Mark Joseph"
                ),
                onItemClick = {}
            )

            EventItem(
                event = SportEvent(
                    title = "Early Morning Tennis Session",
                    sports = Sports.Tennis.displayName,
                    location = "Hyde Park Courts",
                    date = Date().time,
                    time = Time(System.currentTimeMillis()).time,
                    experience = "Expert",
                    numberOfPlayers = "4",
                    host = "Sarah Williams"
                ),
                onItemClick = {}
            )
        }
    }
}