package dev.jylha.station.ui.stations

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.MyLocation
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.google.accompanist.insets.imePadding
import dev.jylha.station.R
import dev.jylha.station.model.Station
import dev.jylha.station.ui.common.EmptyState
import dev.jylha.station.ui.common.Loading
import dev.jylha.station.ui.common.LocalLocationPermission
import dev.jylha.station.ui.common.SearchBar
import dev.jylha.station.ui.common.withPermission
import dev.jylha.station.ui.theme.StationTheme
import dev.jylha.station.util.filterWhen

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

@Composable fun StationsScreen(
    viewState: StationsViewState,
    onSelect: (Station) -> Unit,
    onSelectNearest: () -> Unit,
) {
    val allStations = viewState.stations
    val recentStations = remember(allStations, viewState.recentStations) {
        viewState.recentStations.mapNotNull { stationCode ->
            allStations.firstOrNull { station -> station.code == stationCode }
        }
    }

    var searchEnabled by rememberSaveable { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf("") }

    val matchingStations = remember(searchEnabled, searchText, allStations) {
        allStations.filterWhen(searchEnabled) { station ->
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
                    title = {
                        if (!viewState.selectNearest) {
                            Text(stringResource(R.string.label_select_station))
                        }
                    },
                    actions = {
                        if (!viewState.selectNearest) {
                            IconButton(onClick = onSelectNearest) {
                                Icon(
                                    Icons.Rounded.MyLocation,
                                    contentDescription = selectNearestLabel
                                )
                            }
                            IconButton(onClick = { searchEnabled = true }) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = searchStationLabel
                                )
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding).imePadding()
        when {
            viewState.nearestStation != null -> LaunchedEffect(viewState.nearestStation.code) {
                onSelect(viewState.nearestStation)
            }
            viewState.isFetchingLocation -> FetchingLocation()
            viewState.isLoading -> LoadingStations()
            matchingStations.isEmpty() -> NoMatchingStations(modifier)
            else -> StationList(
                recentStations = recentStations,
                stations = matchingStations,
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

@Preview(group = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(group = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewStationsScreen(
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
