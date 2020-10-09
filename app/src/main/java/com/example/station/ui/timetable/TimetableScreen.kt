package com.example.station.ui.timetable

import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.animation.defaultFlingConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ArrowRightAlt
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Train
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.accessibilityLabel
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import com.example.station.R
import com.example.station.data.stations.LocalizedStationNames
import com.example.station.model.DelayCause
import com.example.station.model.Station
import com.example.station.model.Stop
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import com.example.station.model.Train.Category
import com.example.station.model.arrival
import com.example.station.model.delayCauses
import com.example.station.model.departure
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
import com.example.station.ui.components.ActualTime
import com.example.station.ui.components.EmptyState
import com.example.station.ui.components.EstimatedTime
import com.example.station.ui.components.Loading
import com.example.station.ui.components.RefreshIndicator
import com.example.station.ui.components.ScheduledTime
import com.example.station.ui.components.StationNameProvider
import com.example.station.ui.components.SwipeRefreshLayout
import com.example.station.ui.components.stationName
import com.example.station.ui.theme.StationTheme
import com.example.station.util.differsFrom
import java.time.ZonedDateTime
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun TimetableScreen(station: Station, navigateTo: (Screen) -> Unit) {
    val viewModel = viewModel<TimetableViewModel>()
    savedInstanceState(station.uic) {
        viewModel.offer(TimetableEvent.LoadTimetable(station))
        station.uic
    }
    val viewState by viewModel.state.collectAsState()

    StationNameProvider(viewState.stationNameMapper) {
        TimetableScreen(
            viewState,
            viewModel::offer,
            onTrainSelected = { train -> navigateTo(Screen.TrainDetails(train)) },
            onSelectStations = { navigateTo(Screen.SelectStation) }
        )
    }
}

@Composable
fun TimetableScreen(
    viewState: TimetableViewState,
    onEvent: (TimetableEvent) -> Unit = {},
    onTrainSelected: (Train) -> Unit = {},
    onSelectStations: () -> Unit = {}
) {
    var filterSelectionEnabled by savedInstanceState { false }

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
            onHideFilters = { filterSelectionEnabled = false },
            onSelectStations
        )
    }) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when {
            viewState.isLoadingTimetable -> LoadingTimetable(modifier)
            viewState.station != null -> {
                TimetableScreenContent(
                    station = viewState.station,
                    trains = viewState.timetable,
                    modifier,
                    onTrainSelected,
                    selectedTimetableTypes,
                    timetableTypeSelected,
                    selectedTrainCategories,
                    trainCategorySelected,
                    filterSelectionEnabled,
                    refreshing = viewState.isReloadingTimetable,
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
    onSelectStations: () -> Unit,
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
            IconButton(onClick = onSelectStations) { Icon(Icons.Rounded.LocationCity) }
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
    val matchingTrains by remember(trains, selectedTrainCategories) {
        mutableStateOf(trains.filter { selectedTrainCategories.contains(it.category) })
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
    EmptyState(text = message)
}

@Composable private fun NoMatchingTrains() {
    val message = stringResource(R.string.message_no_matching_trains)
    EmptyState(text = message)
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
        trains.flatMap { train ->
            train.stopsAt(station.uic).map { stop -> Pair(train, stop) }
        }
            .filter { (_, stop) ->
                stop.isWaypoint() ||
                        stop.isDestination() && selectedTimetableTypes.contains(TimetableRow.Type.Arrival) ||
                        stop.isOrigin() && selectedTimetableTypes.contains(TimetableRow.Type.Departure)
            }
            .sortedBy { (_, stop) -> timeOfSelectedStopType(stop) }
    }

    // FIXME: 2.10.2020 This is a temporary workaround to reset scroll position after filtering.
    val clock = AnimationClockAmbient.current.asDisposableClock()
    val config = defaultFlingConfig()
    val saver = remember(config, clock) {
        LazyListState.Saver(config, clock)
    }
    val listState =
        rememberSavedInstanceState(stops, config, clock, saver = saver) {
            LazyListState(
                flingConfig = config,
                animationClock = clock
            )
        }

    LazyColumnFor(
        stops, modifier, listState, contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 0.dp)
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
            departure(123, "1", ZonedDateTime.now()),
            arrival(
                555, "3", ZonedDateTime.now().plusMinutes(60),
                actualTime = ZonedDateTime.now().plusMinutes(64),
                differenceInMinutes = 4, causes = listOf(DelayCause(1))
            ),
            departure(555, "3", ZonedDateTime.now().plusHours(1)),
            arrival(456, "2", ZonedDateTime.now().plusHours(2))
        )
    )
    val stop = train.stopsAt(555).first()

    StationNameProvider(
        nameMapper = LocalizedStationNames.create(listOf(origin, somewhere, destination))
    ) {
        TimetableEntry(train, stop, {})
    }
}

