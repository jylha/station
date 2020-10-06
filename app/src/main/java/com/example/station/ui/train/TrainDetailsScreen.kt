package com.example.station.ui.train

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Dimension
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRightAlt
import androidx.compose.material.icons.rounded.Train
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
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
import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Stop
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import com.example.station.model.commercialStops
import com.example.station.model.currentCommercialStop
import com.example.station.model.isDeparted
import com.example.station.model.isDestination
import com.example.station.model.isNotDeparted
import com.example.station.model.isOrigin
import com.example.station.model.isReached
import com.example.station.model.isWaypoint
import com.example.station.model.stationUic
import com.example.station.ui.components.ActualTime
import com.example.station.ui.components.EstimatedTime
import com.example.station.ui.components.Loading
import com.example.station.ui.components.ScheduledTime
import com.example.station.ui.components.StationNameProvider
import com.example.station.ui.components.portraitOrientation
import com.example.station.ui.components.stationName
import com.example.station.ui.theme.StationTheme
import java.time.ZonedDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable fun TrainDetailsScreen(train: Train) {
    val viewModel = viewModel<TrainDetailsViewModel>()
    val viewState by viewModel.state.collectAsState()
    onCommit(train) { viewModel.setTrain(train) }

    if (train.number == viewState.train?.number) {
        TrainDetailsScreen(viewState)
    }
}

@Composable fun TrainDetailsScreen(state: TrainDetailsViewState) {
    StationNameProvider(nameMapper = state.nameMapper) {
        when {
            state.isLoading -> LoadingTrainDetails()
            state.train != null -> TrainDetails(state.train)
        }
    }
}

@Composable private fun LoadingTrainDetails() {
    val message = stringResource(R.string.message_loading_train_details)
    Loading(message)
}

@Composable private fun TrainDetails(train: Train) {
    Surface(modifier = Modifier.fillMaxSize()) {
        ScrollableColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            TrainIdentification(train)
            Spacer(Modifier.height(16.dp))
            TrainRoute(train.origin(), train.destination())
            Spacer(Modifier.height(20.dp))
            Timetable(train)
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable private fun TrainIdentification(train: Train) {
    train.run {
        if (category == Train.Category.Commuter) {
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
        "IC" -> stringResource(R.string.identification_intercity_train, number)
        "S" -> stringResource(R.string.identification_pendolino_train, number)
        else -> stringResource(R.string.identification_long_distance_train, type, number)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            Icons.Rounded.Train,
            Modifier.size(60.dp)
                .background(MaterialTheme.colors.secondary, CircleShape)
                .padding(4.dp),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Text(
                "$type $number",
                modifier = Modifier.semantics { accessibilityLabel = label },
                style = MaterialTheme.typography.h5
            )
        }
    }
}

@Composable private fun CommuterTrainIdentification(type: String, number: Int) {
    val label = stringResource(R.string.identification_commuter_train, type, number)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            Icons.Rounded.Train,
            Modifier.size(60.dp)
                .background(color = MaterialTheme.colors.primary, CircleShape)
                .padding(4.dp),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSecondary)
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Text(
                "$type $number",
                modifier = Modifier.semantics { accessibilityLabel = label },
                style = MaterialTheme.typography.h5
            )
        }
    }
}

@Composable private fun CommuterTrainIdentification(commuterLineId: String) {
    val label = stringResource(R.string.identification_commuter_line, commuterLineId)
    Column(
        Modifier.size(60.dp)
            .background(color = MaterialTheme.colors.primary, CircleShape)
            .semantics { accessibilityLabel = label },
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            commuterLineId, Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4, color = MaterialTheme.colors.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable private fun TrainRoute(originUic: Int?, destinationUic: Int?) {
    val originName = if (originUic != null) stationName(originUic) else null
    val destinationName = if (destinationUic != null) stationName(destinationUic) else null
    val fromStation = stringResource(R.string.label_from_station, originName ?: "")
    val toStation = stringResource(R.string.label_to_station, destinationName ?: "")

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            originName ?: "",
            Modifier.weight(1f).semantics { accessibilityLabel = fromStation },
            textAlign = TextAlign.Right,
            style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold
        )
        Icon(Icons.Rounded.ArrowRightAlt, Modifier.padding(2.dp))
        Text(
            destinationName ?: "",
            Modifier.weight(1f).semantics { accessibilityLabel = toStation },
            style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold
        )
    }
}

