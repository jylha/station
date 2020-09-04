package com.example.station.ui.select

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.example.station.Screen
import com.example.station.model.Station
import com.example.station.ui.components.SearchBar

@Composable
fun SelectStationScreen(
    navigateTo: (Screen) -> Unit
) {
    val viewModel = viewModel<SelectStationsViewModel>()
    val stations by viewModel.stations.observeAsState()

    var searchEnabled by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val filteredStations = remember(searchEnabled, searchText, stations) {
        stations?.filterWhen(searchEnabled) { station ->
            station.name.contains(searchText, ignoreCase = true)
        } ?: emptyList()
    }

    Scaffold(
        topBar = {
            if (searchEnabled) {
                SearchBar(
                    text = searchText,
                    placeholderText = "Search stations",
                    onValueChanged = { value -> searchText = value },
                    onClose = { searchEnabled = false; searchText = "" }
                )
            } else {
                TopAppBar(
                    title = { Text("Select station") },
                    actions = {
                        IconButton(onClick = { searchEnabled = true }) {
                            Icon(Icons.Default.Search)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        if (stations != null) {
            StationList(filteredStations, onSelect = { station ->
                navigateTo(Screen.Timetable(station))
            }, Modifier.padding(innerPadding))
        } else {
            LoadingStations()
        }
    }
}

@Composable
fun StationList(
    stations: List<Station>,
    onSelect: (Station) -> Any,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        LazyColumnFor(stations) { station ->
            Text(
                text = station.name,
                modifier = Modifier
                    .clickable(onClick = { onSelect(station) })
                    .padding(8.dp)
            )
        }
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

private inline fun <T> List<T>.filterWhen(condition: Boolean, predicate: (T) -> Boolean): List<T> {
    return if (condition) filterTo(ArrayList(), predicate) else this
}