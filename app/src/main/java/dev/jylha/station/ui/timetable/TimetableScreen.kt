package dev.jylha.station.ui.timetable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Train
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jylha.station.R
import dev.jylha.station.data.stations.LocalizedStationNames
import dev.jylha.station.model.Station
import dev.jylha.station.model.Stop
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import dev.jylha.station.model.Train.Category
import dev.jylha.station.model.arrival
import dev.jylha.station.model.arrivalAfter
import dev.jylha.station.model.departure
import dev.jylha.station.model.departureAfter
import dev.jylha.station.model.stopsAt
import dev.jylha.station.model.timeOfNextEvent
import dev.jylha.station.ui.common.CauseCategoriesProvider
import dev.jylha.station.ui.common.EmptyState
import dev.jylha.station.ui.common.ErrorState
import dev.jylha.station.ui.common.Loading
import dev.jylha.station.ui.common.RefreshIndicator
import dev.jylha.station.ui.common.StationNameProvider
import dev.jylha.station.ui.common.SwipeToRefreshLayout
import dev.jylha.station.ui.common.stateSaver
import dev.jylha.station.ui.common.stationName
import dev.jylha.station.ui.theme.StationTheme
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
        viewModel::offer,
        onTrainSelected = { train ->
            val departureDate = train.departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            onNavigateToTrainDetails(departureDate, train.number)
        },
        onSelectStation = onNavigateToStations,
        onRetry = { viewModel.offer(TimetableEvent.LoadTimetable(stationCode)) },
    )
}

@Composable
fun TimetableScreen(
    viewState: TimetableViewState,
    onEvent: (TimetableEvent) -> Unit = {},
    onTrainSelected: (Train) -> Unit = {},
    onSelectStation: () -> Unit = {},
    onRetry: () -> Unit = {},
) {
    when {
        viewState.isLoadingTimetable -> LoadingTimetable()
        viewState.loadingTimetableFailed -> LoadingTimetableFailed(onRetry)
        viewState.station != null -> {
            StationNameProvider(viewState.stationNameMapper) {
                CauseCategoriesProvider(causeCategories = viewState.causeCategories) {
                    TimetableScreen(
                        viewState.station,
                        viewState.timetable,
                        selectedTimetableTypes = viewState.selectedTimetableTypes,
                        onTimetableTypesChanged = { types: Set<TimetableRow.Type> ->
                            onEvent(TimetableEvent.SelectTimetableTypes(types))
                        },
                        selectedTrainCategories = viewState.selectedTrainCategories,
                        onTrainCategoriesChanged = { categories: Set<Category> ->
                            onEvent(TimetableEvent.SelectCategories(categories))
                        },
                        isReloading = viewState.isReloadingTimetable,
                        onReload = { onEvent(TimetableEvent.ReloadTimetable(viewState.station)) },
                        onSelectStation = onSelectStation,
                        onTrainSelected = onTrainSelected
                    )
                }
            }
        }
        else -> ErrorState("Oops. Something went wrong.") {
            Button(onClick = onSelectStation) {
                Text(stringResource(R.string.label_select_station))
            }
        }
    }
}

