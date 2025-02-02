package dev.jylha.station.ui.train

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import dev.jylha.station.R
import dev.jylha.station.model.Stop
import dev.jylha.station.model.Train
import dev.jylha.station.model.arrival
import dev.jylha.station.model.departure
import dev.jylha.station.ui.common.Loading
import dev.jylha.station.ui.common.StationNameProvider
import dev.jylha.station.ui.common.TimeOfArrival
import dev.jylha.station.ui.common.TimeOfDeparture
import dev.jylha.station.ui.common.TrainRoute
import dev.jylha.station.ui.common.portraitOrientation
import dev.jylha.station.ui.common.stationName
import dev.jylha.station.ui.theme.StationTheme
import dev.jylha.station.ui.theme.backgroundColor
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Train details screen composable. Displays details about train's progress on its route.
 *
 * @param viewModel View model for the train details screen.
 * @param departureDate The date of train's departure.
 * @param trainNumber The train number identifying the train.
 */
@Composable
fun TrainDetailsScreen(
    viewModel: TrainDetailsViewModel,
    departureDate: String,
    trainNumber: Int
) {
    rememberSaveable(departureDate, trainNumber) {
        viewModel.setTrain(departureDate, trainNumber)
        Pair(departureDate, trainNumber)
    }
    val viewState by viewModel.state.collectAsState()
    TrainDetailsScreen(viewState, onReload = { train -> viewModel.reload(train) })
}

@Composable
fun TrainDetailsScreen(
    viewState: TrainDetailsViewState,
    onReload: (Train) -> Unit = {},
) {
    val windowInsets =
        WindowInsets.systemBars.union(WindowInsets.displayCutout)
            .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)

    StationNameProvider(viewState.nameMapper) {
        Surface(
            modifier = Modifier
                .background(backgroundColor())
                .windowInsetsPadding(windowInsets),
            color = Color.Red
        ) {
            when {
                viewState.isLoading -> LoadingTrainDetails()
                viewState.train != null -> TrainDetails(
                    viewState.train,
                    viewState.isReloading,
                    onRefresh = { onReload(viewState.train) }
                )
            }
        }
    }
}