enum class ExpandableState { Expanded, Collapsed }

private val expandButtonAlpha = FloatPropKey()
private val expandedContentAlpha = FloatPropKey()
private val expandedContentHeightFraction = FloatPropKey()

private val expandableStateTransition = transitionDefinition<ExpandableState> {
    state(ExpandableState.Expanded) {
        this[expandButtonAlpha] = 0f
        this[expandedContentAlpha] = 0.8f
        this[expandedContentHeightFraction] = 1f
    }
    state(ExpandableState.Collapsed) {
        this[expandButtonAlpha] = 1f
        this[expandedContentAlpha] = 0f
        this[expandedContentHeightFraction] = 0f
    }
    transition(fromState = ExpandableState.Expanded, toState = ExpandableState.Collapsed) {
        expandButtonAlpha using tween(300, 200)
        expandedContentAlpha using tween(300)
        expandedContentHeightFraction using tween(300)
    }
    transition(fromState = ExpandableState.Collapsed, toState = ExpandableState.Expanded) {
        expandButtonAlpha using tween(300)
        expandedContentAlpha using tween(300)
        expandedContentHeightFraction using tween(300)
    }
}

@Composable fun TimetableEntry(
    train: Train,
    stop: Stop,
    onSelect: (Train) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by savedInstanceState { false }

    val expandableState = transition(
        definition = expandableStateTransition,
        toState = if (expanded) ExpandableState.Expanded else ExpandableState.Collapsed,
    )

    val delayCauses = remember(train) { train.delayCauses() }

    TimetableEntryBubble(onClick = { onSelect(train) }, modifier, statusColor(train, stop)) {
        Column {
            Row {
                TrainIdentification(train, Modifier.weight(1f))
                TrainRoute(train.origin(), train.destination(), Modifier.weight(4f))
            }
            Row {
                Arrival(stop.arrival, Modifier.weight(5f))
                TrainTrack(stop.track(), Modifier.weight(2f))
                Departure(stop.departure, Modifier.weight(5f))
                ShowDelayCauseAction(
                    onClick = { expanded = true },
                    enabled = !expanded && delayCauses.isNotEmpty(),
                    Modifier.weight(1f),
                    color = if (delayCauses.isEmpty()) Color.Transparent
                    else StationTheme.colors.late.copy(alpha = expandableState[expandButtonAlpha])
                )
            }
            Row(Modifier.heightFraction(expandableState[expandedContentHeightFraction])) {
                DelayCauses(
                    train.delayCauses(),
                    onClose = { expanded = false },
                    alpha = expandableState[expandedContentAlpha]
                )
            }
        }
    }
}

fun Modifier.heightFraction(fraction: Float): Modifier {
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val constrainedHeight = (placeable.height * fraction).toInt()
        layout(placeable.width, constrainedHeight) {
            placeable.placeRelative(0, 0)
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
    val label = trainIdentificationAccessibilityLabel(train)
    Text(
        text = identification,
        modifier = modifier.semantics { accessibilityLabel = label },
        style = MaterialTheme.typography.body1,
        fontWeight = FontWeight.Bold
    )
}

@Composable fun trainIdentificationAccessibilityLabel(train: Train): String {
    return train.run {
        if (category == Category.LongDistance) {
            when (type) {
                "IC" -> stringResource(R.string.accessibility_label_intercity_train, number)
                "S" -> stringResource(R.string.accessibility_label_pendolino_train, number)
                else -> stringResource(
                    R.string.accessibility_label_long_distance_train,
                    type,
                    number
                )
            }
        } else {
            if (commuterLineId.isNullOrBlank()) {
                stringResource(R.string.accessibility_label_commuter_train, type, number)
            } else {
                stringResource(R.string.accessibility_label_commuter_line, commuterLineId)
            }
        }
    }
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
            val label = stringResource(R.string.accessibility_label_from_station, origin)
            Text(
                origin,
                Modifier.weight(3f).semantics { accessibilityLabel = label },
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold
            )
        }
        if (origin != null && destination != null) {
            Icon(iconAsset, Modifier.padding(horizontal = 4.dp))
        }
        if (destination != null) {
            val label = stringResource(R.string.accessibility_label_to_station, destination)
            Text(
                destination,
                Modifier.weight(3f).semantics { accessibilityLabel = label },
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
            val label = stringResource(R.string.accessibility_label_to_track, track)
            TrackLabel()
            Text(
                track,
                Modifier.semantics { accessibilityLabel = label },
                fontWeight = FontWeight.Bold
            )
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
    arrival?.run {
        when {
            actualTime != null -> TimeField(
                label = { TimeLabel(stringResource(R.string.label_arrived)) },
                time = {
                    ActualTime(
                        actualTime, differenceInMinutes ?: 0,
                        TimetableRow.Type.Arrival
                    )
                },
                modifier
            )
            estimatedTime != null && estimatedTime.differsFrom(scheduledTime) -> TimeField(
                label = { TimeLabel(stringResource(R.string.label_arrives)) },
                time = { EstimatedTime(scheduledTime, estimatedTime, TimetableRow.Type.Arrival) },
                modifier
            )
            else -> TimeField(
                label = { TimeLabel(stringResource(R.string.label_arrives)) },
                time = { ScheduledTime(scheduledTime, TimetableRow.Type.Arrival) },
                modifier
            )
        }
    } ?: Box(modifier)
}

@Composable private fun Departure(departure: TimetableRow?, modifier: Modifier = Modifier) {
    departure?.run {
        when {
            actualTime != null -> TimeField(
                label = { TimeLabel(stringResource(R.string.label_departed)) },
                time = {
                    ActualTime(
                        actualTime, differenceInMinutes ?: 0,
                        TimetableRow.Type.Departure
                    )
                },
                modifier
            )
            estimatedTime != null && estimatedTime.differsFrom(scheduledTime) -> TimeField(
                label = { TimeLabel(stringResource(R.string.label_departs)) },
                time = { EstimatedTime(scheduledTime, estimatedTime, TimetableRow.Type.Departure) },
                modifier
            )
            else -> TimeField(
                label = { TimeLabel(stringResource(R.string.label_departs)) },
                time = { ScheduledTime(scheduledTime, TimetableRow.Type.Departure) },
                modifier
            )
        }
    } ?: Box(modifier)
}

@Composable private fun TimeLabel(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label.toUpperCase(Locale.getDefault()),
        modifier = modifier,
        style = MaterialTheme.typography.caption,
        color = Color.Gray
    )
}

@Composable private fun TimeField(
    label: @Composable () -> Unit,
    time: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        label()
        time()
    }
}

