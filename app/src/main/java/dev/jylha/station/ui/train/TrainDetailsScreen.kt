package dev.jylha.station.ui.train

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Train
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.jylha.station.R
import dev.jylha.station.model.Stop
import dev.jylha.station.model.Train
import dev.jylha.station.model.arrival
import dev.jylha.station.model.commercialStops
import dev.jylha.station.model.currentCommercialStop
import dev.jylha.station.model.departure
import dev.jylha.station.model.isCommuterTrain
import dev.jylha.station.model.isDeparted
import dev.jylha.station.model.isDestination
import dev.jylha.station.model.isNotDeparted
import dev.jylha.station.model.isOrigin
import dev.jylha.station.model.isReached
import dev.jylha.station.model.isWaypoint
import dev.jylha.station.model.stationCode
import dev.jylha.station.ui.common.Loading
import dev.jylha.station.ui.common.StationNameProvider
import dev.jylha.station.ui.common.TimeField
import dev.jylha.station.ui.common.TrainRoute
import dev.jylha.station.ui.common.portraitOrientation
import dev.jylha.station.ui.common.stationName
import dev.jylha.station.ui.theme.StationTheme
import java.time.LocalDate
import java.time.ZonedDateTime

/**
 * Train details screen composable. Displays details about train's progress on its route.
 *
 * @param viewModel View model for the train details screen.
 * @param departureDate The date of train's departure.
 * @param trainNumber The train number identifying the train.
 */
@Composable fun TrainDetailsScreen(
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

@Composable fun TrainDetailsScreen(
    viewState: TrainDetailsViewState,
    onReload: (Train) -> Unit = {},
) {
    StationNameProvider(viewState.nameMapper) {
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

@Composable private fun LoadingTrainDetails() {
    val message = stringResource(R.string.message_loading_train_details)
    Loading(message)
}

@Composable private fun TrainDetails(
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

    val swipeRefreshState = rememberSwipeRefreshState(refreshing)
    SwipeRefresh(swipeRefreshState, onRefresh,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state, trigger, contentColor = MaterialTheme.colors.primary
            )
        }
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
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
                            stop.isOrigin() -> TrainOrigin(train, stop, isCurrent, isPassed)
                            stop.isWaypoint() -> TrainWaypoint(stop, isCurrent, isNext, isPassed)
                            stop.isDestination() -> TrainDestination(stop, isCurrent, isNext)
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun TrainDetailsHeading(train: Train) {
    TrainIdentification(train)
    Spacer(Modifier.height(16.dp))
    TrainRoute(
        stationName(train.origin()) ?: "",
        stationName(train.destination()) ?: "",
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}
    )
    Spacer(Modifier.height(16.dp))
}

@Composable private fun TrainIdentification(train: Train) {
    train.run {
        if (isCommuterTrain()) {
            if (commuterLineId != null) {
                CommuterTrainIdentification(commuterLineId)
            } else {
                CommuterTrainIdentification(type, number)
            }
        } else {
            LongDistanceTrainIdentification(type, number)
        }
    }
}

@Composable private fun LongDistanceTrainIdentification(type: String, number: Int) {
    val label = when (type) {
        "IC" -> stringResource(R.string.accessibility_label_intercity_train, number)
        "S" -> stringResource(R.string.accessibility_label_pendolino_train, number)
        else -> stringResource(R.string.accessibility_label_long_distance_train, type, number)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            Icons.Rounded.Train, contentDescription = null,
            Modifier
                .size(60.dp)
                .background(MaterialTheme.colors.secondary, CircleShape)
                .padding(4.dp),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Text(
                "$type $number",
                modifier = Modifier.semantics { contentDescription = label },
                style = MaterialTheme.typography.h5
            )
        }
    }
}

@Composable private fun CommuterTrainIdentification(type: String, number: Int) {
    val label = stringResource(R.string.accessibility_label_commuter_train, type, number)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            Icons.Rounded.Train, contentDescription = null,
            Modifier
                .size(60.dp)
                .background(color = MaterialTheme.colors.primary, CircleShape)
                .padding(4.dp),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Text(
                "$type $number",
                modifier = Modifier.semantics { contentDescription = label },
                style = MaterialTheme.typography.h5
            )
        }
    }
}