@Composable
private fun LoadingTrainDetails() {
    val message = stringResource(R.string.message_loading_train_details)
    Loading(message)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrainDetails(
    train: Train,
    refreshing: Boolean = false,
    onRefresh: () -> Unit = {},
) {
    val stops = remember(train) { train.commercialStops() }
    val currentStop = remember(train) { train.currentCommercialStop() }
    val currentStopIndex = remember(stops, currentStop) { stops.indexOf(currentStop) }
    val nextStopIndex = remember(currentStop) {
        if (currentStop?.isDeparted() == true) currentStopIndex + 1 else -1
    }

    val pullToRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        modifier = Modifier,
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
        Surface(color = MaterialTheme.colorScheme.background) {
            val routeBehindColor = MaterialTheme.colorScheme.primary
            val routeAheadColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                .compositeOver(MaterialTheme.colorScheme.background)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item { TrainDetailsHeading(train) }
                stops.forEachIndexed { index, stop ->
                    val isCurrent = index == currentStopIndex && stop.isNotDeparted()
                    val isNext = index == nextStopIndex
                    val isPassed = index < currentStopIndex
                    item {
                        when {
                            stop.isOrigin() -> TrainOrigin(
                                train, stop, isCurrent, isPassed, routeBehindColor, routeAheadColor
                            )

                            stop.isWaypoint() -> TrainWaypoint(
                                stop, isCurrent, isNext, isPassed, routeBehindColor, routeAheadColor
                            )

                            stop.isDestination() -> TrainDestination(
                                stop, isCurrent, isNext, routeBehindColor, routeAheadColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainDetailsHeading(train: Train, modifier: Modifier = Modifier) {
    Column(
        modifier.padding(16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TrainIdentification(train)
        TrainRoute(
            stationName(train.origin()) ?: "",
            stationName(train.destination()) ?: "",
            modifier = Modifier
                .fillMaxWidth()
                .semantics(mergeDescendants = true) {}
        )
    }
}


@Composable
private fun TrainOrigin(
    train: Train, origin: Stop, isCurrent: Boolean, isPassed: Boolean,
    routeBehindColor: Color, routeAheadColor: Color,
) {
    val stationIconResId = if (train.isReady() || origin.isDeparted())
        R.drawable.origin_closed else R.drawable.origin_open
    val color = if (origin.isDeparted() || isPassed)
        routeBehindColor else routeAheadColor
    val colorFilter = ColorFilter.tint(color)

    CommercialStop(
        name = { modifier -> StopName(stationName(origin.stationCode()), modifier) },
        stationIcon = { modifier ->
            Image(
                painterResource(stationIconResId), contentDescription = null, modifier,
                colorFilter = colorFilter
            )
        },
        departureTime = { modifier -> TimeOfDeparture(origin.departure, modifier) },
        departureIcon = { modifier ->
            Image(
                painterResource(R.drawable.line), contentDescription = null, modifier,
                contentScale = ContentScale.FillBounds,
                colorFilter = colorFilter
            )
        },
        isCurrent = isCurrent,
    )
}

@Composable
private fun TrainWaypoint(
    waypoint: Stop, isCurrent: Boolean, isNext: Boolean, isPassed: Boolean,
    routeBehindColor: Color, routeAheadColor: Color,
) {
    val stationIconResId = if (waypoint.isReached() || waypoint.isDeparted())
        R.drawable.waypoint_closed else R.drawable.waypoint_open

    val arrivedIconColor = if (waypoint.isReached() || waypoint.isDeparted() || isPassed)
        routeBehindColor else routeAheadColor

    val departedIconColor = if (waypoint.isDeparted() || isPassed)
        routeBehindColor else routeAheadColor

    val arrivedColorFilter = ColorFilter.tint(arrivedIconColor)

    CommercialStop(
        name = { modifier -> StopName(stationName(waypoint.stationCode()), modifier) },
        stationIcon = { modifier ->
            Image(
                painterResource(stationIconResId), contentDescription = null, modifier,
                colorFilter = arrivedColorFilter
            )
        },
        arrivalTime = { modifier -> TimeOfArrival(waypoint.arrival, modifier) },
        arrivalIcon = { modifier ->
            Image(
                painterResource(R.drawable.line), contentDescription = null, modifier,
                contentScale = ContentScale.FillBounds,
                colorFilter = arrivedColorFilter
            )
        },
        departureTime = { modifier -> TimeOfDeparture(waypoint.departure, modifier) },
        departureIcon = { modifier ->
            Image(
                painterResource(R.drawable.line), contentDescription = null, modifier,
                contentScale = ContentScale.FillBounds,
                colorFilter = ColorFilter.tint(departedIconColor)
            )
        },
        isCurrent = isCurrent,
        isNext = isNext
    )
}

@Composable
private fun TrainDestination(
    destination: Stop, isCurrent: Boolean, isNext: Boolean,
    routeBehindColor: Color, routeAheadColor: Color
) {
    val (stationResId, iconColor) = if (destination.isReached()) {
        Pair(R.drawable.destination_closed, routeBehindColor)
    } else {
        Pair(R.drawable.destination_open, routeAheadColor)
    }

    val iconColorFilter = ColorFilter.tint(iconColor)

    CommercialStop(
        name = { modifier -> StopName(stationName(destination.stationCode()), modifier) },
        stationIcon = { modifier ->
            Image(
                painterResource(stationResId), contentDescription = null, modifier,
                colorFilter = iconColorFilter
            )
        },
        arrivalIcon = { modifier ->
            Image(
                painterResource(R.drawable.line), contentDescription = null, modifier,
                contentScale = ContentScale.FillBounds,
                colorFilter = iconColorFilter
            )
        },
        arrivalTime = { modifier -> TimeOfArrival(destination.arrival, modifier) },
        isCurrent = isCurrent,
        isNext = isNext
    )
}

@Composable
private fun StopName(name: String?, modifier: Modifier = Modifier) {
    if (!name.isNullOrBlank()) {
        Text(
            name,
            modifier.semantics { contentDescription = name },
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

/**
 * Commercial stop.
 * @param isCurrent Whether train is currently on this stop.
 * @param isNext Whether train will arrive next on this stop.
 */
@Composable
private fun CommercialStop(
    name: @Composable (Modifier) -> Unit,
    stationIcon: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    arrivalIcon: (@Composable (Modifier) -> Unit) = { Spacer(modifier) },
    arrivalTime: (@Composable (Modifier) -> Unit)? = null,
    departureIcon: (@Composable (Modifier) -> Unit) = { Spacer(modifier) },
    departureTime: (@Composable (Modifier) -> Unit)? = null,
    isCurrent: Boolean = false,
    isNext: Boolean = false,
) {
    ConstraintLayout(
        modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}) {
        val nameRef = createRef()
        val stationIconRef = createRef()
        val arrivalIconRef = createRef()
        val departureIconRef = createRef()
        val arrivalTimeRef = createRef()
        val departureTimeRef = createRef()
        val trainIndicatorRef = createRef()

        val horizontalGuideCenter = createGuidelineFromTop(0.5f)
        val horizontalGuideStart = createGuidelineFromTop(0f)
        val verticalGuideCenter = createGuidelineFromStart(0.5f)
        val verticalGuideEnd = createGuidelineFromStart(0.7f)

        stationIcon(Modifier.constrainAs(stationIconRef) {
            centerAround(verticalGuideCenter)
            centerAround(horizontalGuideCenter)
            width = Dimension.value(20.dp)
            height = Dimension.value(20.dp)
        })

        arrivalIcon(Modifier.constrainAs(arrivalIconRef) {
            centerAround(verticalGuideCenter)
            top.linkTo(parent.top)
            bottom.linkTo(stationIconRef.top)
            width = Dimension.value(20.dp)
            height = Dimension.fillToConstraints
        })

        departureIcon(Modifier.constrainAs(departureIconRef) {
            centerAround(verticalGuideCenter)
            top.linkTo(stationIconRef.bottom)
            bottom.linkTo(parent.bottom)
            width = Dimension.value(20.dp)
            height = Dimension.fillToConstraints
        })

        name(Modifier.constrainAs(nameRef) {
            centerAround(horizontalGuideCenter)
            linkTo(start = parent.start, end = stationIconRef.start, endMargin = 16.dp, bias = 1f)
            width = Dimension.preferredWrapContent
        })

        if (portraitOrientation()) {
            arrivalTime?.invoke(Modifier.constrainAs(arrivalTimeRef) {
                start.linkTo(stationIconRef.end, margin = 16.dp)
                top.linkTo(parent.top, margin = 4.dp)
                bottom.linkTo(horizontalGuideCenter)
            })

            departureTime?.invoke(Modifier.constrainAs(departureTimeRef) {
                start.linkTo(stationIconRef.end, margin = 16.dp)
                top.linkTo(horizontalGuideCenter)
                bottom.linkTo(parent.bottom, margin = 4.dp)
            })
        } else {
            arrivalTime?.invoke(Modifier.constrainAs(arrivalTimeRef) {
                start.linkTo(stationIconRef.end, margin = 16.dp)
                centerAround(horizontalGuideCenter)
                top.linkTo(parent.top, margin = 4.dp)
                bottom.linkTo(parent.bottom, margin = 4.dp)
            })

            departureTime?.invoke(Modifier.constrainAs(departureTimeRef) {
                start.linkTo(verticalGuideEnd)
                centerAround(horizontalGuideCenter)
            })
        }

        if (isCurrent || isNext) {
            Icon(
                Icons.Rounded.Train,
                contentDescription = null,
                Modifier
                    .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                    .size(24.dp)
                    .padding(2.dp)
                    .zIndex(1f)
                    .constrainAs(trainIndicatorRef) {
                        centerAround(verticalGuideCenter)
                        if (isCurrent) {
                            centerAround(horizontalGuideCenter)
                        } else {
                            centerAround(horizontalGuideStart)
                        }
                    },
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun TrainDetailsPreview() {
    val train = Train(
        5, "IC", Train.Category.LongDistance, departureDate = LocalDate.parse("2020-01-01"),
        timetable = listOf(
            departure(
                1, "2", Instant.parse("2020-01-01T09:30Z"),
                actualTime = Instant.parse("2020-01-01T09:31Z"),
                differenceInMinutes = 1, markedReady = true
            ),
            arrival(
                5, "3", Instant.parse("2020-01-01T09:40Z"),
                actualTime = Instant.parse("2020-01-01T09:38Z"),
                differenceInMinutes = -2
            ),
            departure(
                5, "3", Instant.parse("2020-01-01T09:43Z"),
                actualTime = Instant.parse("2020-01-01T09:43Z")
            ),
            arrival(
                4, "4", Instant.parse("2020-01-01T10:11Z"),
                actualTime = Instant.parse("2020-01-01T10:10Z"),
                differenceInMinutes = -1
            ),
            departure(4, "4", Instant.parse("2020-01-01T10:12Z")),
            arrival(
                3, "1", Instant.parse("2020-01-01T10:30Z"),
                estimatedTime = Instant.parse("2020-01-01T10:32Z"),
                differenceInMinutes = 2
            ),
            departure(3, "1", Instant.parse("2020-01-01T10:34Z"), cancelled = true),
            arrival(2, "3", Instant.parse("2020-01-01T11:30Z"), cancelled = true)
        )
    )

    val names = mapOf(
        1 to "Helsinki", 3 to "Hämeenlinna", 5 to "Pasila", 2 to "Tampere", 4 to "Riihimäki"
    )
    StationTheme {
        StationNameProvider({ stationCode -> names[stationCode] }) {
            TrainDetails(train)
        }
    }
}
