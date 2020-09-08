package com.example.station.ui.stations

import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.example.station.Screen
import com.example.station.model.Station
import com.example.station.ui.components.EmptyState
import com.example.station.ui.components.LoadingMessage
import com.example.station.ui.components.SearchBar
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun StationScreen(
    navigateTo: (Screen) -> Unit
) {
    val viewModel = viewModel<StationsViewModel>()
    val viewState by viewModel.viewState.collectAsState()
    val stations = viewState.stations

    var searchEnabled by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val filteredStations = remember(searchEnabled, searchText, stations) {
        stations.filterWhen(searchEnabled) { station ->
            station.name.contains(searchText, ignoreCase = true)
        }
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
        val modifier = Modifier.padding(innerPadding)
        when {
            viewState.isLoading -> LoadingMessage("Loading stations...")
            filteredStations.isEmpty() -> EmptyState("No matching stations.", modifier)
            else -> StationList(
                filteredStations,
                onSelect = { station -> navigateTo(Screen.Timetable(station)) },
                modifier,
                highlightedText = searchText
            )
        }
    }
}

@Composable
private fun StationList(
    stations: List<Station>,
    onSelect: (Station) -> Any,
    modifier: Modifier = Modifier,
    highlightedText: String? = null
) {
    Column(modifier) {
        LazyColumnFor(stations) { station ->
            StationListEntry(station = station, onSelect = onSelect, searchText = highlightedText)
        }
    }
}

@Composable
private fun StationListEntry(
    station: Station,
    onSelect: (Station) -> Any,
    modifier: Modifier = Modifier,
    searchText: String? = null
) {
    val fg = MaterialTheme.colors.onPrimary
    val bg = MaterialTheme.colors.primary
    val name = remember(station.name, searchText) {
        with(AnnotatedString.Builder(station.name)) {
            if (searchText?.isNotBlank() == true) {
                val index = station.name.indexOf(searchText, 0, ignoreCase = true)
                addStyle(SpanStyle(color = fg, background = bg), index, index + searchText.length)
            }
            toAnnotatedString()
        }
    }
    Box(
        modifier
            .fillMaxWidth()
            .clickable(onClick = { onSelect(station) })
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(name)
    }
}

/** Filter a list with [predicate] only when given [condition] is true. */
private inline fun <T> List<T>.filterWhen(condition: Boolean, predicate: (T) -> Boolean): List<T> {
    return if (condition) filterTo(ArrayList(), predicate) else this
}
