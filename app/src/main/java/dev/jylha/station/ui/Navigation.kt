package dev.jylha.station.ui

import android.content.Intent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dev.jylha.station.R
import dev.jylha.station.ui.about.AboutScreen
import dev.jylha.station.ui.home.HomeScreen
import dev.jylha.station.ui.stations.StationsScreen
import dev.jylha.station.ui.timetable.TimetableScreen
import dev.jylha.station.ui.train.TrainDetailsScreen

/** Navigation targets. */
private sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object About : Screen("about")
    data object Stations : Screen("stations")
    data object NearestStation : Screen("nearest_station")

    data object Timetable : Screen("timetable/{stationCode}") {
        private const val STATION_CODE = "stationCode"

        fun route(stationCode: Int) = "timetable/$stationCode"

        val arguments: List<NamedNavArgument> = listOf(
            navArgument(STATION_CODE) { type = NavType.IntType }
        )

        fun stationCode(backStackEntry: NavBackStackEntry): Int {
            return backStackEntry.arguments?.getInt(STATION_CODE) ?: 0
        }
    }

    data object TrainDetails : Screen("train_details/{departureDate}/{trainNumber}") {
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
@Composable
fun StationAppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val ossLicensesTitle = stringResource(id = R.string.label_oss_licenses)

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

    val navigateToOssLicenses = {
        OssLicensesMenuActivity.setActivityTitle(ossLicensesTitle)
        val intent = Intent(context, OssLicensesMenuActivity::class.java)
        ContextCompat.startActivity(context, intent, null)
    }

    NavHost(
        navController, startDestination = Screen.Home.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(
            Screen.Home.route,
            enterTransition = { fadeIn(animationSpec = tween(600)) },
            exitTransition = {
                if (targetState.destination.route == Screen.About.route)
                    fadeOut(animationSpec = tween(200, 400))
                else
                    fadeOut(animationSpec = tween(600))
            },
            popEnterTransition = {
                if (initialState.destination.route == Screen.About.route)
                    fadeIn(animationSpec = tween(400))
                else
                    EnterTransition.None
            },
            popExitTransition = null, // Use default
        ) {
            HomeScreen(
                viewModel = hiltViewModel(),
                onNavigateToStations = navigateToStations,
                onNavigateToNearestStation = navigateToNearestStation,
                onNavigateToTimetable = { stationCode -> navigateToTimetable(stationCode) },
                onNavigateToAbout = { navigateTo(Screen.About) },
            )
        }
        composable(
            Screen.About.route,
            enterTransition = { fadeIn(animationSpec = tween(600)) },
            exitTransition = { fadeOut(animationSpec = tween(400, 200)) },
            popEnterTransition = null,
        ) {
            AboutScreen(
                onNavigateToOssLicenses = navigateToOssLicenses
            )
        }
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
            popEnterTransition = { fadeIn(animationSpec = tween(600)) },
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
            enterTransition = { fadeIn(animationSpec = tween(600)) },
        ) { backStackEntry ->
            TrainDetailsScreen(
                viewModel = hiltViewModel(),
                departureDate = Screen.TrainDetails.departureDate(backStackEntry),
                trainNumber = Screen.TrainDetails.trainNumber(backStackEntry)
            )
        }
    }
}
