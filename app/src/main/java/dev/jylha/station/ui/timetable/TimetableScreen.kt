package dev.jylha.station.ui.timetable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.jylha.station.R
import dev.jylha.station.data.stations.LocalizedStationNames
import dev.jylha.station.model.Station
import dev.jylha.station.model.Stop
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import dev.jylha.station.model.Train.Category
import dev.jylha.station.model.arrival
import dev.jylha.station.model.departure
import dev.jylha.station.ui.common.CauseCategoriesProvider
import dev.jylha.station.ui.common.EmptyState
import dev.jylha.station.ui.common.ErrorState
import dev.jylha.station.ui.common.Loading
import dev.jylha.station.ui.common.StationNameProvider
import dev.jylha.station.ui.common.stationName
import dev.jylha.station.ui.theme.StationTheme
import java.time.LocalDate
import java.time.ZonedDateTime

/**
 * Timetable screen composable. Timetable screen displays the timetable for the
 * specified station.
 *
 * @param viewModel View model for the timetable screen.
 * @param stationCode The UIC code specifying the station.
 * @param onNavigateToStations A callback function to navigate to stations list.
 * @param onNavigateToTrainDetails A callback function to navigate to train details.
 */
@Composable
fun TimetableScreen(
    viewModel: TimetableViewModel,
    stationCode: Int,
    onNavigateToStations: () -> Unit,
    onNavigateToTrainDetails: (String, Int) -> Unit,
) {
    rememberSaveable(stationCode) {
        viewModel.offer(TimetableEvent.LoadTimetable(stationCode))
        stationCode
    }
    val viewState by viewModel.state.collectAsState()

    TimetableScreen(
        viewState,
        stationCode,
        onEvent = viewModel::offer,
        onTrainSelected = { train ->
            onNavigateToTrainDetails(train.departureDateString, train.number)
        },
        onSelectStation = onNavigateToStations,
        onRetry = { viewModel.offer(TimetableEvent.LoadTimetable(stationCode)) },
    )
}

