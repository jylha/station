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
    val navigateTo = navigationViewModel::navigateTo
    Surface(color = MaterialTheme.colors.background) {
        with(navigationViewModel.screen) {
            when (this) {
                is Screen.Home -> HomeScreen(
                    onNavigateToStations = { navigateTo(Screen.SelectStation) },
                    onNavigateToNearestStation = { navigateTo(Screen.SelectNearest) },
                    onNavigateToTimetable = { stationCode ->
                        navigateTo(Screen.Timetable(stationCode))
                    },
                    onNavigateToAbout = { navigateTo(Screen.About) }
                )
                is Screen.About -> AboutScreen()
                is Screen.SelectStation -> StationScreen(
                    onNavigateToTimetable = { stationCode ->
                        navigateTo(Screen.Timetable(stationCode))
                    },
                    onNavigateToNearestStation = { navigateTo(Screen.SelectNearest) },
                )
                is Screen.SelectNearest -> StationScreen(
                    onNavigateToTimetable = { stationCode ->
                        navigateTo(Screen.Timetable(stationCode))
                    },
                    onNavigateToNearestStation = { navigateTo(Screen.SelectNearest) },
                    selectNearestStation = true
                )
                is Screen.Timetable -> TimetableScreen(stationCode,
                    onNavigateToStations = { navigateTo(Screen.SelectStation) },
                    onNavigateToTrainDetails = { trainNumber ->
                        navigateTo(Screen.TrainDetails(trainNumber)) }
                )
                is Screen.TrainDetails -> TrainDetailsScreen(trainNumber)
            }
        }
    }
}
