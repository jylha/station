package dev.jylha.station.ui.stations

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.MyLocation
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import dev.jylha.station.R
import dev.jylha.station.model.Station
import dev.jylha.station.ui.Screen
import dev.jylha.station.ui.common.AmbientLocationPermission
import dev.jylha.station.ui.common.EmptyState
import dev.jylha.station.ui.common.Loading
import dev.jylha.station.ui.common.SearchBar
import dev.jylha.station.ui.common.withPermission
import dev.jylha.station.util.filterWhen
import dev.jylha.station.util.findAllMatches
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun StationScreen(
    navigateTo: (Screen) -> Unit,
    selectNearestStation: Boolean = false
) {
    val viewModel = viewModel<StationsViewModel>()
    onActive { viewModel.setSelectionMode(selectNearestStation) }
    val state by viewModel.state.collectAsState()

    when {
        selectNearestStation -> SelectNearestStation(state, navigateTo)
        state.isLoading -> LoadingStations()
        else -> {
            val locationPermission = AmbientLocationPermission.current
            StationsScreen(
                state,
                onSelect = { station ->
                    viewModel.stationSelected(station)
                    navigateTo(Screen.Timetable(station))
                },
                onSelectNearest = {
                    withPermission(locationPermission) { granted ->
                        if (granted) navigateTo(Screen.SelectNearest)
                    }
                }
            )
        }
    }
}

@Composable fun SelectNearestStation(state: StationsViewState, navigateTo: (Screen) -> Unit) {
    when {
        state.nearestStation != null -> navigateTo(Screen.Timetable(state.nearestStation))
        state.isFetchingLocation -> FetchingLocation()
        state.isLoading -> LoadingStations()
    }
}

@Composable fun StationsScreen(
    state: StationsViewState,
    onSelect: (Station) -> Unit,
    onSelectNearest: () -> Unit = {},
) {
    val stations = state.stations
    val recentStations = remember(stations, state.recentStations) {
        state.recentStations.mapNotNull { stationCode ->
            stations.firstOrNull { station -> station.code == stationCode }
        }
    }

    var searchEnabled by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val filteredStations = remember(searchEnabled, searchText, stations) {
        stations.filterWhen(searchEnabled) { station ->
            station.name.contains(searchText, ignoreCase = true)
        }
    }

    val searchLabel = stringResource(R.string.accessibility_label_search)
    val selectNearestLabel = stringResource(R.string.label_nearest_station)
    val searchStationLabel = stringResource(R.string.label_search_station)

    Scaffold(
        topBar = {
            if (searchEnabled) {
                SearchBar(
                    text = searchText,
                    placeholderText = searchStationLabel,
                    modifier = Modifier.semantics { contentDescription = searchLabel },
                    onValueChanged = { value -> searchText = value },
                    onClose = { searchEnabled = false; searchText = "" }
                )
            } else {
                TopAppBar(
                    title = { Text(stringResource(R.string.label_select_station)) },
                    actions = {
                        IconButton(
                            onClick = onSelectNearest,
                            modifier = Modifier.semantics {
                                contentDescription = selectNearestLabel
                            }
                        ) { Icon(Icons.Rounded.MyLocation, contentDescription = null) }
                        IconButton(
                            onClick = { searchEnabled = true },
                            modifier = Modifier.semantics {
                                contentDescription = searchStationLabel
                            }
                        ) { Icon(Icons.Default.Search, contentDescription = null) }
                    }
                )
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when {
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
    val message = stringResource(R.string.message_retrieving_location)
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
        if (searchText.isNullOrBlank() && recentStations.isNotEmpty()) {
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
    val textColor = if (searchText.isNullOrBlank()) MaterialTheme.colors.onBackground else
        MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
    val highlightedTextColor = MaterialTheme.colors.onBackground
    val name = remember(station.name, searchText) {
        with(AnnotatedString.Builder(station.name)) {
            if (!searchText.isNullOrBlank()) {
                station.name.findAllMatches(searchText).forEach { (startIndex, endIndex) ->
                    addStyle(
                        SpanStyle(highlightedTextColor, fontWeight = FontWeight.Bold),
                        startIndex,
                        endIndex
                    )
                }
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
        Text(name, style = MaterialTheme.typography.body1, color = textColor)
    }
}
