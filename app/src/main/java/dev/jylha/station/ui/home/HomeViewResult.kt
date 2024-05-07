package dev.jylha.station.ui.home

import dev.jylha.station.model.Station

/** Base class for all results on HomeView. */
sealed class HomeViewResult

/** Results for loading the UIC Code of most recently used station from settings. */
sealed class LoadSettings : HomeViewResult() {
    data object Loading : LoadSettings()
    data object Success : LoadSettings()
    data class Error(val message: String?) : LoadSettings()
}

/** Results for loading a station. */
sealed class LoadStation : HomeViewResult() {
    data object Loading : LoadStation()
    data class Success(val station: Station?) : LoadStation()
    data class Error(val message: String?) : LoadStation()
}
