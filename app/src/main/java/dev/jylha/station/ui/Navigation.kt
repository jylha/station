package dev.jylha.station.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import dev.jylha.station.ui.about.AboutScreen
import dev.jylha.station.ui.home.HomeScreen
import dev.jylha.station.ui.stations.StationsScreen
import dev.jylha.station.ui.timetable.TimetableScreen
import dev.jylha.station.ui.train.TrainDetailsScreen

/** Navigation targets. */
private sealed class Screen(val route: String) {
    object Home : Screen("home")
    object About : Screen("about")
    object Stations : Screen("stations")
    object NearestStation : Screen("nearest_station")

    object Timetable : Screen("timetable/{stationCode}") {
        fun route(stationCode: Int) = "timetable/$stationCode"
        const val argName = "stationCode"
    }

    object TrainDetails : Screen("train_details/{trainNumber}") {
        fun route(trainNumber: Int) = "train_details/$trainNumber"
        const val argName = "trainNumber"
    }
}

/**
 * Station app navigation composable. The composable defines the navigation graph for the
 * application.
 */
@Composable
fun StationAppNavigation() {
    val navController = rememberNavController()
    val navigateTo = { screen: Screen ->
        navController.navigate(screen.route)
    }
    val navigateToStations = {
        navController.navigate(Screen.Stations.route) {
            popUpTo(Screen.Home.route) {}
        }
    }
    val navigateToNearestStation = {
        navController.navigate(Screen.NearestStation.route) {
            popUpTo(Screen.Home.route) {}
        }
    }
    val navigateToTimetable = { stationCode: Int ->
        navController.navigate(Screen.Timetable.route(stationCode)) {
            popUpTo(Screen.NearestStation.route) { inclusive = true }
        }
    }
    val navigateToTrainDetails = { trainNumber: Int ->
        navController.navigate(Screen.TrainDetails.route(trainNumber))
    }

    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { backStackEntry ->
            HomeScreen(
                viewModel = hiltViewModel(backStackEntry),
                onNavigateToStations = { navigateToStations() },
                onNavigateToNearestStation = { navigateToNearestStation() },
                onNavigateToTimetable = { stationCode -> navigateToTimetable(stationCode) },
                onNavigateToAbout = { navigateTo(Screen.About) },
            )
        }
        composable(Screen.About.route) { AboutScreen() }
        composable(Screen.Stations.route) { backStackEntry ->
            StationsScreen(
                hiltViewModel(backStackEntry),
                onNavigateToTimetable = { stationCode -> navigateToTimetable(stationCode) },
                onNavigateToNearestStation = { navigateToNearestStation() },
            )
        }
        composable(Screen.NearestStation.route) { backStackEntry ->
            StationsScreen(
                hiltViewModel(backStackEntry),
                onNavigateToTimetable = { stationCode -> navigateToTimetable(stationCode) },
                onNavigateToNearestStation = { navigateToNearestStation() },
                selectNearestStation = true,
            )
        }
        composable(
            Screen.Timetable.route,
            arguments = listOf(navArgument(Screen.Timetable.argName) { type = NavType.IntType })
        ) { backStackEntry ->
            TimetableScreen(
                hiltViewModel(backStackEntry),
                stationCode = backStackEntry.arguments?.getInt(Screen.Timetable.argName) ?: 0,
                onNavigateToStations = { navigateTo(Screen.Stations) },
                onNavigateToTrainDetails = { trainNumber -> navigateToTrainDetails(trainNumber) },
            )
        }
        composable(
            Screen.TrainDetails.route,
            arguments = listOf(navArgument(Screen.TrainDetails.argName) { type = NavType.IntType })
        ) { backStackEntry ->
            TrainDetailsScreen(
                hiltViewModel(backStackEntry),
                trainNumber = backStackEntry.arguments?.getInt(Screen.TrainDetails.argName) ?: 0
            )
        }
    }
}