@Composable
fun TimetableScreen(
    viewState: TimetableViewState,
    stationCode: Int,
    modifier: Modifier = Modifier,
    onEvent: (TimetableEvent) -> Unit = {},
    onTrainSelected: (Train) -> Unit = {},
    onSelectStation: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    var showTimetableFilters by rememberSaveable { mutableStateOf(false) }

    StationNameProvider(viewState.stationNameMapper) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TimetableTopAppBar(
                    stationName = stationName(stationCode = stationCode),
                    selectedTimetableTypes = viewState.selectedTimetableTypes,
                    selectedTrainCategories = viewState.selectedTrainCategories,
                    filterSelectionEnabled = showTimetableFilters,
                    onShowFilters = { showTimetableFilters = true },
                    onHideFilters = { showTimetableFilters = false },
                    onSelectStation = onSelectStation
                )
            }
        ) { paddingValues ->
            CauseCategoriesProvider(viewState.causeCategories) {
                TimetableScreenContent(
                    viewState,
                    showTimetableFilters,
                    onEvent,
                    onRetry,
                    onTrainSelected,
                    onSelectStation,
                    Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable private fun TimetableTopAppBar(
    stationName: String?,
    selectedTimetableTypes: Set<TimetableRow.Type>,
    selectedTrainCategories: Set<Category>,
    filterSelectionEnabled: Boolean,
    onShowFilters: () -> Unit,
    onHideFilters: () -> Unit,
    onSelectStation: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column(modifier) {
                TopAppBarTitle(stationName)
                TopAppBarSubtitle(selectedTimetableTypes, selectedTrainCategories)
            }
        },
        actions = {
            val selectStationLabel = stringResource(R.string.label_select_station)
            IconButton(onClick = onSelectStation) {
                Icon(Icons.Rounded.LocationCity, contentDescription = selectStationLabel)
            }
            if (filterSelectionEnabled) {
                val hideFiltersLabel = stringResource(R.string.label_hide_filters)
                IconButton(onClick = onHideFilters) {
                    Icon(Icons.Default.ExpandLess, contentDescription = hideFiltersLabel)
                }
            } else {
                val showFiltersLabel = stringResource(R.string.label_show_filters)
                IconButton(onClick = onShowFilters) {
                    Icon(Icons.Default.FilterList, contentDescription = showFiltersLabel)
                }
            }
        }
    )
}

/** A title displaying the station name. */
@Composable private fun TopAppBarTitle(stationName: String?, modifier: Modifier = Modifier) {
    val titleText = stationName ?: stringResource(id = R.string.title_timetable)
    Text(titleText, modifier)
}

/** A subtitle displaying the selected categories. */
@Composable private fun TopAppBarSubtitle(
    timetableTypes: Set<TimetableRow.Type>,
    trainCategories: Set<Category>,
    modifier: Modifier = Modifier
) {
    val subtitleText = if (trainCategories.size == 1) {
        if (trainCategories.contains(Category.LongDistance)) {
            if (timetableTypes.size == 1) {
                if (timetableTypes.contains(TimetableRow.Type.Arrival)) {
                    stringResource(id = R.string.subtitle_arriving_long_distance_trains)
                } else {
                    stringResource(id = R.string.subtitle_departing_long_distance_trains)
                }
            } else {
                stringResource(id = R.string.subtitle_long_distance_trains)
            }
        } else {
            if (timetableTypes.size == 1) {
                if (timetableTypes.contains(TimetableRow.Type.Arrival)) {
                    stringResource(id = R.string.subtitle_arriving_commuter_trains)
                } else {
                    stringResource(id = R.string.subtitle_departing_commuter_trains)
                }
            } else {
                stringResource(id = R.string.subtitle_commuter_trains)
            }
        }
    } else {
        if (timetableTypes.size == 1) {
            if (timetableTypes.contains(TimetableRow.Type.Arrival)) {
                stringResource(id = R.string.subtitle_arriving_trains)
            } else {
                stringResource(id = R.string.subtitle_departing_trains)
            }
        } else {
            stringResource(id = R.string.subtitle_all_trains)
        }
    }
    Text(subtitleText, modifier, style = MaterialTheme.typography.caption)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable private fun TimetableScreenContent(
    viewState: TimetableViewState,
    showTimetableFilters: Boolean,
    onEvent: (TimetableEvent) -> Unit,
    onRetry: () -> Unit,
    onTrainSelected: (Train) -> Unit,
    onSelectStation: () -> Unit,
    modifier: Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colors.background,
    ) {
        Column(Modifier.fillMaxSize()) {
            AnimatedVisibility(showTimetableFilters) {
                TimetableFilterSelection(
                    timetableTypes = viewState.selectedTimetableTypes,
                    onTimetableTypesChanged = { timetableTypes ->
                        onEvent(TimetableEvent.SelectTimetableTypes(timetableTypes))
                    },
                    categories = viewState.selectedTrainCategories,
                    onCategoriesChanged = { categories ->
                        onEvent(TimetableEvent.SelectCategories(categories))
                    }
                )
            }
            when {
                viewState.isLoadingTimetable -> LoadingTimetable()
                viewState.loadingTimetableFailed -> LoadingTimetableFailed(onRetry)
                viewState.station != null -> Timetable(
                    station = viewState.station,
                    trains = viewState.timetable,
                    selectedTimetableTypes = viewState.selectedTimetableTypes,
                    selectedTrainCategories = viewState.selectedTrainCategories,
                    onTrainSelected = onTrainSelected,
                    refreshing = viewState.isReloadingTimetable,
                    onRefresh = { onEvent(TimetableEvent.ReloadTimetable(viewState.station)) }
                )
                else -> ErrorState("Oops. Something went wrong.") {
                    Button(onClick = onSelectStation) {
                        Text(stringResource(R.string.label_select_station))
                    }
                }
            }
        }
    }
}

@Composable private fun LoadingTimetable(modifier: Modifier = Modifier) {
    val message = stringResource(R.string.message_loading_timetable)
    Loading(message, modifier)
}

@Composable private fun LoadingTimetableFailed(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val message = stringResource(R.string.message_loading_timetable_failed)
    ErrorState(message, modifier) {
        Button(onClick = onRetry, Modifier.width(180.dp)) {
            Text(stringResource(R.string.label_retry))
        }
    }
}

@Composable private fun Timetable(
    station: Station,
    trains: List<Train>,
    selectedTimetableTypes: Set<TimetableRow.Type>,
    selectedTrainCategories: Set<Category>,
    onTrainSelected: (Train) -> Unit = {},
    refreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    val matchingTrains by remember(trains, selectedTrainCategories) {
        mutableStateOf(trains.filter { selectedTrainCategories.contains(it.category) })
    }

    val swipeRefreshState = rememberSwipeRefreshState(refreshing)
    SwipeRefresh(
        swipeRefreshState, onRefresh,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state, trigger, contentColor = MaterialTheme.colors.primary
            )
        }
    ) {
        when {
            trains.isEmpty() -> EmptyTimetable()
            matchingTrains.isEmpty() -> NoMatchingTrains()
            else -> Timetable(
                station,
                matchingTrains,
                onTrainSelected,
                selectedTimetableTypes
            )
        }
    }
}

