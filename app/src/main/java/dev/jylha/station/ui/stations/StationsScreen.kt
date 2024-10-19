package dev.jylha.station.ui.stations

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.jylha.station.R
import dev.jylha.station.model.Station
import dev.jylha.station.ui.common.EmptyState
import dev.jylha.station.ui.common.Loading
import dev.jylha.station.ui.common.LocalLocationPermission
import dev.jylha.station.ui.common.SearchBar
import dev.jylha.station.ui.common.withPermission
import dev.jylha.station.ui.theme.StationTheme
import dev.jylha.station.util.filterWhen
import kotlinx.collections.immutable.toImmutableList

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
@Composable
fun StationsScreen(
    viewModel: StationsViewModel,
    onNavigateToTimetable: (Int) -> Unit,
    onNavigateToNearestStation: () -> Unit,
    selectNearestStation: Boolean = false
) {
    rememberSaveable(selectNearestStation) {
        viewModel.setSelectionMode(selectNearestStation)
        selectNearestStation
    }
    val viewState by viewModel.state.collectAsState()

    val locationPermission = LocalLocationPermission.current
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

@Composable
fun StationsScreen(
    state: StationsViewState,
    onSelect: (Station) -> Unit,
    onSelectNearest: () -> Unit,
) {
    val allStations = state.stations
    val recentStations = remember(allStations, state.recentStations) {
        state.recentStations.mapNotNull { stationCode ->
            allStations.firstOrNull { station -> station.code == stationCode }
        }.toImmutableList()
    }

    var searchEnabled by rememberSaveable { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf("") }

    val matchingStations = remember(searchEnabled, searchText, allStations) {
        allStations.filterWhen(searchEnabled) { station ->
            station.name.contains(searchText, ignoreCase = true)
        }.toImmutableList()
    }
    val setSearchState: (Boolean) -> Unit = { enabled ->
        searchText = ""
        searchEnabled = enabled
    }

    Scaffold(
        topBar = {
            StationsListTopAppBar(
                searchEnabled,
                onSearchEnabled = setSearchState,
                searchText,
                onSearchTextChanged = { text -> searchText = text },
                showContent = !state.selectNearest,
                onSelectNearest,
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding).imePadding()
        when {
            state.nearestStation != null -> LaunchedEffect(state.nearestStation.code) {
                onSelect(state.nearestStation)
            }

            state.isFetchingLocation -> FetchingLocation()
            state.isLoading -> LoadingStations()
            matchingStations.isEmpty() -> NoMatchingStations(modifier)
            else -> StationList(
                recentStations = recentStations,
                stations = matchingStations,
                onSelect = { station -> setSearchState(false); onSelect(station) },
                modifier,
                searchText
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StationsListTopAppBar(
    searchEnabled: Boolean,
    onSearchEnabled: (Boolean) -> Unit,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    showContent: Boolean,
    onSelectNearest: () -> Unit,
) {
    val searchLabel = stringResource(R.string.accessibility_label_search)
    val searchStationLabel = stringResource(R.string.label_search_station)
    if (searchEnabled) {
        SearchBar(
            text = searchText,
            placeholderText = searchStationLabel,
            modifier = Modifier.semantics { contentDescription = searchLabel },
            onValueChanged = onSearchTextChanged,
            onClose = { onSearchEnabled(false) }
        )
    } else {
        TopAppBar(
            title = {
                if (showContent) Text(stringResource(R.string.label_select_station))
            },
            actions = {
                if (showContent) {
                    IconButton(onClick = onSelectNearest) {
                        Icon(
                            Icons.Rounded.MyLocation,
                            contentDescription = stringResource(R.string.label_nearest_station)
                        )
                    }
                    IconButton(onClick = { onSearchEnabled(true) }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = searchStationLabel
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
        )
    }
}

@Composable
private fun LoadingStations(modifier: Modifier = Modifier) {
    val message = stringResource(R.string.message_loading_stations)
    Loading(message, modifier)
}

@Composable
private fun FetchingLocation(modifier: Modifier = Modifier) {
    val message = stringResource(R.string.message_retrieving_location)
    Loading(message, modifier)
}

@Composable
private fun NoMatchingStations(modifier: Modifier = Modifier) {
    val message = stringResource(R.string.message_no_matching_stations)
    EmptyState(message, modifier)
}

@PreviewLightDark
@Composable
private fun StationsScreenPreview(
    @PreviewParameter(StationsViewStateProvider::class) state: StationsViewState
) {
    StationTheme {
        StationsScreen(state, onSelect = {}, onSelectNearest = {})
    }
}

internal class StationsViewStateProvider : PreviewParameterProvider<StationsViewState> {
    companion object {

        private val stations = listOf(
            Station("Hanko", "HNK", 1, 1.0, 1.0),
            Station("Helsinki", "HKI", 2, 1.0, 1.0),
            Station("Hämeenlinna", "HL", 3, 1.0, 1.0),
            Station("Pasila", "PSL", 4, 1.0, 1.0),
        )

        private val parameters: List<StationsViewState> = listOf(
            StationsViewState(stations = stations),
            StationsViewState(stations = stations, recentStations = listOf(2, 4)),
        )
    }

    override val count: Int = parameters.size
    override val values: Sequence<StationsViewState> = parameters.asSequence()
}