@Composable private fun CommuterTrainIdentification(commuterLineId: String) {
    val label = stringResource(R.string.accessibility_label_commuter_line, commuterLineId)
    Column(
        Modifier
            .size(60.dp)
            .background(color = MaterialTheme.colors.primary, CircleShape)
            .semantics { contentDescription = label },
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            commuterLineId, Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4, color = MaterialTheme.colors.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable private fun TrainOrigin(
    train: Train,
    origin: Stop,
    isCurrent: Boolean,
    isPassed: Boolean
) {
    val stationIconResId = if (train.isReady() || origin.isDeparted())
        R.drawable.origin_closed else R.drawable.origin_open
    val color = if (origin.isDeparted() || isPassed) {
        MaterialTheme.colors.primaryVariant
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
    }
    val colorFilter = ColorFilter.tint(color)

    CommercialStop(
        name = { modifier -> StopName(stationName(origin.stationCode()), modifier) },
        stationIcon = { modifier ->
            Image(
                painterResource(stationIconResId), contentDescription = null, modifier,
                colorFilter = colorFilter
            )
        },
        departureTime = { modifier -> TimeField(origin.departure, modifier) },
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

@Composable private fun TrainWaypoint(
    waypoint: Stop,
    isCurrent: Boolean,
    isNext: Boolean,
    isPassed: Boolean
) {
    val stationIconResId = if (waypoint.isReached() || waypoint.isDeparted())
        R.drawable.waypoint_closed else R.drawable.waypoint_open

    val arrivedIconColor = if (waypoint.isReached() || waypoint.isDeparted() || isPassed)
        MaterialTheme.colors.primaryVariant else MaterialTheme.colors.onSurface.copy(alpha = 0.5f)

    val departedIconColor = if (waypoint.isDeparted() || isPassed)
        MaterialTheme.colors.primaryVariant else MaterialTheme.colors.onSurface.copy(alpha = 0.5f)

    val arrivedColorFilter = ColorFilter.tint(arrivedIconColor)

    CommercialStop(
        name = { modifier -> StopName(stationName(waypoint.stationCode()), modifier) },
        stationIcon = { modifier ->
            Image(
                painterResource(stationIconResId), contentDescription = null, modifier,
                colorFilter = arrivedColorFilter
            )
        },
        arrivalTime = { modifier -> TimeField(waypoint.arrival, modifier) },
        arrivalIcon = { modifier ->
            Image(
                painterResource(R.drawable.line), contentDescription = null, modifier,
                contentScale = ContentScale.FillBounds,
                colorFilter = arrivedColorFilter
            )
        },
        departureTime = { modifier -> TimeField(waypoint.departure, modifier) },
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

@Composable private fun TrainDestination(destination: Stop, isCurrent: Boolean, isNext: Boolean) {
    val (stationResId, iconColor) = if (destination.isReached()) {
        Pair(R.drawable.destination_closed, MaterialTheme.colors.primaryVariant)
    } else {
        Pair(R.drawable.destination_open, MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
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
        arrivalTime = { modifier -> TimeField(destination.arrival, modifier) },
        isCurrent = isCurrent,
        isNext = isNext
    )
}

@Composable private fun StopName(name: String?, modifier: Modifier = Modifier) {
    if (!name.isNullOrBlank()) {
        Text(
            name,
            modifier.semantics { contentDescription = name },
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

/**
 * Commercial stop.
 * @param isCurrent Whether train is currently on this stop.
 * @param isNext Whether train will arrive next on this stop.
 */
@Composable private fun CommercialStop(
    name: @Composable (modifier: Modifier) -> Unit,
    stationIcon: @Composable (modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier,
    arrivalIcon: (@Composable (modifier: Modifier) -> Unit) = { Box(modifier) },
    arrivalTime: (@Composable (modifier: Modifier) -> Unit)? = null,
    departureIcon: (@Composable (modifier: Modifier) -> Unit) = { Box(modifier) },
    departureTime: (@Composable (modifier: Modifier) -> Unit)? = null,
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

        stationIcon(modifier = Modifier.constrainAs(stationIconRef) {
            centerAround(verticalGuideCenter)
            centerAround(horizontalGuideCenter)
            width = Dimension.value(20.dp)
            height = Dimension.value(20.dp)
        })

        arrivalIcon(modifier = Modifier.constrainAs(arrivalIconRef) {
            centerAround(verticalGuideCenter)
            top.linkTo(parent.top)
            bottom.linkTo(stationIconRef.top)
            width = Dimension.value(20.dp)
            height = Dimension.fillToConstraints
        })

        departureIcon(modifier = Modifier.constrainAs(departureIconRef) {
            centerAround(verticalGuideCenter)
            top.linkTo(stationIconRef.bottom)
            bottom.linkTo(parent.bottom)
            width = Dimension.value(20.dp)
            height = Dimension.fillToConstraints
        })

        name(modifier = Modifier.constrainAs(nameRef) {
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
                    .background(MaterialTheme.colors.secondary, CircleShape)
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
                tint = MaterialTheme.colors.onSecondary
            )
        }
    }
}

@Preview(name = "TrainDetailsScreen", showBackground = true)
@Composable private fun PreviewTrainDetails() {
    val train = Train(
        5, "IC", Train.Category.LongDistance, departureDate = LocalDate.parse("2020-01-01"),
        timetable = listOf(
            departure(
                1, "2", ZonedDateTime.parse("2020-01-01T09:30Z"),
                actualTime = ZonedDateTime.parse("2020-01-01T09:31Z"),
                differenceInMinutes = 1, markedReady = true
            ),
            arrival(
                5, "3", ZonedDateTime.parse("2020-01-01T09:40Z"),
                actualTime = ZonedDateTime.parse("2020-01-01T09:38Z"),
                differenceInMinutes = -2
            ),
            departure(
                5, "3", ZonedDateTime.parse("2020-01-01T09:43Z"),
                actualTime = ZonedDateTime.parse("2020-01-01T09:43Z")
            ),
            arrival(
                4, "4", ZonedDateTime.parse("2020-01-01T10:11Z"),
                actualTime = ZonedDateTime.parse("2020-01-01T10:10Z"),
                differenceInMinutes = -1
            ),
            departure(4, "4", ZonedDateTime.parse("2020-01-01T10:12Z")),
            arrival(
                3, "1", ZonedDateTime.parse("2020-01-01T10:30Z"),
                estimatedTime = ZonedDateTime.parse("2020-01-01T10:32Z"),
                differenceInMinutes = 2
            ),
            departure(3, "1", ZonedDateTime.parse("2020-01-01T10:34Z"), cancelled = true),
            arrival(2, "3", ZonedDateTime.parse("2020-01-01T11:30Z"), cancelled = true)
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
