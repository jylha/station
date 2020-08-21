package com.example.station

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

sealed class Screen(val title: String) {

    object Home : Screen("Home")
    object SelectStation : Screen("Select Station")
    data class Timetable(val stationId: String) : Screen("Timetable")
}


class NavigationViewModel : ViewModel() {

    var screen by mutableStateOf<Screen>(Screen.Home)
        private set

    fun navigateTo(screen: Screen) {
        this.screen = screen
    }

    fun navigateBack(): Boolean {
        return if (screen != Screen.Home) {
            screen = Screen.Home
            true
        } else {
            false
        }
    }
}