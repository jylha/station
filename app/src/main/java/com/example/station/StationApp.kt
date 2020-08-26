package com.example.station

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.example.station.ui.StationTheme
import com.example.station.ui.home.HomeScreen
import com.example.station.ui.select.SelectStationScreen
import com.example.station.ui.timetable.TimetableScreen

@Composable
fun StationApp(
    navigationViewModel: NavigationViewModel
) {
    StationTheme {
        StationAppContent(navigationViewModel)
    }
}

@Composable
fun StationAppContent(
    navigationViewModel: NavigationViewModel
) {
    Surface(color = MaterialTheme.colors.background) {
        when (val screen = navigationViewModel.screen) {
            is Screen.Home -> HomeScreen(
                navigateTo = navigationViewModel::navigateTo,
            )
            is Screen.SelectStation -> SelectStationScreen(
                navigateTo = navigationViewModel::navigateTo
            )
            is Screen.Timetable -> TimetableScreen(screen.stationId)
        }
    }
}
