package com.example.station.ui.timetable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Dimension
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.rounded.ArrowRightAlt
import androidx.compose.material.icons.rounded.Train
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import com.example.station.R
import com.example.station.data.stations.LocalizedStationNames
import com.example.station.model.Station
import com.example.station.model.Stop
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import com.example.station.model.Train.Category
import com.example.station.model.isDeparted
import com.example.station.model.isDestination
import com.example.station.model.isNotDeparted
import com.example.station.model.isNotReached
import com.example.station.model.isOrigin
import com.example.station.model.isReached
import com.example.station.model.isWaypoint
import com.example.station.model.stopsAt
import com.example.station.model.timeOfNextEvent
import com.example.station.model.track
import com.example.station.ui.Screen
import com.example.station.ui.components.EmptyState
import com.example.station.ui.components.Loading
import com.example.station.ui.components.RefreshIndicator
import com.example.station.ui.components.StationNameProvider
import com.example.station.ui.components.SwipeRefreshLayout
import com.example.station.ui.components.stationName
import com.example.station.ui.theme.StationTheme
import com.example.station.util.atLocalZone
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun TimetableScreen(station: Station, navigateTo: (Screen) -> Unit) {
    val viewModel = viewModel<TimetableViewModel>()
    remember(station) { viewModel.offer(TimetableEvent.LoadTimetable(station)) }
    val viewState by viewModel.state.collectAsState()

    StationNameProvider(viewState.mapper) {
        TimetableScreen(
            viewState,
            viewModel::offer,
            trainSelected = { train -> navigateTo(Screen.TrainDetails(train)) }
        )
    }
}

@Composable
fun TimetableScreen(
    viewState: TimetableViewState,
    onEvent: (TimetableEvent) -> Unit,
    trainSelected: (Train) -> Unit
) {
    var filterSelectionEnabled by remember { mutableStateOf(false) }

    val selectedTimetableTypes = viewState.selectedTimetableTypes
    val selectedTrainCategories = viewState.selectedTrainCategories

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
        onEvent(TimetableEvent.SelectTimetableTypes(updatedTypes))
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
        onEvent(TimetableEvent.SelectCategories(updatedCategories))
    }

    Scaffold(topBar = {
        TimetableTopAppBar(
            viewState.station?.name,
            selectedTimetableTypes,
            selectedTrainCategories,
            filterSelectionEnabled,
            onShowFilters = { filterSelectionEnabled = true },
            onHideFilters = { filterSelectionEnabled = false }
        )
    }) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when {
            viewState.loading -> LoadingTimetable(modifier)
            viewState.station != null -> {
                TimetableScreenContent(
                    station = viewState.station,
                    trains = viewState.timetable,
                    modifier,
                    trainSelected,
                    selectedTimetableTypes,
                    timetableTypeSelected,
                    selectedTrainCategories,
                    trainCategorySelected,
                    filterSelectionEnabled,
                    refreshing = viewState.reloading,
                    onRefresh = { onEvent(TimetableEvent.ReloadTimetable(viewState.station)) }
                )
            }
            else -> {
                // TODO: 15.9.2020 Replace with error message..
                EmptyState("Oops. Something went wrong.", modifier)
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
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Column(modifier) {
                Title(stationName)
                Subtitle(selectedTimetableTypes, selectedTrainCategories)
            }
        },
        actions = {
            if (filterSelectionEnabled) {
                IconButton(onClick = onHideFilters) { Icon(Icons.Default.ExpandLess) }
            } else {
                IconButton(onClick = onShowFilters) { Icon(Icons.Default.FilterList) }
            }
        }
    )
}

@Composable private fun LoadingTimetable(modifier: Modifier = Modifier) {
    val message = stringResource(R.string.message_loading_timetable)
    Loading(message, modifier)
}

/** A title displaying the station name. */
@Composable private fun Title(stationName: String?, modifier: Modifier = Modifier) {
    val titleText = stationName ?: stringResource(id = R.string.title_timetable)
    Text(titleText, modifier)
}

