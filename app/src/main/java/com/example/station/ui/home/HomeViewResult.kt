package com.example.station.ui.home

import com.example.station.model.Station

sealed class HomeViewResult {
    object LoadingSettings : HomeViewResult()
    object SettingsLoaded : HomeViewResult()
    object LoadingStation : HomeViewResult()
    data class StationLoaded(val station: Station) : HomeViewResult()
}
