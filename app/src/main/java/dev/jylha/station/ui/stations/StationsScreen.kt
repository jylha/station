package dev.jylha.station.ui.stations

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import dev.jylha.station.util.findAllMatches

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
            else -> StationSelectionList(
                stations = matchingStations,
                recentStations = recentStations,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable private fun StationSelectionList(
    stations: List<Station>,
    recentStations: List<Station>,
    onSelect: (Station) -> Unit,
    modifier: Modifier,
    searchText: String
) {
    Surface(modifier) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
            if (searchText.isBlank() && recentStations.isNotEmpty()) {
                item { StationListLabel(stringResource(R.string.label_recent)) }
                items(recentStations) { station ->
                    StationListEntry(station.name, onSelect = { onSelect(station) })
                }
                item { Divider() }
            }

            item {
                StationListLabel(
                    stringResource(
                        if (searchText.isBlank()) R.string.label_all_stations
                        else R.string.label_matching_stations
                    )
                )
            }

            stations
                .groupBy { station -> station.name.first() }
                .forEach { (letter, group) ->
                    stickyHeader { StationListStickyLetter(letter) }
                    itemsIndexed(group) { index, station ->
                        StationListEntry(
                            stationName = station.name,
                            onSelect = { onSelect(station) },
                            modifier = Modifier
                                .then(
                                    if (index == 0) {
                                        Modifier.requiredHeight(Dp.Hairline)
                                            .wrapContentHeight(
                                                align = Alignment.Bottom,
                                                unbounded = true
                                            )
                                    } else {
                                        Modifier
                                    }
                                ),
                            searchText = searchText
                        )
                    }
                }
        }
    }
}

@Composable private fun StationListLabel(label: String) {
    Text(
        label.uppercase(),
        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
        style = MaterialTheme.typography.caption,
    )
}

@Composable private fun StationListStickyLetter(letter: Char) {
    Box(
        modifier = Modifier.requiredWidth(StickyLetterColumnWidth),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter.toString().uppercase(),
            modifier = Modifier.padding(vertical = 10.dp),
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.body1,
        )
    }
}

@Composable private fun StationListEntry(
    stationName: String,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    searchText: String = ""
) {
    val textColor = if (searchText.isBlank()) MaterialTheme.colors.onSurface else
        MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            .compositeOver(MaterialTheme.colors.surface)
    val highlightedTextColor = MaterialTheme.colors.onSurface
    val name = remember(stationName, searchText) {
        with(AnnotatedString.Builder(stationName)) {
            if (searchText.isNotBlank()) {
                stationName.findAllMatches(searchText).forEach { (startIndex, endIndex) ->
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
            .padding(start = StickyLetterColumnWidth)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onSelect)
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Text(name, style = MaterialTheme.typography.body1, color = textColor)
    }
}

val StickyLetterColumnWidth = 32.dp

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
