package com.example.station.ui.timetable

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import com.example.station.model.Station
import com.example.station.model.Train
import kotlinx.coroutines.ExperimentalCoroutinesApi


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
    }) {
        Stack {
            if (viewState.loading) {
                LoadingTimetable("Loading timetable...")
            } else if (viewState.station != null && viewState.timetable != null) {
                Timetable(station = viewState.station, trains = viewState.timetable)
            }
        }
    }
}

@Composable
fun LoadingTimetable(message: String ) {
    Box(
        modifier = Modifier.fillMaxSize(),
        gravity = Alignment.Center
    ) {
        Column(horizontalGravity = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(message)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Loading() {
    LoadingTimetable("Loading timetable...")
}

@Composable
fun Timetable(station: Station, trains: List<Train>) {
    Column {
        Text(station.name)
        LazyColumnFor(items = trains) { train ->
            TimetableRow(train)
        }
    }
}

@Composable
fun TimetableRow(train: Train) {
    Row {
        Text("${train.type} ${train.number}")
        Spacer(Modifier.width(16.dp))
        Text("${train.origin()} -> ${train.destination()}")
    }
}