/** A subtitle displaying the selected categories. */
@Composable private fun Subtitle(
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
    val matchingTrains by remember(trains, selectedTrainCategories, selectedTimetableTypes) {
        mutableStateOf(trains
            .filter { selectedTrainCategories.contains(it.category) }
        )
    }

    SwipeRefreshLayout(
        modifier, refreshing, onRefresh, refreshIndicator = { RefreshIndicator() }
    ) {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                if (showFilterSelection) {
                    FilterSelection(
                        selectedTimetableTypes, timetableTypeSelected,
                        selectedTrainCategories, trainCategorySelected
                    )
                }
                when {
                    // TODO: 1.10.2020 Localize these.
                    trains.isEmpty() -> EmptyState("No trains scheduled to stop in the near future.")
                    matchingTrains.isEmpty() -> EmptyState("No trains of selected category scheduled in the near future.")
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

@Composable private fun Timetable(
    station: Station,
    trains: List<Train>,
    onSelect: (Train) -> Unit,
    selectedTimetableTypes: Set<TimetableRow.Type>,
    modifier: Modifier = Modifier
) {
    val stops = trains
        .flatMap { train ->
            train.stopsAt(station.uic).map { stop -> Pair(train, stop) }
        }
        .filter { (_, stop) ->
            stop.isWaypoint() ||
                    stop.isDestination() && selectedTimetableTypes.contains(TimetableRow.Type.Arrival) ||
                    stop.isOrigin() && selectedTimetableTypes.contains(TimetableRow.Type.Departure)
        }
        .sortedBy { (_, stop) -> stop.timeOfNextEvent() }

    // TODO: 1.10.2020 Sort by the time of selected timetable type.

    LazyColumnFor(
        stops, modifier, contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 0.dp)
    ) { (train, stop) ->
        TimetableEntry(train, stop, onSelect = onSelect, Modifier.padding(bottom = 8.dp))
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
    Surface(modifier.fillMaxWidth(), elevation = 4.dp) {
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
    Row(modifier.fillMaxWidth()) {
        SelectionButton(
            onClick = { timetableTypeSelected(TimetableRow.Type.Arrival) },
            selected = timetableTypes.contains(TimetableRow.Type.Arrival),
            Modifier.weight(1f)
        ) {
            Icon(vectorResource(R.drawable.ic_arrival), Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.timetable_type_arriving))
        }
        Spacer(modifier = Modifier.width(8.dp))
        SelectionButton(
            onClick = { timetableTypeSelected(TimetableRow.Type.Departure) },
            selected = timetableTypes.contains(TimetableRow.Type.Departure),
            Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.timetable_type_departing))
            Spacer(modifier = Modifier.width(8.dp))
            Icon(vectorResource(R.drawable.ic_departure), Modifier.size(24.dp))
        }
    }
}

@Composable private fun CategorySelection(
    categories: Set<Category>,
    categorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val image = remember { Icons.Rounded.Train }
    Row(modifier.fillMaxWidth()) {
        SelectionButton(
            onClick = { categorySelected(Category.LongDistance) },
            selected = categories.contains(Category.LongDistance),
            Modifier.weight(1f)
        ) {
            Icon(image)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.category_long_distance_trains))
        }
        Spacer(Modifier.width(8.dp))
        SelectionButton(
            onClick = { categorySelected(Category.Commuter) },
            selected = categories.contains(Category.Commuter),
            Modifier.weight(1f)
        ) {
            Icon(image)
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
        backgroundColor = if (selected) MaterialTheme.colors.primaryVariant else Color.Gray,
        contentColor = MaterialTheme.colors.onPrimary
    ) { content() }
}

@Composable private fun DarkSelectionButton(
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val color = if (selected) Color.Green.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.7f)

    OutlinedButton(
        onClick,
        modifier,
        contentColor = color,
        backgroundColor = Color.Transparent,
        border = BorderStroke(2.dp, color),
    ) { content() }
}

@Preview(name = "TimetableEntry - Dark", group = "TimetableEntry")
@Composable private fun PreviewDarkTimetableEntry() {
    StationTheme(darkTheme = true) {
        PreviewTimetableEntry()
    }
}

@Preview(name = "TimetableEntry - Light", group = "TimetableEntry")
@Composable private fun PreviewLightTimetableEntry() {
    StationTheme(darkTheme = false) {
        PreviewTimetableEntry()
    }
}

