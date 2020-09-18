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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material.icons.rounded.ArrowRightAlt
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import com.example.station.data.stations.LocalizedStationNames
import com.example.station.model.Station
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import com.example.station.model.Train.Category
import com.example.station.ui.Screen
import com.example.station.ui.components.EmptyState
import com.example.station.ui.components.LoadingMessage
import com.example.station.ui.components.StationName
import com.example.station.ui.components.StationNameProvider
import com.example.station.ui.theme.StationTheme
import com.example.station.util.atLocalZone
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.ZonedDateTime


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun TimetableScreen(station: Station, navigateTo: (Screen) -> Unit) {
    val viewModel = viewModel<TimetableViewModel>()
    remember(station) { viewModel.offer(TimetableEvent.LoadTimetable(station)) }
    val viewState by viewModel.state.collectAsState()

    StationNameProvider(viewState.mapper) {
        TimetableScreen(viewState, viewModel::offer, trainSelected = { train ->
            navigateTo(Screen.TrainDetails(train))
        })
    }
}

@Composable
fun TimetableScreen(
    viewState: TimetableViewState,
    onEvent: (TimetableEvent) -> Unit,
    trainSelected: (Train) -> Unit
) {
    var categorySelectionEnabled by remember { mutableStateOf(false) }
    val selectedCategories = viewState.selectedCategories
    val categorySelected: (Category) -> Unit = { category ->
        val updatedCategories =
            if (selectedCategories.contains(category)) {
                if (category == Category.LongDistance) {
                    setOf(Category.Commuter)
                } else {
                    setOf(Category.LongDistance)
                }
            } else {
                selectedCategories + category
            }
        onEvent(TimetableEvent.SelectCategories(updatedCategories))
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { TimetableTitle(viewState.station?.name, selectedCategories) },
            actions = {
                IconButton(onClick = { categorySelectionEnabled = !categorySelectionEnabled }) {
                    if (categorySelectionEnabled) Icon(Icons.Default.ExpandLess)
                    else Icon(Icons.Default.FilterList)
                }
            }
        )
    }) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when {
            viewState.loading -> LoadingMessage("Loading timetable...", modifier)
            viewState.station != null -> {
                Timetable(
                    station = viewState.station,
                    trains = viewState.timetable,
                    modifier,
                    trainSelected,
                    selectedCategories,
                    categorySelected,
                    categorySelectionEnabled
                )
            }
            else -> {
                // TODO: 15.9.2020 Replace with error message..
                EmptyState("Oops. Something went wrong.", modifier)
            }
        }
    }
}

/** The title shown in TimetableScreen's TopAppBar. */
@Composable
private fun TimetableTitle(
    stationName: String?,
    selectedCategories: Set<Category>,
    modifier: Modifier = Modifier
) {
    val trainCategories = remember(selectedCategories) {
        if (selectedCategories.size == 1) {
            if (selectedCategories.contains(Category.LongDistance)) {
                "Long-distance trains"
            } else {
                "Commuter trains"
            }
        } else {
            "All trains"
        }
    }

    Column(modifier) {
        Text(stationName ?: "Timetable")
        Text(trainCategories, style = MaterialTheme.typography.caption)
    }
}

