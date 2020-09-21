package com.example.station.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.example.station.ui.about.AboutScreen
import com.example.station.ui.theme.StationTheme
import com.example.station.ui.home.HomeScreen
import com.example.station.ui.stations.StationScreen
import com.example.station.ui.timetable.TimetableScreen
import com.example.station.ui.train.TrainDetailsScreen

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
            is Screen.Home -> HomeScreen(navigationViewModel::navigateTo)
            is Screen.About -> AboutScreen()
            is Screen.SelectStation -> StationScreen(navigationViewModel::navigateTo)
            is Screen.Timetable -> TimetableScreen(screen.station, navigationViewModel::navigateTo)
            is Screen.TrainDetails -> TrainDetailsScreen(screen.train)
        }
    }
}
