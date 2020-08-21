package com.example.station.ui.select

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.station.Screen

@Composable
fun SelectStationScreen(navigateTo: (Screen) -> Unit) {
    Column {
        Text("Select station")
        listOf("Helsinki", "Tampere").map {
            Text(it, modifier = Modifier.clickable(onClick = {
                navigateTo(Screen.Timetable(it))
            }))
        }
    }
}