package com.example.station.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.example.station.ui.theme.StationTheme
import com.example.station.ui.home.HomeScreen
import com.example.station.ui.stations.StationScreen
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
            is Screen.SelectStation -> StationScreen(
                navigateTo = navigationViewModel::navigateTo
            )
            is Screen.Timetable -> TimetableScreen(screen.station)
        }
    }
}
