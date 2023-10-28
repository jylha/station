package dev.jylha.station.ui.timetable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jylha.station.R
import dev.jylha.station.data.stations.LocalizedStationNames
import dev.jylha.station.model.Station
import dev.jylha.station.model.Stop
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import dev.jylha.station.model.Train.Category
import dev.jylha.station.model.arrival
import dev.jylha.station.model.departure
import dev.jylha.station.ui.LightAndDarkPreviews
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
 * Timetable screen displays the timetable for the specified train station.
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
    onNavigateToTrainDetails: (departureDate: String, trainNumber: Int) -> Unit,
) {
    rememberSaveable(stationCode) {
        viewModel.offer(TimetableEvent.LoadTimetable(stationCode))
        stationCode
    }
    val state by viewModel.state.collectAsState()

    TimetableScreen(
        state = state,
        stationCode = stationCode,
        onEvent = viewModel::offer,
        onTrainSelected = { train ->
            onNavigateToTrainDetails(train.departureDateString, train.number)
        },
        onSelectStation = onNavigateToStations,
        onRetry = { viewModel.offer(TimetableEvent.LoadTimetable(stationCode)) },
    )
}

/**
 * Timetable screen.
 *
 * @param state A UI state of the timetable screen.
 * @param stationCode The UIC code of a train station.
 * @param modifier An optional modifier applied to the screen.
 * @param onEvent Called to handle a timetable event.
 * @param onRetry Called to navigate to a train details screen.
 * @param onSelectStation Called to navigate to the station list screen.
 * @param onRetry Called to reload the timetable.
 */
@Composable
fun TimetableScreen(
    state: TimetableViewState,
    stationCode: Int,
    modifier: Modifier = Modifier,
    onEvent: (TimetableEvent) -> Unit = {},
    onTrainSelected: (Train) -> Unit = {},
    onSelectStation: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    var filtersVisible by rememberSaveable { mutableStateOf(false) }

    StationNameProvider(state.stationNameMapper) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TimetableTopAppBar(
                    stationName = stationName(stationCode = stationCode),
                    selectedTimetableTypes = state.selectedTimetableTypes,
                    selectedTrainCategories = state.selectedTrainCategories,
                    filterSelectionEnabled = filtersVisible,
                    onShowFilters = remember { { filtersVisible = true } },
                    onHideFilters = remember { { filtersVisible = false } },
                    onSelectStation = onSelectStation
                )
            },
        ) { paddingValues ->
            CauseCategoriesProvider(state.causeCategories) {
                TimetableScreenContent(
                    state = state,
                    timetablesFiltersVisible = filtersVisible,
                    onEvent = onEvent,
                    onRetry = onRetry,
                    onTrainSelected = onTrainSelected,
                    onSelectStation = onSelectStation,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun TimetableScreenContent(
    state: TimetableViewState,
    timetablesFiltersVisible: Boolean,
    onEvent: (TimetableEvent) -> Unit,
    onRetry: () -> Unit,
    onTrainSelected: (Train) -> Unit,
    onSelectStation: () -> Unit,
    modifier: Modifier,
) {
    Column(modifier.fillMaxSize()) {
        AnimatedVisibility(timetablesFiltersVisible) {
            TimetableFilterSelection(
                timetableTypes = state.selectedTimetableTypes,
                onTimetableTypesChanged = { timetableTypes ->
                    onEvent(TimetableEvent.SelectTimetableTypes(timetableTypes))
                },
                trainCategories = state.selectedTrainCategories,
                onTrainCategoriesChanged = { categories ->
                    onEvent(TimetableEvent.SelectCategories(categories))
                },
            )
        }
        when {
            state.isLoadingTimetable -> LoadingTimetable()
            state.loadingTimetableFailed -> LoadingTimetableFailed(onRetry)
            state.station != null -> Timetable(
                station = state.station,
                trains = Trains(state.timetable),
                timetableTypes = state.selectedTimetableTypes,
                trainCategories = state.selectedTrainCategories,
                onTrainSelected = onTrainSelected,
                refreshing = state.isReloadingTimetable,
                onRefresh = { onEvent(TimetableEvent.ReloadTimetable(state.station)) },
            )

            else -> ErrorState("Oops. Something went wrong.") {
                Button(onClick = onSelectStation) {
                    Text(stringResource(R.string.label_select_station))
                }
            }
        }
    }
}

@Composable
private fun LoadingTimetable(modifier: Modifier = Modifier) {
    val message = stringResource(R.string.message_loading_timetable)
    Loading(message, modifier)
}

@Composable
private fun LoadingTimetableFailed(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val message = stringResource(R.string.message_loading_timetable_failed)
    ErrorState(message, modifier) {
        Button(onClick = onRetry, Modifier.width(180.dp)) {
            Text(stringResource(R.string.label_retry))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Timetable(
    station: Station,
    trains: Trains,
    timetableTypes: TimetableTypes,
    trainCategories: TrainCategories,
    onTrainSelected: (Train) -> Unit,
    modifier: Modifier = Modifier,
    refreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    val matchingTrains by remember(trains, trainCategories) {
        mutableStateOf(
            Trains(trains.filter { trainCategories.contains(it.category) })
        )
    }
    val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh)
    Box(
        modifier = modifier.pullRefresh(pullRefreshState).clipToBounds(),
    ) {
        when {
            trains.isEmpty() -> EmptyTimetable()
            matchingTrains.isEmpty() -> NoMatchingTrains()
            else -> Timetable(
                station,
                matchingTrains,
                onTrainSelected,
                timetableTypes
            )
        }
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun EmptyTimetable() {
    val message = stringResource(R.string.message_empty_timetable)
    EmptyState(message = message)
}

@Composable
private fun NoMatchingTrains() {
    val message = stringResource(R.string.message_no_matching_trains)
    EmptyState(message = message)
}

@Composable
private fun Timetable(
    station: Station,
    trains: Trains,
    onSelect: (Train) -> Unit,
    selectedTimetableTypes: TimetableTypes,
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
        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(stops) { (train, stop) ->
                TimetableEntry(train, stop, onSelect)
            }
        }
    }
}

@LightAndDarkPreviews
@Composable
private fun TimetablePreview() {
    val helsinki = Station(
        name = "Helsinki", shortCode = "HKI", code = 1,
        longitude = 1.0, latitude = 1.0
    )
    val turku = Station(
        name = "Turku Central Station", shortCode = "TKU", code = 130,
        longitude = 1.0, latitude = 1.0
    )
    val date = LocalDate.parse("2020-01-01")
    val trains = Trains(
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
    StationTheme {
        StationNameProvider(mapper) {
            Surface(color = MaterialTheme.colorScheme.background) {
                Timetable(
                    station = helsinki,
                    trains = trains,
                    timetableTypes = TimetableTypes(
                        TimetableRow.Type.Arrival,
                        TimetableRow.Type.Departure
                    ),
                    trainCategories = TrainCategories(Category.LongDistance),
                    onTrainSelected = {},
                )
            }
        }
    }
}
