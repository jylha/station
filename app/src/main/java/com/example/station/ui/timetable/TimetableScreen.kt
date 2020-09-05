package com.example.station.ui.timetable

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import com.example.station.model.Station
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import com.example.station.ui.components.EmptyState
import com.example.station.ui.components.LoadingMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDateTime


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
    Scaffold(topBar = {
        TopAppBar {
            Text(modifier = Modifier.padding(16.dp), text = "Timetable")
        }
    }) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        if (viewState.loading) {
            LoadingMessage("Loading timetable...")
        } else if (viewState.station != null && viewState.timetable.isNotEmpty()) {
            Timetable(station = viewState.station, trains = viewState.timetable)
        } else {
            EmptyState("No trains scheduled to stop in the near future.", modifier)
        }
    }
}

@Composable
private fun Timetable(station: Station, trains: List<Train>, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = modifier.fillMaxSize()
    ) {
        Column {
            Text(station.name)
            LazyColumnFor(items = trains) { train ->
                TimetableEntry(station, train)
            }
        }
    }
}

@Composable
private fun TimetableEntry(station: Station, train: Train) {
    TimetableEntryBubble {
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
                Text("Arrives at $scheduledArrival", Modifier.weight(1f))
                Text("Leaves at ${"-"}", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TimetableEntryBubble(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
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
            1, "S", timetable = listOf(
                TimetableRow("RS", 12345, "3", LocalDateTime.MIN)
            )
        ),
        Train(
            2, "IC", timetable = listOf(
                TimetableRow("RS", 12345, "4", LocalDateTime.MIN)
            )
        )
    )
    Timetable(station, trains)
}
