package com.example.sportsapp.presentation.screens.my_events.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFE5E5EA),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable{onItemClick(event)}
            .padding(16.dp)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF1C1C1E),
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFF007AFF),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ){
                Text(
                    text = event.sports,
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(16.dp)
            )
            event.location?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    color = Color(0xFF3C3C43),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                painter = painterResource(R.drawable.calendar),
                contentDescription = null,
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = dateFormatter.format(event.date),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = Color(0xFF3C3C43)
            )
            Text(
                text = "•",
                color = Color(0xFF8E8E93)
            )
            Text(
                text = timeFormatter.format(event.time),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = Color(0xFF3C3C43)
            )

        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = event.experience,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = Color(0xFF3C3C43)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Hosted by ${event.host}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = Color(0xFF3C3C43)
            )
        }

    }

}

@Preview
@Composable
private fun SportsItemPreview(){
    SportsAppTheme {
        Column(
            modifier = Modifier
                .background(Color(0xFFF2F2F7))
                .padding(16.dp)
        ) {
            EventItem(
                event = SportEvent(
                    title = "Footy Sesh",
                    sports = Sports.TableTennis,
                    location = "Queensway, Paddington",
                    date = Date(),
                    time = Time(System.currentTimeMillis()),
                    experience = "Expert",
                    host = "Mark Joseph"
                ),
                onItemClick = {}
            )
        }
    }
}