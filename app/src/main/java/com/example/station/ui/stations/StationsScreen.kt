package com.example.station.ui.stations

import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
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
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.example.station.R
import com.example.station.model.Station
import com.example.station.ui.Screen
import com.example.station.ui.components.EmptyState
import com.example.station.ui.components.Loading
import com.example.station.ui.components.SearchBar
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun StationScreen(
    navigateTo: (Screen) -> Unit,
    selectNearestStation: Boolean = false
) {
    val viewModel = viewModel<StationsViewModel>()
    val state by viewModel.state.collectAsState()

    onActive { viewModel.setSelectionMode(selectNearestStation) }

    if (selectNearestStation && state.selectNearest) {
        val station = state.nearestStation
        if (station != null) navigateTo(Screen.Timetable(station))
    }

    when {
        state.isLoading && state.stations.isEmpty() -> LoadingStations()
        state.isFetchingLocation || state.selectNearest -> FetchingLocation()
        else -> StationsScreen(state, onSelect = { station ->
            viewModel.stationSelected(station)
            navigateTo(Screen.Timetable(station))
        })
    }
}

@Composable fun StationsScreen(state: StationsViewState, onSelect: (Station) -> Any) {
    val stations = state.stations
    val recentStations = remember(stations, state.recentStations) {
        state.recentStations.mapNotNull { uic ->
            stations.firstOrNull { station -> station.uic == uic }
        }
    }

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
                    placeholderText = stringResource(R.string.label_search_station),
                    onValueChanged = { value -> searchText = value },
                    onClose = { searchEnabled = false; searchText = "" }
                )
            } else {
                TopAppBar(
                    title = { Text(stringResource(R.string.label_select_station)) },
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

            state.isLoading && state.stations.isEmpty() -> LoadingStations(modifier)


            filteredStations.isEmpty() -> NoMatchingStations(modifier)
            else -> StationSelection(
                filteredStations,
                recentStations,
                onSelect = onSelect,
                modifier,
                searchText
            )
        }
    }
}

@Composable private fun LoadingStations(modifier: Modifier = Modifier) {
    val message = stringResource(R.string.message_loading_stations)
    Loading(message, modifier)
}

@Composable private fun FetchingLocation(modifier: Modifier = Modifier) {
    val message = "Fetching location" // TODO: 28.9.2020 Add string resource.
    Loading(message, modifier)
}

@Composable private fun NoMatchingStations(modifier: Modifier = Modifier) {
    val message = stringResource(R.string.message_no_matching_stations)
    EmptyState(message, modifier)
}

@Composable private fun StationSelection(
    stations: List<Station>,
    recentStations: List<Station>,
    onSelect: (Station) -> Any,
    modifier: Modifier,
    searchText: String?
) {
    ScrollableColumn(modifier) {
        if (searchText.isNullOrBlank()) {
            StationList(recentStations, onSelect, stringResource(R.string.label_recent))
            Divider()
        }
        val labelResId = if (searchText.isNullOrBlank()) R.string.label_all_stations
        else R.string.label_matching_stations
        StationList(stations, onSelect, stringResource(labelResId), searchText)
    }
}

@Composable private fun StationList(
    stations: List<Station>,
    onSelect: (Station) -> Any,
    label: String? = null,
    highlightedText: String? = null
) {
    Column {
        if (!label.isNullOrBlank()) {
            Text(
                label.toUpperCase(Locale.getDefault()),
                modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
        for (station in stations) {
            StationListEntry(
                station = station,
                onSelect = onSelect,
                searchText = highlightedText
            )
        }
    }
}

@Composable private fun StationListEntry(
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
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = { onSelect(station) })
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Text(name, style = MaterialTheme.typography.body1)
    }
}

/** Filter a list with [predicate] only when given [condition] is true. */
private inline fun <T> List<T>.filterWhen(condition: Boolean, predicate: (T) -> Boolean): List<T> {
    return if (condition) filterTo(ArrayList(), predicate) else this
}