@Composable private fun EmptyTimetable() {
    val message = stringResource(R.string.message_empty_timetable)
    EmptyState(message = message)
}

@Composable private fun NoMatchingTrains() {
    val message = stringResource(R.string.message_no_matching_trains)
    EmptyState(message = message)
}

@Composable private fun Timetable(
    station: Station,
    trains: List<Train>,
    onSelect: (Train) -> Unit,
    selectedTimetableTypes: Set<TimetableRow.Type>,
    modifier: Modifier = Modifier
) {
    val timeOfSelectedStopType = remember(selectedTimetableTypes) {
        when {
            selectedTimetableTypes.size == 2 ->
                { stop: Stop -> stop.timeOfNextEvent() }
            selectedTimetableTypes.contains(TimetableRow.Type.Arrival) ->
                { stop: Stop -> stop.arrival?.run { actualTime ?: scheduledTime } }
            selectedTimetableTypes.contains(TimetableRow.Type.Departure) ->
                { stop: Stop -> stop.departure?.run { actualTime ?: scheduledTime } }
            else -> { _ -> null }
        }
    }

    val stops = remember(trains, selectedTimetableTypes) {
        val cutoffTime = ZonedDateTime.now().minusMinutes(5)
        trains.flatMap { train ->
            train.stopsAt(station.code).map { stop -> Pair(train, stop) }
        }
            .filter { (_, stop) ->
                selectedTimetableTypes.contains(TimetableRow.Type.Arrival)
                        && stop.arrivalAfter(cutoffTime)
                        || selectedTimetableTypes.contains(TimetableRow.Type.Departure)
                        && stop.departureAfter(cutoffTime)
            }
            .sortedBy { (_, stop) -> timeOfSelectedStopType(stop) }
    }

    when {
        stops.isEmpty() -> NoMatchingTrains()
        else -> LazyColumn(modifier, contentPadding = PaddingValues(8.dp)) {
            items(stops) { (train, stop) ->
                TimetableEntry(train, stop, onSelect, Modifier.padding(bottom = 8.dp))
            }
        }
    }
}

@Preview(showBackground = true, name = "Timetable")
@Composable
private fun PreviewTimetable() {
    val helsinki = Station(
        name = "Helsinki", shortCode = "HKI", code = 1,
        longitude = 1.0, latitude = 1.0
    )
    val turku = Station(
        name = "Turku Central Station", shortCode = "TKU", code = 130,
        longitude = 1.0, latitude = 1.0
    )
    val date = LocalDate.parse("2020-01-01")
    val trains = listOf(
        Train(
            1, "S", Category.LongDistance, departureDate = date, timetable = listOf(
                departure(1, "1", ZonedDateTime.parse("2020-01-01T09:30Z")),
                arrival(130, "2", ZonedDateTime.parse("2020-01-01T10:30Z"))
            )
        ),
        Train(
            2, "HDM", Category.LongDistance, departureDate = date, timetable = listOf(
                departure(130, "3", ZonedDateTime.parse("2020-01-01T09:30Z")),
                arrival(1, "4", ZonedDateTime.parse("2020-01-01T10:30Z"))
            )
        ),
        Train(
            3, "IC", Category.LongDistance, departureDate = date, timetable = listOf(
                departure(130, "4", ZonedDateTime.parse("2020-01-01T09:45Z"), cancelled = true),
                arrival(1, "3", ZonedDateTime.parse("2020-01-01T10:50Z"), cancelled = true),
            )
        )
    )

    val mapper = LocalizedStationNames.from(listOf(helsinki, turku), LocalContext.current)
    StationTheme(darkTheme = true) {
        StationNameProvider(mapper) {
            Surface(color = MaterialTheme.colors.background) {
                Timetable(
                    helsinki, trains,
                    setOf(TimetableRow.Type.Arrival, TimetableRow.Type.Departure),
                    setOf(Category.LongDistance)
                )
            }
        }
    }
}