@Composable private fun Timetable(train: Train) {
    val stops = remember(train) { train.commercialStops() }
    val currentStop = remember(train) { train.currentCommercialStop() }
    val currentStopIndex = remember(stops, currentStop) { stops.indexOf(currentStop) }
    val nextStopIndex = remember(currentStop) {
        if (currentStop?.isDeparted() == true) currentStopIndex + 1 else -1
    }

    Column {
        stops.forEachIndexed() { index, stop ->
            val isCurrent = index == currentStopIndex && stop.isNotDeparted()
            val isNext = index == nextStopIndex
            when {
                stop.isOrigin() -> TrainOrigin(train, stop, isCurrent)
                stop.isWaypoint() -> TrainWaypoint(stop, isCurrent, isNext)
                stop.isDestination() -> TrainDestination(stop, isCurrent, isNext)
            }
        }
    }
}

@Composable private fun TrainOrigin(train: Train, origin: Stop, isCurrent: Boolean) {

    val (stationIconResId, stationIconColor) = if (train.isReady() || origin.isDeparted()) {
        Pair(R.drawable.origin_closed, MaterialTheme.colors.primaryVariant)
    } else {
        Pair(R.drawable.origin_open, MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
    }

    val lineColor = if (origin.isDeparted()) {
        MaterialTheme.colors.primaryVariant
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
    }

    CommercialStop(
        name = { StopName(stationName(origin.stationUic())) },
        stationIcon = {
            Image(
                vectorResource(stationIconResId),
                colorFilter = ColorFilter.tint(stationIconColor)
            )
        },
        departureTime = { StopTime(origin.departure) },
        departureIcon = {
            Image(
                vectorResource(R.drawable.line), contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(lineColor)
            )
        },
        isCurrent = isCurrent
    )
}

@Composable private fun TrainWaypoint(waypoint: Stop, isCurrent: Boolean, isNext: Boolean) {
    val (stationIconResId, arrivedIconColor) = if (waypoint.isReached()) {
        Pair(R.drawable.waypoint_closed, MaterialTheme.colors.primaryVariant)
    } else {
        Pair(R.drawable.waypoint_open, MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
    }

    val departedIconColor = if (waypoint.isDeparted()) {
        MaterialTheme.colors.primaryVariant
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
    }

    val arrivedColorFilter = ColorFilter.tint(arrivedIconColor)

    CommercialStop(
        name = { StopName(stationName(waypoint.stationUic())) },
        stationIcon = {
            Image(
                vectorResource(stationIconResId),
                colorFilter = arrivedColorFilter
            )
        },
        arrivalTime = { StopTime(waypoint.arrival) },
        arrivalIcon = {
            Image(
                vectorResource(R.drawable.line), contentScale = ContentScale.Crop,
                colorFilter = arrivedColorFilter
            )
        },
        departureTime = { StopTime(waypoint.departure) },
        departureIcon = {
            Image(
                vectorResource(R.drawable.line), contentScale = ContentScale.Crop,
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
        name = { StopName(stationName(destination.stationUic())) },
        stationIcon = { Image(vectorResource(stationResId), colorFilter = iconColorFilter) },
        arrivalIcon = {
            Image(
                vectorResource(R.drawable.line), contentScale = ContentScale.Crop,
                colorFilter = iconColorFilter
            )
        },
        arrivalTime = { StopTime(destination.arrival) },
        isCurrent = isCurrent,
        isNext = isNext
    )
}

@Composable private fun StopName(name: String?) {
    if (!name.isNullOrBlank()) {
        Text(name, style = MaterialTheme.typography.subtitle1)
    }
}

@Composable private fun StopTime(timetableRow: TimetableRow?) {
    timetableRow?.apply {
        when {
            actualTime != null -> ActualTime(actualTime, differenceInMinutes ?: 0)
            estimatedTime != null -> EstimatedTime(scheduledTime, estimatedTime)
            else -> ScheduledTime(scheduledTime)
        }
    }
}

@Composable private fun CommercialStop(
    name: @Composable () -> Unit,
    stationIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    arrivalIcon: (@Composable () -> Unit)? = null,
    arrivalTime: (@Composable () -> Unit)? = null,
    departureIcon: (@Composable () -> Unit)? = null,
    departureTime: (@Composable () -> Unit)? = null,
    isCurrent: Boolean = false,
    isNext: Boolean = false
) {
    ConstraintLayout(modifier.fillMaxWidth()) {
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

        Box(Modifier.constrainAs(stationIconRef) {
            centerAround(verticalGuideCenter)
            centerAround(horizontalGuideCenter)
            width = Dimension.value(20.dp)
            height = Dimension.value(20.dp)
        }) { stationIcon() }

        Box(Modifier.constrainAs(arrivalIconRef) {
            centerAround(verticalGuideCenter)
            top.linkTo(parent.top)
            bottom.linkTo(stationIconRef.top)
            width = Dimension.value(20.dp)
            height = Dimension.fillToConstraints
        }) { arrivalIcon?.invoke() }

        Box(Modifier.constrainAs(departureIconRef) {
            centerAround(verticalGuideCenter)
            top.linkTo(stationIconRef.bottom)
            bottom.linkTo(parent.bottom)
            width = Dimension.value(20.dp)
            height = Dimension.fillToConstraints
        }) { departureIcon?.invoke() }

        Box(Modifier.constrainAs(nameRef) {
            centerAround(horizontalGuideCenter)
            end.linkTo(stationIconRef.start, margin = 16.dp)
        }) { name() }

        if (portraitOrientation()) {
            Box(Modifier.constrainAs(arrivalTimeRef) {
                start.linkTo(stationIconRef.end, margin = 16.dp)
                top.linkTo(parent.top, margin = 4.dp)
                bottom.linkTo(horizontalGuideCenter)
            }) { arrivalTime?.invoke() }

            Box(Modifier.constrainAs(departureTimeRef) {
                start.linkTo(stationIconRef.end, margin = 16.dp)
                top.linkTo(horizontalGuideCenter)
                bottom.linkTo(parent.bottom, margin = 4.dp)
            }) { departureTime?.invoke() }
        } else {
            Box(Modifier.constrainAs(arrivalTimeRef) {
                start.linkTo(stationIconRef.end, margin = 16.dp)
                centerAround(horizontalGuideCenter)
                top.linkTo(parent.top, margin = 4.dp)
                bottom.linkTo(parent.bottom, margin = 4.dp)
            }) { arrivalTime?.invoke() }

            Box(Modifier.constrainAs(departureTimeRef) {
                start.linkTo(verticalGuideEnd)
                centerAround(horizontalGuideCenter)
            }) { departureTime?.invoke() }
        }

        if (isCurrent || isNext) {
            Icon(
                Icons.Rounded.Train,
                Modifier
                    .background(MaterialTheme.colors.secondary, CircleShape)
                    .size(24.dp)
                    .padding(2.dp)
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
        5, "IC", Train.Category.LongDistance, timetable = listOf(
            TimetableRow.departure(
                "HKI", 1, "2",
                ZonedDateTime.parse("2020-01-01T09:30:00.000Z"),
                actualTime = ZonedDateTime.parse("2020-01-01T09:31:00.000Z"),
                differenceInMinutes = 1, markedReady = true
            ),
            TimetableRow.arrival(
                "RI", 4, "4",
                ZonedDateTime.parse("2020-01-01T10:11:00.000Z"),
                actualTime = ZonedDateTime.parse("2020-01-01T10:10:00.000Z"),
                differenceInMinutes = -1
            ),
            TimetableRow.departure(
                "RI", 4, "4", ZonedDateTime.parse("2020-01-01T10:12:00.000Z")
            ),
            TimetableRow.arrival(
                "HML", 3, "1", ZonedDateTime.parse("2020-01-01T10:30:00.000Z"),
                estimatedTime = ZonedDateTime.parse("2020-01-01T10:31:00.000Z")
            ),
            TimetableRow.departure(
                "HML", 3, "1", ZonedDateTime.parse("2020-01-01T10:34:00.000Z")
            ),
            TimetableRow.arrival(
                "TPE", 2, "3", ZonedDateTime.parse("2020-01-01T11:30:00.000Z")
            )
        )
    )

    val names = mapOf(1 to "Helsinki", 3 to "Hämeenlinna", 2 to "Tampere", 4 to "Riihimäki")
    StationTheme {
        StationNameProvider(nameMapper = object : StationNameMapper {
            override fun stationName(stationUic: Int): String? = names[stationUic]
        }) {
            TrainDetails(train)
        }
    }
}