@Composable private fun TimetableScreen(
    station: Station,
    timetable: List<Train>,
    selectedTimetableTypes: Set<TimetableRow.Type>,
    onTimetableTypesChanged: (Set<TimetableRow.Type>) -> Unit,
    selectedTrainCategories: Set<Category>,
    onTrainCategoriesChanged: (Set<Category>) -> Unit,
    isReloading: Boolean,
    onReload: () -> Unit,
    onSelectStation: () -> Unit,
    onTrainSelected: (Train) -> Unit
) {
    var filterSelectionEnabled by rememberSaveable(saver = stateSaver()) { mutableStateOf(false) }

    val timetableTypeSelected: (TimetableRow.Type) -> Unit = { type ->
        val updatedTypes =
            if (selectedTimetableTypes.contains(type)) {
                if (type == TimetableRow.Type.Arrival) {
                    setOf(TimetableRow.Type.Departure)
                } else {
                    setOf(TimetableRow.Type.Arrival)
                }
            } else {
                selectedTimetableTypes + type
            }
        onTimetableTypesChanged(updatedTypes)
    }

    val trainCategorySelected: (Category) -> Unit = { category ->
        val updatedCategories =
            if (selectedTrainCategories.contains(category)) {
                if (category == Category.LongDistance) {
                    setOf(Category.Commuter)
                } else {
                    setOf(Category.LongDistance)
                }
            } else {
                selectedTrainCategories + category
            }
        onTrainCategoriesChanged(updatedCategories)
    }

    Scaffold(topBar = {
        TimetableTopAppBar(
            stationName(stationCode = station.code),
            selectedTimetableTypes,
            selectedTrainCategories,
            filterSelectionEnabled,
            onShowFilters = { filterSelectionEnabled = true },
            onHideFilters = { filterSelectionEnabled = false },
            onSelectStation
        )
    }) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        TimetableScreenContent(
            station = station,
            trains = timetable,
            modifier,
            onTrainSelected,
            selectedTimetableTypes,
            timetableTypeSelected,
            selectedTrainCategories,
            trainCategorySelected,
            filterSelectionEnabled,
            refreshing = isReloading,
            onRefresh = onReload
        )
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
    station: Station,
    trains: List<Train>,
    modifier: Modifier = Modifier,
    onTrainSelected: (Train) -> Unit,
    selectedTimetableTypes: Set<TimetableRow.Type>,
    timetableTypeSelected: (TimetableRow.Type) -> Unit,
    selectedTrainCategories: Set<Category>,
    trainCategorySelected: (Category) -> Unit,
    showFilterSelection: Boolean = false,
    refreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    val matchingTrains by remember(trains, selectedTrainCategories) {
        mutableStateOf(trains.filter { selectedTrainCategories.contains(it.category) })
    }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = modifier.fillMaxSize()
    ) {
        Column {
            AnimatedVisibility(visible = showFilterSelection) {
                FilterSelection(
                    selectedTimetableTypes, timetableTypeSelected,
                    selectedTrainCategories, trainCategorySelected
                )
            }
            SwipeToRefreshLayout(refreshing, onRefresh, refreshIndicator = { RefreshIndicator() }
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

@Preview(name = "FilterSelection - light", "Filter selection")
@Composable private fun PreviewLightFilterSelection() {
    StationTheme(darkTheme = false) {
        FilterSelection(setOf(TimetableRow.Type.Arrival), {}, setOf(Category.LongDistance), {})
    }
}

@Preview(name = "FilterSelection - dark", "Filter selection")
@Composable private fun PreviewDarkFilterSelection() {
    StationTheme(darkTheme = true) {
        FilterSelection(setOf(TimetableRow.Type.Arrival), {}, setOf(Category.LongDistance), {})
    }
}

@Composable private fun FilterSelection(
    timetableTypes: Set<TimetableRow.Type>,
    timetableTypeSelected: (TimetableRow.Type) -> Unit,
    categories: Set<Category>,
    categorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier.fillMaxWidth(), elevation = 2.dp) {
        Column(Modifier.padding(8.dp)) {
            TimetableTypeSelection(timetableTypes, timetableTypeSelected)
            Spacer(modifier = Modifier.height(8.dp))
            CategorySelection(categories, categorySelected)
        }
    }
}

@Composable private fun TimetableTypeSelection(
    timetableTypes: Set<TimetableRow.Type>,
    timetableTypeSelected: (TimetableRow.Type) -> Unit,
    modifier: Modifier = Modifier
) {
    val arrivingLabel = if (timetableTypes.contains(TimetableRow.Type.Arrival))
        stringResource(R.string.accessibility_label_hide_arriving_trains)
    else
        stringResource(R.string.accessibility_label_show_arriving_trains)

    val departingLabel = if (timetableTypes.contains(TimetableRow.Type.Departure))
        stringResource(R.string.accessibility_label_hide_departing_trains)
    else
        stringResource(R.string.accessibility_label_show_departing_trains)

    Row(modifier.fillMaxWidth()) {
        SelectionButton(
            onClick = { timetableTypeSelected(TimetableRow.Type.Arrival) },
            selected = timetableTypes.contains(TimetableRow.Type.Arrival),
            Modifier.weight(1f).semantics { contentDescription = arrivingLabel }
        ) {
            Icon(
                painterResource(R.drawable.ic_arrival),
                contentDescription = null,
                Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.timetable_type_arriving))
        }
        Spacer(modifier = Modifier.width(8.dp))
        SelectionButton(
            onClick = { timetableTypeSelected(TimetableRow.Type.Departure) },
            selected = timetableTypes.contains(TimetableRow.Type.Departure),
            Modifier.weight(1f).semantics { contentDescription = departingLabel }
        ) {
            Text(stringResource(R.string.timetable_type_departing))
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painterResource(R.drawable.ic_departure),
                contentDescription = null,
                Modifier.size(24.dp)
            )
        }
    }
}

@Composable private fun CategorySelection(
    categories: Set<Category>,
    categorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val image = remember { Icons.Rounded.Train }
    val longDistanceLabel = if (categories.contains(Category.LongDistance))
        stringResource(R.string.accessibility_label_hide_long_distance_trains)
    else
        stringResource(R.string.accessibility_label_show_long_distance_trains)

    val commuterLabel = if (categories.contains(Category.Commuter))
        stringResource(R.string.accessibility_label_hide_commuter_trains)
    else
        stringResource(R.string.accessibility_label_show_commuter_trains)

    Row(modifier.fillMaxWidth()) {
        SelectionButton(
            onClick = { categorySelected(Category.LongDistance) },
            selected = categories.contains(Category.LongDistance),
            Modifier.weight(1f).semantics { contentDescription = longDistanceLabel }
        ) {
            Icon(image, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.category_long_distance_trains))
        }
        Spacer(Modifier.width(8.dp))
        SelectionButton(
            onClick = { categorySelected(Category.Commuter) },
            selected = categories.contains(Category.Commuter),
            Modifier.weight(1f).semantics { contentDescription = commuterLabel }
        ) {
            Icon(image, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.category_commuter_trains))
        }
    }
}

@Composable private fun SelectionButton(
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (MaterialTheme.colors.isLight) {
        LightSelectionButton(onClick, selected, modifier, content)
    } else {
        DarkSelectionButton(onClick, selected, modifier, content)
    }
}

@Composable private fun LightSelectionButton(
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Button(
        onClick,
        modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (selected) MaterialTheme.colors.primaryVariant else Color.Gray,
            contentColor = MaterialTheme.colors.onPrimary
        )
    ) { content() }
}

@Composable private fun DarkSelectionButton(
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val color = if (selected) MaterialTheme.colors.primaryVariant.copy(alpha = 0.9f)
    else Color.Gray.copy(alpha = 0.7f)

    OutlinedButton(
        onClick,
        modifier,
        border = BorderStroke(2.dp, color),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = color,
        ),
    ) { content() }
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
            TimetableScreenContent(helsinki, trains, Modifier, {},
                setOf(TimetableRow.Type.Arrival, TimetableRow.Type.Departure), {},
                setOf(Category.LongDistance), {})
        }
    }
}
