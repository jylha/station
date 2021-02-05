package dev.jylha.station.ui.stations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
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
import dev.jylha.station.R
import dev.jylha.station.model.Station
import dev.jylha.station.ui.common.AmbientLocationPermission
import dev.jylha.station.ui.common.EmptyState
import dev.jylha.station.ui.common.Loading
import dev.jylha.station.ui.common.SearchBar
import dev.jylha.station.ui.common.withPermission
import dev.jylha.station.util.filterWhen
import dev.jylha.station.util.findAllMatches
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Stations screen composable. Stations screen displays a list of stations to select from,
 * displays recently selected stations, and enables searching for a station by its name.
 * In addition, it handles the selection of nearest station and displays progress indicators
 * when fetching device location or list of stations.
 *
 * @param viewModel View model for the stations screen.
 * @param onNavigateToTimetable A callback function to navigate to the timetable screen of the
 * specified station.
 * @param onNavigateToNearestStation A callback function to navigate to the timetable screen
 * of the nearest station.
 * @param selectNearestStation `true` for selecting the nearest station, `false` for displaying
 * the list of train stations.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun StationsScreen(
    viewModel: StationsViewModel,
    onNavigateToTimetable: (Int) -> Unit,
    onNavigateToNearestStation: () -> Unit,
    selectNearestStation: Boolean = false
) {
    savedInstanceState(selectNearestStation) {
        viewModel.setSelectionMode(selectNearestStation)
        selectNearestStation
    }
    val viewState by viewModel.state.collectAsState()

    when {
        selectNearestStation -> SelectNearestStation(viewState, onNavigateToTimetable)
        viewState.isLoading -> LoadingStations()
        else -> {
            val locationPermission = AmbientLocationPermission.current
            StationsScreen(
                viewState,
                onSelect = { station ->
                    viewModel.stationSelected(station)
                    onNavigateToTimetable(station.code)
                },
                onSelectNearest = {
                    withPermission(locationPermission) { granted ->
                        if (granted) onNavigateToNearestStation()
                    }
                }
            )
        }
    }
}

@Composable fun SelectNearestStation(
    state: StationsViewState,
    onNavigateToTimetable: (Int) -> Unit
) {
    when {
        state.nearestStation != null -> onNavigateToTimetable(state.nearestStation.code)
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
                        IconButton(onClick = onSelectNearest) {
                            Icon(Icons.Rounded.MyLocation, contentDescription = selectNearestLabel)
                        }
                        IconButton(onClick = { searchEnabled = true }) {
                            Icon(Icons.Default.Search, contentDescription = searchStationLabel)
                        }
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
    LazyColumn(modifier = modifier) {
        if (searchText.isNullOrBlank() && recentStations.isNotEmpty()) {
            item { StationListLabel(stringResource(R.string.label_recent)) }
            items(recentStations) { station -> StationListEntry(station, onSelect) }
            item { Divider() }
        }

        item {
            StationListLabel(
                stringResource(
                    if (searchText.isNullOrBlank()) R.string.label_all_stations
                    else R.string.label_matching_stations
                )
            )
        }
        items(stations) { station -> StationListEntry(station, onSelect, searchText = searchText) }
    }
}

@Composable private fun StationListLabel(label: String) {
    Text(
        label.toUpperCase(Locale.getDefault()),
        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
        style = MaterialTheme.typography.caption,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
    )
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
