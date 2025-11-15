package com.example.sportsapp.presentation.screens.sports_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExperienceFilterItem(
    experienceLevel: String,
    isSelected: Boolean = false,
    onItemClick:() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = experienceLevel,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = if (isSelected) 18.sp else 16.sp,
            color = if (isSelected) Color.White else Color(0xFFAAAAAA),
        )
    }
}