@Composable private fun ShowDelayCauseAction(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    color: Color
) {
    Box(
        modifier,
        alignment = Alignment.CenterEnd
    ) {
        IconButton(onClick, Modifier.preferredSize(30.dp), enabled = enabled) {
            Icon(Icons.Outlined.Info, tint = color)
        }
    }
}

@Composable private fun DelayCauses(
    delayCauses: List<DelayCause>,
    onClose: () -> Unit,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    val contentColor = MaterialTheme.colors.onSurface.copy(alpha = alpha)
    Column(modifier.fillMaxWidth().padding(top = 8.dp)) {
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f * alpha))
        Row(
            Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(Modifier.weight(12f)) {
                Text(
                    "Cause of delay".toUpperCase(Locale.getDefault()),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f * alpha),
                    style = MaterialTheme.typography.caption
                )
                delayCauses.forEach { cause -> DelayCause(cause, contentColor) }
            }
            HideDelayCauseAction(
                onClick = onClose,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable private fun DelayCause(
    cause: DelayCause,
    color: Color,
    modifier: Modifier = Modifier
) {
    val category = "${cause.categoryCodeId}"
    val detailedCategory = cause.detailedCategoryCodeId?.run { " - $this" } ?: ""
    val thirdCategory = cause.thirdCategoryCodeId?.run { " - $this" } ?: ""
    Text(
        category + detailedCategory + thirdCategory,
        modifier.padding(8.dp),
        color = color
    )
}

@Composable private fun HideDelayCauseAction(
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier,
        alignment = Alignment.Center
    ) {
        IconButton(onClick, Modifier.preferredSize(30.dp)) {
            Icon(Icons.Rounded.ExpandLess, tint = color)
        }
    }
}

@Composable private fun statusColor(train: Train, stop: Stop): Color? {
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
        modifier.fillMaxSize().background(color ?: Color.Transparent)
    )
}

@Preview(showBackground = true, name = "Timetable")
@Composable
private fun PreviewTimetable() {
    val helsinki = Station(
        name = "Helsinki", shortCode = "HKI", uic = 1,
        longitude = 1.0, latitude = 1.0
    )
    val turku = Station(
        name = "Turku Central Station", shortCode = "TKU", uic = 130,
        longitude = 1.0, latitude = 1.0
    )
    val trains = listOf(
        Train(
            1, "S", Category.LongDistance, timetable = listOf(
                departure(1, "1", ZonedDateTime.parse("2020-01-01T09:30:00.000Z")),
                arrival(130, "2", ZonedDateTime.parse("2020-01-01T10:30:00.000Z"))
            )
        ),
        Train(
            2, "IC", Category.LongDistance, timetable = listOf(
                departure(130, "3", ZonedDateTime.parse("2020-01-01T09:30:00.000Z")),
                arrival(1, "4", ZonedDateTime.parse("2020-01-01T10:30:00.000Z"))
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
