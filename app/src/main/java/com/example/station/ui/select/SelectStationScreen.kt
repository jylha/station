package com.example.station.ui.select

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.example.station.Screen
import com.example.station.model.Station

@Composable
fun SelectStationScreen(
    navigateTo: (Screen) -> Unit
) {

    val viewModel = viewModel<SelectStationsViewModel>()
    val stations by viewModel.stations.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar {
                Text(modifier = Modifier.padding(16.dp), text = "Select station")
            }
        }
    ) {
        if (stations != null) {
            StationList(stations!!, onSelect = { station ->
                navigateTo(Screen.Timetable(station))
            })
        } else {
            LoadingStations()
        }
    }
}

@Composable
fun StationList(stations: List<Station>, onSelect: (Station) -> Any) {
    LazyColumnFor(stations) { station ->
        Text(
            text = station.name,
            modifier = Modifier
                .clickable(onClick = { onSelect(station) })
                .padding(8.dp)
        )
    }
}


@Composable
fun LoadingStations() {
    Surface(Modifier.fillMaxSize()) {
        Text(
            text = "Loading stations...",
            modifier = Modifier.padding(16.dp)
        )
    }
}
