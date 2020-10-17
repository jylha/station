package com.example.station.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.station.model.Station
import com.example.station.model.Train

sealed class Screen(val title: String) {
    object Home : Screen("Home")
    object About : Screen("About")
    object SelectStation : Screen("Select Station")
    object SelectNearest : Screen("Select Nearest Station")
    data class Timetable(val station: Station) : Screen("Timetable")
    data class TrainDetails(val train: Train) : Screen("Train Info")
}

class NavigationViewModel : ViewModel() {

    private val history = mutableStateListOf<Screen>(Screen.Home)
    val screen by derivedStateOf { history.last() }

    fun navigateTo(nextScreen: Screen) {
        history.apply {
            when (nextScreen) {
                Screen.Home -> {
                    if (lastIndex > 0) removeRange(1, lastIndex)
                }
                Screen.About -> {
                    add(nextScreen)
                }
                Screen.SelectStation -> {
                    if (lastIndex > 0) removeRange(1, history.lastIndex)
                    add(nextScreen)
                }
                Screen.SelectNearest -> {
                    if (lastIndex > 0) removeRange(1, history.lastIndex)
                    add(nextScreen)
                }
                is Screen.Timetable -> {
                    if (screen == Screen.SelectNearest) removeLast()
                    add(nextScreen)
                }
                is Screen.TrainDetails -> {
                    add(nextScreen)
                }
            }
        }
    }

    fun navigateBack(): Boolean {
        return if (history.size > 1) {
            history.removeLast()
            true
        } else {
            false
        }
    }
}
