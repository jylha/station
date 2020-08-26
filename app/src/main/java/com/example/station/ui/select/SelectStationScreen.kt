package com.example.station.ui.select

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.example.station.Screen

@Composable
fun SelectStationScreen(
    navigateTo: (Screen) -> Unit,
    viewModel: SelectStationsViewModel
) {
    val stations by viewModel.stations.observeAsState()
    Column {
        Text("Select station")
        stations?.map { station ->
            Text(station.name, modifier = Modifier.clickable(onClick = {
                navigateTo(Screen.Timetable(station.name))
            }))
        }
    }
}