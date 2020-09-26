package com.example.station.ui.train

import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material.icons.rounded.ArrowRightAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
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
import com.example.station.model.isDeparted
import com.example.station.model.isDestination
import com.example.station.model.isNotReached
import com.example.station.model.isOrigin
import com.example.station.model.isReached
import com.example.station.model.isWaypoint
import com.example.station.model.stationUic
import com.example.station.ui.components.StationNameProvider
import com.example.station.ui.components.portraitOrientation
import com.example.station.ui.components.stationName
import com.example.station.util.atLocalZone
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable fun TrainDetailsScreen(train: Train) {
    val viewModel = viewModel<TrainDetailsViewModel>()
    val viewState by viewModel.state.collectAsState()

    StationNameProvider(nameMapper = viewState.nameMapper) {
        TrainDetailsScreen(viewState, train)
    }
}

@Composable fun TrainDetailsScreen(viewState: TrainDetailsViewState, train: Train) {
    val stops = remember(train) { train.commercialStops() }

    Surface(modifier = Modifier.fillMaxSize()) {
        ScrollableColumn(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            TrainIcon()
            Spacer(Modifier.height(8.dp))
            TrainIdentification(type = train.type, number = train.number)
            Spacer(Modifier.height(8.dp))
            TrainRoute(train.origin(), train.destination())
            Spacer(Modifier.height(16.dp))
            TrainStops(train, stops)
        }
    }
}

@Composable private fun TrainIdentification(type: String, number: Int) {
    Row {
        Text("$type $number", style = MaterialTheme.typography.h4)
    }
}

@Composable private fun TrainIcon() {
    Image(
        Icons.Outlined.Train, Modifier.size(80.dp),
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
    )
}

@Composable private fun TrainRoute(originUic: Int?, destinationUic: Int?) {
    val originName = if (originUic != null) stationName(originUic) else null
    val destinationName = if (destinationUic != null) stationName(destinationUic) else null

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            originName ?: "",
            style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold
        )
        Icon(Icons.Rounded.ArrowRightAlt)
        Text(
            destinationName ?: "",
            style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold
        )
    }
}

@Composable private fun TrainStops(train: Train, stops: List<Stop>) {
    Column {
        stops.forEach { stop ->
            when {
                stop.isOrigin() -> TrainOrigin(train, stop)
                stop.isWaypoint() -> TrainWaypoint(stop)
                stop.isDestination() -> TrainDestination(stop)
            }
        }
    }
}

@Composable private fun TrainOrigin(train: Train, origin: Stop) {

    val (stationIconResId, stationIconColor) = if (train.isNotReady()) {
        Pair(R.drawable.origin_open, MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
    } else {
        Pair(R.drawable.origin_closed, MaterialTheme.colors.primary)
    }

    val lineColor = if (origin.isDeparted()) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
    }

    CommercialStop(
        name = { StopName(stationName(origin.stationUic())) },
        stationIcon = {
            Image(
                vectorResource(stationIconResId), colorFilter = ColorFilter.tint(stationIconColor)
            )
        },
        departureTime = { StopTime(origin.departure) },
        departureIcon = {
            Image(
                vectorResource(R.drawable.line), contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(lineColor)
            )
        }
    )
}

@Composable private fun TrainWaypoint(waypoint: Stop) {
    val (stationIconResId, arrivedIconColor) = if (waypoint.isNotReached()) {
        Pair(R.drawable.waypoint_open, MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
    } else {
        Pair(R.drawable.waypoint_closed, MaterialTheme.colors.primary)
    }

    val departedIconColor = if (waypoint.isDeparted()) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
    }

    val arrivedColorFilter = ColorFilter.tint(arrivedIconColor)

    CommercialStop(
        name = { StopName(stationName(waypoint.stationUic())) },
        stationIcon = { Image(vectorResource(stationIconResId), colorFilter = arrivedColorFilter) },
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
        }
    )
}

@Composable private fun TrainDestination(destination: Stop) {
    val (stationResId, iconColor) = if (destination.isReached()) {
        Pair(R.drawable.destination_closed, MaterialTheme.colors.primary)
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
        arrivalTime = { StopTime(destination.arrival) }
    )
}

@Composable private fun StopName(name: String?) {
    Text(name ?: "<missing>", style = MaterialTheme.typography.subtitle1)
}

@Composable private fun StopTime(timetableRow: TimetableRow?) {
    timetableRow?.apply {
        if (actualTime != null) {
            ActualTime(actualTime, differenceInMinutes ?: 0)
        } else {
            ScheduledTime(scheduledTime)
        }
    }
}

private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

@Composable private fun ScheduledTime(scheduledTime: ZonedDateTime) {
    val time = scheduledTime.atLocalZone().format(formatter)
    Text(
        time,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
        style = MaterialTheme.typography.body1,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Light   
    )
}

@Composable private fun ActualTime(actualTime: ZonedDateTime, differenceInMinutes: Int) {
    val time = actualTime.atLocalZone().format(formatter)
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            time,
            style = MaterialTheme.typography.body1,
            fontStyle = FontStyle.Normal
        )

        if (differenceInMinutes != 0) {
            Spacer(Modifier.width(4.dp))
            val (text, color) = when {
                differenceInMinutes > 0 -> Pair("+$differenceInMinutes", Color.Red)
                else -> Pair("$differenceInMinutes", Color.Green)
            }
            Text(text, color = color, style = MaterialTheme.typography.caption)
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
) {
    ConstraintLayout(modifier.fillMaxWidth()) {
        val nameRef = createRef()
        val stationIconRef = createRef()
        val arrivalIconRef = createRef()
        val departureIconRef = createRef()
        val timeRef = createRef()
        val arrivalTimeRef = createRef()
        val departureTimeRef = createRef()

        val horizontalGuideCenter = createGuidelineFromTop(0.5f)
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
    }
}

@Preview(name = "TrainDetailsScreen", showBackground = true)
@Composable private fun PreviewTrainDetails() {
    val train = Train(
        5, "IC", Train.Category.LongDistance, timetable = listOf(
            TimetableRow.departure(
                "HKI", 1, "2", ZonedDateTime.parse("2020-01-01T09:30:00.000Z")
            ),
            TimetableRow.arrival(
                "HML", 3, "1", ZonedDateTime.parse("2020-01-01T10:30:00.000Z")
            ),
            TimetableRow.departure(
                "HML", 3, "1", ZonedDateTime.parse("2020-01-01T10:34:00.000Z")
            ),
            TimetableRow.arrival(
                "TPE", 2, "3", ZonedDateTime.parse("2020-01-01T11:30:00.000Z")
            )
        )
    )

    val viewState = TrainDetailsViewState.initial()
    val names = mapOf(1 to "Helsinki", 3 to "Hämeenlinna", 2 to "Tampere")
    StationNameProvider(nameMapper = object : StationNameMapper {
        override fun stationName(stationUic: Int): String? = names[stationUic]
    }) {
        TrainDetailsScreen(viewState, train)
    }
}