@Composable private fun PreviewTimetableEntry() {
    val origin = Station(
        true, Station.Type.Station, "Here", "H",
        123, "FI", 100.0, 50.0
    )
    val somewhere = Station(
        true, Station.Type.StoppingPoint, "Somewhere", "S",
        555, "FI", 50.0, 100.0
    )
    val destination = Station(
        true, Station.Type.StoppingPoint, "There", "H",
        456, "FI", 50.0, 100.0
    )
    val train = Train(
        1, "IC", Category.LongDistance, timetable = listOf(
            TimetableRow.departure("H", 123, "1", ZonedDateTime.now()),
            TimetableRow.arrival(
                "S", 555, "3", ZonedDateTime.now().plusMinutes(60),
                actualTime = ZonedDateTime.now().plusMinutes(64),
                differenceInMinutes = 4
            ),
            TimetableRow.departure("S", 555, "3", ZonedDateTime.now().plusHours(1)),
            TimetableRow.arrival("T", 456, "2", ZonedDateTime.now().plusHours(2))
        )
    )
    val stop = train.stopsAt(555).first()

    StationNameProvider(
        nameMapper = LocalizedStationNames.create(listOf(origin, somewhere, destination))
    ) {
        TimetableEntry(train, stop, {})
    }
}

@Composable fun TimetableEntry(
    train: Train,
    stop: Stop,
    onSelect: (Train) -> Unit,
    modifier: Modifier = Modifier
) {
    TimetableEntryBubble(onClick = { onSelect(train) }, modifier, statusColor(train, stop)) {
        Column {
            Row {
                TrainIdentification(train, Modifier.weight(1f))
                TrainRoute(train.origin(), train.destination(), Modifier.weight(4f))
            }
            Row {
                Arrival(stop.arrival, Modifier.weight(2f))
                TrainTrack(stop.track(), Modifier.weight(1f))
                Departure(stop.departure, Modifier.weight(2f))
            }
        }
    }
}

@Composable private fun TrainIdentification(train: Train, modifier: Modifier = Modifier) {
    val identification = remember(train) {
        if (train.category == Category.Commuter && train.commuterLineId?.isNotBlank() == true) {
            train.commuterLineId
        } else {
            "${train.type} ${train.number}"
        }
    }
    Text(
        text = identification,
        modifier = modifier,
        style = MaterialTheme.typography.body1,
        fontWeight = FontWeight.Bold
    )
}

