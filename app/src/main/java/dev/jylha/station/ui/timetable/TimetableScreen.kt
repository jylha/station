package dev.jylha.station.ui.timetable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
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
import dev.jylha.station.ui.common.CauseCategoriesProvider
import dev.jylha.station.ui.common.EmptyState
import dev.jylha.station.ui.common.ErrorState
import dev.jylha.station.ui.common.Loading
import dev.jylha.station.ui.common.StationNameProvider
import dev.jylha.station.ui.common.stationName
import dev.jylha.station.ui.theme.StationTheme
import dev.jylha.station.ui.theme.backgroundColor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.time.Duration.Companion.minutes

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
 * @param onEvent Called to handle a timetable event.
 * @param onRetry Called to navigate to a train details screen.
 * @param onSelectStation Called to navigate to the station list screen.
 * @param onRetry Called to reload the timetable.
 */
@Composable
fun TimetableScreen(
    state: TimetableViewState,
    stationCode: Int,
    onEvent: (TimetableEvent) -> Unit = {},
    onTrainSelected: (Train) -> Unit = {},
    onSelectStation: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    var filtersVisible by rememberSaveable { mutableStateOf(false) }

    val windowInsets =
        WindowInsets.systemBars.union(WindowInsets.displayCutout)
            .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)

    StationNameProvider(state.stationNameMapper) {
        Scaffold(
            modifier = Modifier
                .background(backgroundColor())
                .windowInsetsPadding(windowInsets),
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
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ) { contentPadding ->
            CauseCategoriesProvider(state.causeCategories) {
                TimetableScreenContent(
                    state = state,
                    timetablesFiltersVisible = filtersVisible,
                    onEvent = onEvent,
                    onRetry = onRetry,
                    onTrainSelected = onTrainSelected,
                    onSelectStation = onSelectStation,
                    modifier = Modifier,
                    contentPadding = contentPadding
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
    contentPadding: PaddingValues,
) {
    Column(modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding())) {
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
                trains = state.timetable,
                timetableTypes = state.selectedTimetableTypes,
                trainCategories = state.selectedTrainCategories,
                onTrainSelected = onTrainSelected,
                refreshing = state.isReloadingTimetable,
                onRefresh = { onEvent(TimetableEvent.ReloadTimetable(state.station)) },
                contentPadding = PaddingValues(
                    start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = contentPadding.calculateBottomPadding()
                ),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Timetable(
    station: Station,
    trains: ImmutableList<Train>,
    timetableTypes: TimetableTypes,
    trainCategories: TrainCategories,
    onTrainSelected: (Train) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    refreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    val matchingTrains by remember(trains, trainCategories) {
        mutableStateOf(
            trains.filter { train -> trainCategories.contains(train.category) }
                .toImmutableList()
        )
    }

    val pullToRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        modifier = modifier.clipToBounds(),
        isRefreshing = refreshing,
        state = pullToRefreshState,
        onRefresh = onRefresh,
        indicator = {
            PullToRefreshDefaults.Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState,
                isRefreshing = refreshing,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    ) {
        when {
            trains.isEmpty() -> EmptyTimetable(Modifier.padding(paddingValues = contentPadding))
            else -> Timetable(
                station = station,
                trains = matchingTrains,
                onSelect = onTrainSelected,
                selectedTimetableTypes = timetableTypes,
                contentPadding = contentPadding
            )
        }
    }
}

@Composable
private fun EmptyTimetable(modifier: Modifier = Modifier) {
    val message = stringResource(R.string.message_empty_timetable)
    EmptyState(message = message, modifier)
}

@Composable
private fun NoMatchingTrains(modifier: Modifier = Modifier) {
    val message = stringResource(R.string.message_no_matching_trains)
    EmptyState(message = message, modifier)
}

@Composable
private fun Timetable(
    station: Station,
    trains: ImmutableList<Train>,
    onSelect: (Train) -> Unit,
    selectedTimetableTypes: TimetableTypes,
    contentPadding: PaddingValues
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
        val cutoffTime = Clock.System.now().minus(5.minutes)
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
        stops.isEmpty() -> NoMatchingTrains(Modifier.padding(paddingValues = contentPadding))
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding() + 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 8.dp,
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current) + 8.dp,
                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current) + 8.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(stops) { (train, stop) ->
                TimetableEntry(train, stop, onSelect)
            }
        }
    }
}

@PreviewLightDark
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
    val trains = persistentListOf(
        Train(
            1, "S", Category.LongDistance, departureDate = date, timetable = listOf(
                departure(1, "1", Instant.parse("2020-01-01T09:30Z")),
                arrival(130, "2", Instant.parse("2020-01-01T10:30Z"))
            )
        ),
        Train(
            2, "HDM", Category.LongDistance, departureDate = date, timetable = listOf(
                departure(130, "3", Instant.parse("2020-01-01T09:30Z")),
                arrival(1, "4", Instant.parse("2020-01-01T10:30Z"))
            )
        ),
        Train(
            3, "IC", Category.LongDistance, departureDate = date, timetable = listOf(
                departure(130, "4", Instant.parse("2020-01-01T09:45Z"), cancelled = true),
                arrival(1, "3", Instant.parse("2020-01-01T10:50Z"), cancelled = true),
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
