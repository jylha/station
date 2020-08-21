package com.example.station.ui.timetable

import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import androidx.ui.tooling.preview.Preview

@Composable
fun TimetableScreen(stationId: String) {
    Text("Timetable: $stationId")
}

@Preview
@Composable
fun PreviewTimetable() {
    TimetableScreen(stationId = "King's Cross")
}