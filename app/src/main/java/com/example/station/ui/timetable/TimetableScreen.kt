package com.example.station.ui.timetable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Dimension
import androidx.compose.foundation.layout.InnerPadding
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import com.example.station.model.Station
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import com.example.station.model.Train.Category
import com.example.station.ui.components.EmptyState
import com.example.station.ui.components.LoadingMessage
import com.example.station.ui.theme.StationTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.ZonedDateTime


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun TimetableScreen(station: Station) {
    val viewModel = viewModel<TimetableViewModel>()
    remember(station) { viewModel.offer(TimetableEvent.LoadTimetable(station)) }
    val viewState by viewModel.state.collectAsState()
    TimetableScreen(viewState, viewModel::offer)
}

@Composable
fun TimetableScreen(viewState: TimetableViewState, onEvent: (TimetableEvent) -> Unit) {
    var categorySelectionEnabled by remember { mutableStateOf(false) }
    var selectedCategories by remember {
        mutableStateOf(setOf(Category.Commuter, Category.LongDistance))
    }
    val categorySelected: (Category) -> Unit = { category ->
        selectedCategories = if (selectedCategories.contains(category)) {
            if (category == Category.LongDistance) {
                setOf(Category.Commuter)
            } else {
                setOf(Category.LongDistance)
            }
        } else {
            selectedCategories + category
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Column {
                    Text(viewState.station?.name ?: "Timetable")
                    val subtitle = if (selectedCategories.size == 1) {
                        if (selectedCategories.contains(Category.LongDistance)) {
                            "Long-distance trains"
                        } else {
                            "Commuter trains"
                        }
                    } else {
                        "All trains"
                    }
                    Text(subtitle, style = MaterialTheme.typography.caption)
                }
            },
            actions = {
                IconButton(onClick = { categorySelectionEnabled = !categorySelectionEnabled }) {
                    if (categorySelectionEnabled) Icon(Icons.Default.ExpandLess)
                    else Icon(Icons.Default.FilterList)
                }
            }
        )
    }) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        if (viewState.loading) {
            LoadingMessage("Loading timetable...", modifier)
        } else if (viewState.station != null && viewState.timetable.isNotEmpty()) {
            Timetable(
                station = viewState.station,
                trains = viewState.timetable,
                modifier,
                selectedCategories,
                categorySelected,
                categorySelectionEnabled
            )
        } else {
            EmptyState("No trains scheduled to stop in the near future.", modifier)
        }
    }
}

@Composable
private fun Timetable(
    station: Station, trains: List<Train>, modifier: Modifier = Modifier,
    selectedCategories: Set<Category>, categorySelected: (Category) -> Unit,
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
            if (matchingTrains.isEmpty()) {
                EmptyState(text = "No trains of selected category scheduled in the near future.")
            } else {
                LazyColumnFor(
                    matchingTrains,
                    contentPadding = InnerPadding(8.dp, 8.dp, 8.dp, 0.dp)
                ) { train ->
                    TimetableEntry(
                        station, train, Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySelection(
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

@Composable
private fun CategoryButton(
    onClick: (Category) -> Unit, category: Category, selected: Boolean,
    modifier: Modifier = Modifier
) {
    val text = remember {
        when (category) {
            Category.LongDistance -> "Long-distance"
            Category.Commuter -> "Commuter"
            Category.Other -> "Other"
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
        contentPadding = InnerPadding(8.dp),
    ) {
        Icon(image)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
private fun TimetableEntry(station: Station, train: Train, modifier: Modifier = Modifier) {
    TimetableEntryBubble(modifier, indicatorColor = statusColor(train, station)) {
        Column {
            Row {
                Text(
                    text = "${train.type} ${train.number}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = "${train.origin()} -> ${train.destination()}",
                    modifier = Modifier.weight(2f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "track: ${train.track(station.uicCode) ?: "-"}",
                    modifier = Modifier.weight(1f)
                )
            }
            Row {
                val scheduledArrival = train.scheduledArrivalAt(station.uicCode)?.toLocalTime()
                Text(
                    if (scheduledArrival != null) "Arrives at $scheduledArrival" else "",
                    Modifier.weight(1f)
                )

                val scheduledDeparture =
                    train.scheduledDepartureAt(station.uicCode)?.toLocalTime()
                Text(
                    if (scheduledDeparture != null) "Leaves at $scheduledDeparture" else "",
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun statusColor(train: Train, station: Station): Color? {
    return when {
        train.isOrigin(station.uicCode) && train.isNotReady() -> StationTheme.colors.trainOnOriginStation
        train.onRouteTo(station.uicCode) -> StationTheme.colors.trainOnRouteToStation
        train.onStation(station.uicCode) -> StationTheme.colors.trainOnStation
        train.hasDeparted(station.uicCode) -> StationTheme.colors.trainHasDepartedStation
        else -> null
    }
}

@Composable private fun TimetableEntryBubble(
    modifier: Modifier = Modifier,
    indicatorColor: Color? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier.fillMaxWidth(),
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

@Preview(showBackground = true)
@Composable
private fun Timetable() {
    val station = Station(
        passengerTraffic = true,
        type = Station.Type.Station,
        name = "Railway Station",
        code = "RS",
        uicCode = 12345,
        countryCode = "FI",
        longitude = 1.0,
        latitude = 1.0
    )
    val trains = listOf(
        Train(
            1, "S", Category.LongDistance, true, timetable = listOf(
                TimetableRow(
                    "RS", 12345, TimetableRow.Type.Departure, "1",
                    ZonedDateTime.parse("2020-01-01T09:30:00.000Z")
                ),
                TimetableRow(
                    "ZZ", 54321, TimetableRow.Type.Arrival, "2",
                    ZonedDateTime.parse("2020-01-01T10:30:00.000Z"),
                )
            )
        ),
        Train(
            2, "IC", Category.LongDistance, true, timetable = listOf(
                TimetableRow(
                    "ZZ", 54321, TimetableRow.Type.Departure, "3",
                    ZonedDateTime.parse("2020-01-01T09:30:00.000Z")
                ),
                TimetableRow(
                    "RS", 12345, TimetableRow.Type.Arrival, "4",
                    ZonedDateTime.parse("2020-01-01T10:30:00.000Z")
                )
            )
        )
    )
    Timetable(
        station, trains, selectedCategories = setOf(Category.LongDistance),
        categorySelected = {}, showCategorySelection = true
    )
}