@Composable
private fun Timetable(
    station: Station,
    trains: List<Train>,
    modifier: Modifier = Modifier,
    trainSelected: (Train) -> Unit,
    selectedCategories: Set<Category>,
    categorySelected: (Category) -> Unit,
    showCategorySelection: Boolean = false
) {
    val matchingTrains by remember(trains, selectedCategories) {
        mutableStateOf(trains.filter { selectedCategories.contains(it.category) })
    }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = modifier.fillMaxSize()
    ) {
        Column {
            if (showCategorySelection) {
                CategorySelection(selectedCategories, categorySelected)
            }
            when {
                trains.isEmpty() -> EmptyState("No trains scheduled to stop in the near future.")
                matchingTrains.isEmpty() -> EmptyState("No trains of selected category scheduled in the near future.")
                else -> {
                    LazyColumnFor(
                        matchingTrains,
                        contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 0.dp)
                    ) { train ->
                        TimetableEntry(
                            station,
                            train,
                            onSelect = trainSelected,
                            Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}


@Preview(name = "CategorySelection")
@Composable private fun PreviewCategorySelection() {
    CategorySelection(setOf(Category.LongDistance), {})
}

@Composable private fun CategorySelection(
    categories: Set<Category>,
    categorySelected: (Category) -> Unit,
) {
    Surface(Modifier.fillMaxWidth(), elevation = 4.dp) {
        Row(Modifier.padding(8.dp)) {
            CategoryButton(
                categorySelected, Category.LongDistance, categories.contains(Category.LongDistance),
                Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            CategoryButton(
                categorySelected, Category.Commuter, categories.contains(Category.Commuter),
                Modifier.weight(1f)
            )
        }
    }
}

@Composable private fun CategoryButton(
    onClick: (Category) -> Unit, category: Category, selected: Boolean,
    modifier: Modifier = Modifier
) {
    val text = remember {
        when (category) {
            Category.LongDistance -> "Long-distance"
            Category.Commuter -> "Commuter"
        }
    }
    val image = remember { Icons.Outlined.Train }
    val color = if (selected) Color.Green.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.7f)

    OutlinedButton(
        onClick = { onClick(category) },
        modifier,
        contentColor = color,
        backgroundColor = Color.Transparent,
        border = BorderStroke(2.dp, color),
        contentPadding = PaddingValues(8.dp),
    ) {
        Icon(image)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Preview(name = "TimetableEntry - Dark", group = "TimetableEntry")
@Composable fun PreviewDarkTimetableEntry() {
    StationTheme(darkTheme = true) {
        PreviewTimetableEntry()
    }
}

@Preview(name = "TimetableEntry - Light", group = "TimetableEntry")
@Composable fun PreviewLightTimetableEntry() {
    StationTheme(darkTheme = false) {
        PreviewTimetableEntry()
    }
}

@Composable private fun PreviewTimetableEntry() {
    val origin = Station(
        true, Station.Type.Station, "Here", "H",
        123, "FI", 100.0, 50.0
    )
    val destination = Station(
        true, Station.Type.StoppingPoint, "There", "H",
        456, "FI", 50.0, 100.0
    )
    val train = Train(
        1, "IC", Category.LongDistance, true, timetable = listOf(
            TimetableRow.departure("H", 123, "1", ZonedDateTime.now()),
            TimetableRow.arrival("T", 456, "2", ZonedDateTime.now().plusHours(2))
        )
    )
    StationNameProvider(nameMapper = LocalizedStationNames.create(listOf(origin, destination))) {
        TimetableEntry(origin, train, {})
    }
}

@Composable private fun TimetableEntry(
    station: Station,
    train: Train,
    onSelect: (Train) -> Unit,
    modifier: Modifier = Modifier
) {
    TimetableEntryBubble(onClick = { onSelect(train) }, modifier, statusColor(train, station)) {
        Column {
            Row {
                TrainIdentification(train, Modifier.weight(1f))
                TrainRoute(train.origin(), train.destination(), Modifier.weight(2f))
                TrainTrack(train.track(station.uic), Modifier.weight(1f))
            }
            Row {
                Arrival(train.arrivalAt(station.uic), Modifier.weight(1f))
                Departure(train.departureAt(station.uic), Modifier.weight(1f))
            }
        }
    }
}

@Composable private fun TrainIdentification(train: Train, modifier: Modifier = Modifier) {
    Text(
        text = "${train.type} ${train.number}",
        modifier = modifier,
        style = MaterialTheme.typography.h6
    )
}

@Composable private fun TrainRoute(
    originUic: Int?,
    destinationUic: Int?,
    modifier: Modifier = Modifier
) {
    val iconAsset = remember { Icons.Rounded.ArrowRightAlt }
    val origin = if (originUic != null) StationName.forUic(originUic) else null
    val destination = if (destinationUic != null) StationName.forUic(destinationUic) else null
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (origin != null) Text(
            origin,
            Modifier.weight(3f),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold
        )
        if (origin != null && destination != null) Icon(
            iconAsset,
            Modifier.padding(horizontal = 8.dp)
        )
        if (destination != null) Text(
            destination,
            Modifier.weight(3f),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TrainTrack(track: String?, modifier: Modifier = Modifier) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("track:", style = MaterialTheme.typography.caption)
        Spacer(Modifier.width(4.dp))
        Text(text = if (track.isNullOrBlank()) "--" else track, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Arrival(arrival: TimetableRow?, modifier: Modifier = Modifier) {
    val arrivalText = when {
        arrival?.actualTime != null ->
            "Arrived at ${arrival.actualTime.atLocalZone().toLocalTime()}"
        arrival?.scheduledTime != null ->
            "Arrives at ${arrival.scheduledTime.atLocalZone().toLocalTime()}"
        else -> ""
    }
    Text(arrivalText, modifier)
}

@Composable
private fun Departure(departure: TimetableRow?, modifier: Modifier = Modifier) {
    val departureText = when {
        departure?.actualTime != null ->
            "Departed at ${departure.actualTime.atLocalZone().toLocalTime()}"
        departure?.scheduledTime != null ->
            "Departs at ${departure.scheduledTime.atLocalZone().toLocalTime()}"
        else -> ""
    }
    Text(departureText, modifier)
}

@Composable
private fun statusColor(train: Train, station: Station): Color? {
    return when {
        train.isOrigin(station.uic) && train.isNotReady() -> StationTheme.colors.trainOnOriginStation
        train.onRouteTo(station.uic) -> StationTheme.colors.trainOnRouteToStation
        train.onStation(station.uic) -> StationTheme.colors.trainOnStation
        train.hasDeparted(station.uic) -> StationTheme.colors.trainHasDepartedStation
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
            1, "S", Category.LongDistance, true, timetable = listOf(
                TimetableRow.departure(
                    "HKI", 1, "1", ZonedDateTime.parse("2020-01-01T09:30:00.000Z")
                ),
                TimetableRow.arrival(
                    "TKU", 130, "2", ZonedDateTime.parse("2020-01-01T10:30:00.000Z"),
                )
            )
        ),
        Train(
            2, "IC", Category.LongDistance, true, timetable = listOf(
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
            Timetable(helsinki, trains, Modifier, {}, setOf(Category.LongDistance), {})
        }
    }

}
