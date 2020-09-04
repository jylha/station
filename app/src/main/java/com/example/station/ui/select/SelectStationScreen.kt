package com.example.station.ui.select

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
                    hintText = "Search stations",
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

@Composable
fun SearchBar(
    text: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    hintText: String = "",
    onClose: (() -> Unit)?
) {
    var active by remember { mutableStateOf(false) }

    Surface(modifier, color = MaterialTheme.colors.surface, elevation = 4.dp) {
        Row(
            Modifier.fillMaxWidth().padding(8.dp),
            verticalGravity = Alignment.CenterVertically
        ) {
            if (onClose != null) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.KeyboardBackspace)
                }
            }
            TextField(
                value = text,
                onValueChange = onValueChanged,
                label = { if (!active) Text(hintText) },
                onTextInputStarted = { active = true },
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.surface,
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Done,
                onImeActionPerformed = { _, kb ->
                    kb?.hideSoftwareKeyboard()
                    onClose?.invoke()
                }
            )
        }
    }
}

private inline fun <T> List<T>.filterWhen(condition: Boolean, predicate: (T) -> Boolean): List<T> {
    return if (condition) filterTo(ArrayList(), predicate) else this
}