@Composable private fun TrainRoute(
    originUic: Int?,
    destinationUic: Int?,
    modifier: Modifier = Modifier
) {
    val iconAsset = remember { Icons.Rounded.ArrowRightAlt }
    val origin = if (originUic != null) stationName(originUic) else null
    val destination = if (destinationUic != null) stationName(destinationUic) else null
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (origin != null) {
            Text(
                origin, Modifier.weight(3f),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold
            )
        }
        if (origin != null && destination != null) {
            Icon(iconAsset, Modifier.padding(horizontal = 4.dp))
        }
        if (destination != null) {
            Text(
                destination,
                Modifier.weight(3f),
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable private fun TrainTrack(track: String?, modifier: Modifier = Modifier) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (track?.isNotBlank() == true) {
            TrackLabel()
            Text(track, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable private fun TrackLabel() {
    Text(
        text = stringResource(R.string.label_track).toUpperCase(Locale.getDefault()),
        style = MaterialTheme.typography.caption,
        color = Color.Gray
    )
}

@Composable private fun Arrival(arrival: TimetableRow?, modifier: Modifier = Modifier) {
    when {
        arrival?.actualTime != null -> Time(
            stringResource(R.string.label_arrived),
            arrival.actualTime,
            modifier,
            arrival.differenceInMinutes
        )
        arrival != null -> Time(
            stringResource(R.string.label_arrives),
            arrival.scheduledTime,
            modifier
        )
        else -> Box(modifier)
    }
}

@Composable private fun Departure(departure: TimetableRow?, modifier: Modifier = Modifier) {
    when {
        departure?.actualTime != null -> Time(
            stringResource(R.string.label_departed),
            departure.actualTime,
            modifier,
            departure.differenceInMinutes
        )
        departure != null -> Time(
            stringResource(R.string.label_departs),
            departure.scheduledTime,
            modifier
        )
        else -> Box(modifier)
    }
}

@Composable
private fun Time(
    label: String, dateTime: ZonedDateTime, modifier: Modifier = Modifier,
    delay: Int? = null
) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val time = dateTime.atLocalZone().format(formatter)

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label.toUpperCase(Locale.getDefault()),
            style = MaterialTheme.typography.caption,
            color = Color.Gray
        )
        ConstraintLayout(Modifier.fillMaxWidth()) {
            val timeRef = createRef()
            val delayRef = createRef()

            Text(time, Modifier.constrainAs(timeRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            })

            if (delay != null && delay != 0) {
                val delayModifier = Modifier.constrainAs(delayRef) {
                    start.linkTo(timeRef.end, 4.dp)
                    top.linkTo(timeRef.top)
                    bottom.linkTo(timeRef.bottom)
                }
                if (delay > 0) {
                    Text(
                        text = "+$delay", delayModifier, color = StationTheme.colors.late,
                        style = MaterialTheme.typography.caption
                    )
                } else {
                    Text(
                        text = "$delay", delayModifier, color = StationTheme.colors.early,
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
    }
}

@Composable
private fun statusColor(train: Train, stop: Stop): Color? {
    return when {
        train.isNotReady() -> StationTheme.colors.trainIsNotReady
        train.hasReachedDestination() -> StationTheme.colors.trainReachedDestination
        train.isRunning && stop.isNotReached() -> StationTheme.colors.trainOnRouteToStation
        stop.isReached() && stop.isNotDeparted() -> StationTheme.colors.trainOnStation
        stop.isDeparted() -> StationTheme.colors.trainHasDepartedStation
        else -> null
    }
}

@Composable private fun TimetableEntryBubble(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    indicatorColor: Color? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 2.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        ConstraintLayout {
            val indicatorRef = createRef()
            val contentRef = createRef()
            val contentMargin = 8.dp

            StatusIndicatorStripe(
                Modifier.constrainAs(indicatorRef) {
                    start.linkTo(parent.start)
                    end.linkTo(contentRef.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.value(8.dp)
                    height = Dimension.fillToConstraints
                },
                color = indicatorColor
            )
            Box(
                Modifier.constrainAs(contentRef) {
                    start.linkTo(indicatorRef.end, contentMargin)
                    top.linkTo(parent.top, contentMargin)
                    bottom.linkTo(parent.bottom, contentMargin)
                    end.linkTo(parent.end, contentMargin)
                    width = Dimension.fillToConstraints
                }
            ) {
                content()
            }
        }
    }
}

@Composable
private fun StatusIndicatorStripe(modifier: Modifier = Modifier, color: Color? = null) {
    Box(
        modifier.fillMaxSize(),
        backgroundColor = color ?: Color.Transparent
    )
}

@Preview(showBackground = true, name = "Timetable")
@Composable
private fun PreviewTimetable() {
    val helsinki = Station(
        passengerTraffic = true,
        type = Station.Type.Station,
        name = "Helsinki",
        shortCode = "HKI",
        uic = 1,
        countryCode = "FI",
        longitude = 1.0,
        latitude = 1.0
    )
    val turku = Station(
        passengerTraffic = true,
        type = Station.Type.Station,
        name = "Turku Central Station",
        shortCode = "TKU",
        uic = 130,
        countryCode = "FI",
        longitude = 1.0,
        latitude = 1.0
    )
    val trains = listOf(
        Train(
            1, "S", Category.LongDistance, timetable = listOf(
                TimetableRow.departure(
                    "HKI", 1, "1", ZonedDateTime.parse("2020-01-01T09:30:00.000Z")
                ),
                TimetableRow.arrival(
                    "TKU", 130, "2", ZonedDateTime.parse("2020-01-01T10:30:00.000Z"),
                )
            )
        ),
        Train(
            2, "IC", Category.LongDistance, timetable = listOf(
                TimetableRow.departure(
                    "TKU", 130, "3", ZonedDateTime.parse("2020-01-01T09:30:00.000Z")
                ),
                TimetableRow.arrival(
                    "HKI", 1, "4", ZonedDateTime.parse("2020-01-01T10:30:00.000Z")
                )
            )
        )
    )

    val mapper = LocalizedStationNames.create(
        listOf(helsinki, turku), ContextAmbient.current
    )

    StationTheme(darkTheme = true) {
        StationNameProvider(mapper) {
            TimetableScreenContent(helsinki, trains, Modifier, {},
                setOf(TimetableRow.Type.Arrival), {}, setOf(Category.LongDistance), {})
        }
    }

}
