package com.example.sportsapp.presentation.screens.sports_list.components

import com.example.sportsapp.R
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterItem(
    filterName: String,
    @DrawableRes icon: Int,
    isActive: Boolean = false,
    onFilterClick: () -> Unit
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color = if (isActive) Color(0xFFa9d1fc) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = if (isActive) Color(0xFF007AFF) else Color(0xFFE5E5EA),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable{
                onFilterClick()
            }
            .padding(10.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            tint = if (isActive) Color(0xFF007AFF) else Color(0xFF3C3C43),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(bottom = 6.dp)
        )
        Text(
            text = filterName,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 13.sp,
            color = Color(0xFF3C3C43)
        )
    }
}

@Preview
@Composable
private fun FilterItemPreview(){
    Column(
        modifier = Modifier
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        FilterItem(
            "Location",
            R.drawable.location,
            isActive = false,
            onFilterClick = {}
        )
    }
}