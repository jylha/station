package com.example.station.ui.train

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.outlined.Train
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.station.R
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import com.example.station.util.atLocalZone
import java.time.ZonedDateTime

@OptIn(ExperimentalLazyDsl::class)
@Composable fun TrainDetailsScreen(train: Train) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        ScrollableColumn(horizontalGravity = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(32.dp))
            TrainIcon()
            Spacer(Modifier.height(8.dp))
            TrainIdentification(type = train.type, number = train.number)
            Spacer(Modifier.height(8.dp))
            TrainRoute(train.origin(), train.destination())
            Spacer(Modifier.height(16.dp))
            TrainTimetable(timetable = train.timetable)
        }
    }
}

@Composable private fun TrainIdentification(type: String, number: Int) {
    Row() {
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
    Row(
        verticalGravity = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(originUic.toString() ?: "")
        Icon(Icons.Default.ArrowRight)
        Text(destinationUic.toString() ?: "")
    }
}

@Composable private fun TrainTimetable(timetable: List<TimetableRow>) {
    Column {
        timetable.forEachIndexed { index, timetableRow ->
            when {
                index == 0 -> {
                    TrainOrigin(timetableRow)
                }
                index == timetable.size - 1 -> {
                    TrainDestination(timetableRow)
                }
                timetableRow.type == TimetableRow.Type.Arrival -> {
                    val departure = timetable[index + 1]
                    require(departure.stationShortCode == timetableRow.stationShortCode && departure.type == TimetableRow.Type.Departure)
                    TrainWaypoint(timetableRow, departure)
                }
            }
        }
    }
}


@Composable private fun TrainOrigin(departure: TimetableRow) {
    Station(
        name = departure.stationShortCode,
        departs = departure.scheduledTime.atLocalZone().toLocalTime().toString(),
        id = R.drawable.origin_open
    )
}

@Composable private fun TrainDestination(arrival: TimetableRow) {
    Station(
        arrival.stationShortCode,
        arrives = arrival.scheduledTime.atLocalZone().toLocalTime().toString(),
        id = R.drawable.destination_open
    )
}

@Composable private fun TrainWaypoint(arrival: TimetableRow, departure: TimetableRow) {
    Station(
        arrival.stationShortCode,
        arrival.scheduledTime.atLocalZone().toLocalTime().toString(),
        departure.scheduledTime.atLocalZone().toLocalTime().toString(),
        id = R.drawable.waypoint_open
    )
}

@Composable private fun Station(
    name: String,
    arrives: String = "",
    departs: String = "",
    @DrawableRes id: Int
) {
    Row(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(20.dp)
    ) {
        Text(name, Modifier.weight(3f))
        Text(arrives, Modifier.weight(2f))
        Image(
            vectorResource(id), Modifier.weight(1f),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
        )
        Text(departs, Modifier.weight(2f))
    }
}


@Preview(showBackground = true)
@Composable fun TrainDetails() {
    val train = Train(
        5, "IC", Train.Category.LongDistance, true, timetable = listOf(
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
    TrainDetailsScreen(train)
}
