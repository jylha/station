package dev.jylha.station.ui.home

import dev.jylha.station.model.Station

data class HomeViewState(
    val isLoadingSettings: Boolean = false,
    val isLoadingStation: Boolean = false,
    val stationCode: Int? = null,
    val station: Station? = null
) {
    val isLoading: Boolean
        get() = isLoadingSettings || isLoadingStation

    companion object {
        fun initial() = HomeViewState()
    }
}

fun HomeViewState.reduce(result: HomeViewResult): HomeViewState {
    return when (result) {
        LoadSettings.Loading -> copy(isLoadingSettings = true)
        is LoadSettings.Error -> copy(isLoadingSettings = false)
        is LoadSettings.Success -> copy(isLoadingSettings = false)

        LoadStation.Loading -> copy(isLoadingStation = true, isLoadingSettings = false)
        is LoadStation.Error -> copy(isLoadingStation = false)
        is LoadStation.Success -> copy(isLoadingStation = false, station = result.station)
    }
}

