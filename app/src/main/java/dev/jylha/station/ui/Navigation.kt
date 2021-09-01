package dev.jylha.station.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
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
        private const val STATION_CODE = "stationCode"

        fun route(stationCode: Int) = "timetable/$stationCode"

        val arguments: List<NamedNavArgument> = listOf(
            navArgument(STATION_CODE) { type = NavType.IntType }
        )

        fun stationCode(backStackEntry: NavBackStackEntry): Int {
            return backStackEntry.arguments?.getInt(STATION_CODE) ?: 0
        }
    }

    object TrainDetails : Screen("train_details/{departureDate}/{trainNumber}") {
        private const val DEPARTURE_DATE = "departureDate"
        private const val TRAIN_NUMBER = "trainNumber"

        fun route(departureDate: String, trainNumber: Int): String =
            "train_details/$departureDate/$trainNumber"

        val arguments = listOf(
            navArgument(DEPARTURE_DATE) { type = NavType.StringType },
            navArgument(TRAIN_NUMBER) { type = NavType.IntType }
        )

        fun departureDate(backStackEntry: NavBackStackEntry): String =
            backStackEntry.arguments?.getString(DEPARTURE_DATE) ?: ""

        fun trainNumber(backStackEntry: NavBackStackEntry): Int =
            backStackEntry.arguments?.getInt(TRAIN_NUMBER) ?: 0
    }
}

/**
 * Station app navigation composable. The composable defines the navigation graph for the
 * application.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StationAppNavigation() {
    val navController = rememberAnimatedNavController()

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
    val navigateToTrainDetails = { departureDate: String, trainNumber: Int ->
        navController.navigate(Screen.TrainDetails.route(departureDate, trainNumber))
    }

    AnimatedNavHost(
        navController, startDestination = Screen.Home.route,
        enterTransition = { _, _ -> fadeIn(1f, snap()) },
        exitTransition = { _, _ -> fadeOut(0f, snap()) }
    ) {
        composable(
            Screen.Home.route,
            enterTransition = { _, _ ->
                fadeIn(0f, tween(600))
            },
            exitTransition = { _, target ->
                if (target.destination.route == Screen.About.route)
                    fadeOut(0f, tween(200, 400))
                else
                    fadeOut(0f, tween(600))
            },
            popEnterTransition = { initial, _ ->
                if (initial.destination.route == Screen.About.route)
                    fadeIn(0f, tween(400))
                else
                    fadeIn(1f, snap())
            },
            popExitTransition = null, // Use default
        ) {
            HomeScreen(
                viewModel = hiltViewModel(),
                onNavigateToStations = { navigateToStations() },
                onNavigateToNearestStation = { navigateToNearestStation() },
                onNavigateToTimetable = { stationCode -> navigateToTimetable(stationCode) },
                onNavigateToAbout = { navigateTo(Screen.About) },
            )
        }
        composable(
            Screen.About.route,
            enterTransition = { _, _ ->
                fadeIn(animationSpec = tween(600))
            },
            exitTransition = { _, _ ->
                fadeOut(animationSpec = tween(400, 200))
            },
            popEnterTransition = null,
        ) { AboutScreen() }
        composable(Screen.Stations.route) {
            StationsScreen(
                viewModel = hiltViewModel(),
                onNavigateToTimetable = { stationCode -> navigateToTimetable(stationCode) },
                onNavigateToNearestStation = { navigateToNearestStation() },
            )
        }
        composable(Screen.NearestStation.route) {
            StationsScreen(
                viewModel = hiltViewModel(),
                onNavigateToTimetable = { stationCode -> navigateToTimetable(stationCode) },
                onNavigateToNearestStation = { navigateToNearestStation() },
                selectNearestStation = true,
            )
        }
        composable(
            Screen.Timetable.route, Screen.Timetable.arguments,
            popEnterTransition = { _, _ -> fadeIn(0f, tween(600)) },
        ) { backStackEntry ->
            TimetableScreen(
                viewModel = hiltViewModel(),
                stationCode = Screen.Timetable.stationCode(backStackEntry),
                onNavigateToStations = { navigateToStations() },
                onNavigateToTrainDetails = { departureDate, trainNumber ->
                    navigateToTrainDetails(departureDate, trainNumber)
                },
            )
        }
        composable(
            Screen.TrainDetails.route, Screen.TrainDetails.arguments,
            enterTransition = { _, _ -> fadeIn(0f, tween(600)) },
        ) { backStackEntry ->
            TrainDetailsScreen(
                viewModel = hiltViewModel(),
                departureDate = Screen.TrainDetails.departureDate(backStackEntry),
                trainNumber = Screen.TrainDetails.trainNumber(backStackEntry)
            )
        }
    }
}
