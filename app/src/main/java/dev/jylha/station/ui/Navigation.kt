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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dev.jylha.station.R
import dev.jylha.station.ui.about.AboutScreen
import dev.jylha.station.ui.home.HomeScreen
import dev.jylha.station.ui.stations.StationsScreen
import dev.jylha.station.ui.timetable.TimetableScreen
import dev.jylha.station.ui.train.TrainDetailsScreen
import kotlinx.serialization.Serializable

/** Navigation targets. */
private sealed class Screen {

    @Serializable
    data object Home : Screen()

    @Serializable
    data object About : Screen()

    @Serializable
    data object Stations : Screen()

    @Serializable
    data object NearestStation : Screen()

    @Serializable
    data class Timetable(val stationCode: Int) : Screen()

    @Serializable
    data class TrainDetails(val departureDate: String, val trainNumber: Int) : Screen()
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

    val navigateToStations = {
        navController.navigate(Screen.Stations) {
            popUpTo(Screen.Home) {}
        }
    }
    val navigateToNearestStation = {
        navController.navigate(Screen.NearestStation) {
            popUpTo(Screen.Home) {}
        }
    }
    val navigateToTimetable = { stationCode: Int ->
        navController.navigate(Screen.Timetable(stationCode)) {
            popUpTo(Screen.NearestStation) { inclusive = true }
        }
    }
    val navigateToTrainDetails = { departureDate: String, trainNumber: Int ->
        navController.navigate(Screen.TrainDetails(departureDate, trainNumber))
    }

    val navigateToOssLicenses = {
        OssLicensesMenuActivity.setActivityTitle(ossLicensesTitle)
        val intent = Intent(context, OssLicensesMenuActivity::class.java)
        ContextCompat.startActivity(context, intent, null)
    }

    NavHost(
        navController, startDestination = Screen.Home,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable<Screen.Home>(
            enterTransition = { fadeIn(animationSpec = tween(600)) },
            exitTransition = { fadeOut(animationSpec = tween(200, 400)) },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = null, // Use default
        ) {
            HomeScreen(
                viewModel = hiltViewModel(),
                onNavigateToStations = navigateToStations,
                onNavigateToNearestStation = navigateToNearestStation,
                onNavigateToTimetable = { stationCode -> navigateToTimetable(stationCode) },
                onNavigateToAbout = { navController.navigate(Screen.About) },
            )
        }
        composable<Screen.About>(
            enterTransition = { fadeIn(animationSpec = tween(600)) },
            exitTransition = { fadeOut(animationSpec = tween(400, 200)) },
            popEnterTransition = null,
        ) {
            AboutScreen(
                onNavigateToOssLicenses = navigateToOssLicenses
            )
        }
        composable<Screen.Stations> {
            StationsScreen(
                viewModel = hiltViewModel(),
                onNavigateToTimetable = { stationCode -> navigateToTimetable(stationCode) },
                onNavigateToNearestStation = { navigateToNearestStation() },
            )
        }
        composable<Screen.NearestStation> {
            StationsScreen(
                viewModel = hiltViewModel(),
                onNavigateToTimetable = { stationCode -> navigateToTimetable(stationCode) },
                onNavigateToNearestStation = { navigateToNearestStation() },
                selectNearestStation = true,
            )
        }
        composable<Screen.Timetable>(
            popEnterTransition = { fadeIn(animationSpec = tween(600)) },
        ) { backStackEntry ->
            TimetableScreen(
                viewModel = hiltViewModel(),
                stationCode = backStackEntry.toRoute<Screen.Timetable>().stationCode,
                onNavigateToStations = { navigateToStations() },
                onNavigateToTrainDetails = { departureDate, trainNumber ->
                    navigateToTrainDetails(departureDate, trainNumber)
                },
            )
        }
        composable<Screen.TrainDetails>(
            enterTransition = { fadeIn(animationSpec = tween(600)) },
        ) { backStackEntry ->
            TrainDetailsScreen(
                viewModel = hiltViewModel(),
                departureDate = backStackEntry.toRoute<Screen.TrainDetails>().departureDate,
                trainNumber = backStackEntry.toRoute<Screen.TrainDetails>().trainNumber,
            )
        }
    }
}
