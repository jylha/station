package com.example.station.ui.home

import com.example.station.model.Station

/** Base class for all results on HomeView. */
sealed class HomeViewResult

/** Results for loading the UIC of recent station from settings. */
sealed class LoadSettings : HomeViewResult() {
    object Loading : LoadSettings()
    data class Success(val stationUic: Int?) : LoadSettings()
    data class Error(val message: String?) : LoadSettings()
}

/** Results for loading a station. */
sealed class LoadStation : HomeViewResult() {
    object Loading : LoadStation()
    data class Success(val station: Station?) : LoadStation()
    data class Error(val message: String?) : LoadStation()
}
