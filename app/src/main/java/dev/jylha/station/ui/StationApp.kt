package dev.jylha.station.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import dev.jylha.station.ui.about.AboutScreen
import dev.jylha.station.ui.home.HomeScreen
import dev.jylha.station.ui.stations.StationScreen
import dev.jylha.station.ui.theme.StationTheme
import dev.jylha.station.ui.timetable.TimetableScreen
import dev.jylha.station.ui.train.TrainDetailsScreen

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
            is Screen.SelectNearest -> StationScreen(navigationViewModel::navigateTo, true)
            is Screen.Timetable -> TimetableScreen(screen.station, navigationViewModel::navigateTo)
            is Screen.TrainDetails -> TrainDetailsScreen(screen.train)
        }
    }
}
