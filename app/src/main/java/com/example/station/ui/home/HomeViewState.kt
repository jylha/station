package com.example.station.ui.home

import com.example.station.model.Station

data class HomeViewState(
    val isLoadingSettings: Boolean = false,
    val isLoadingStation: Boolean = false,
    val stationUicCode: Int? = null,
    val station: Station? = null
) {
    val isLoading: Boolean
        get() = isLoadingSettings || isLoadingStation

    companion object {
        fun initial() = HomeViewState(isLoadingSettings = true)
    }
}

fun HomeViewState.reduce(result: HomeViewResult): HomeViewState {
    return when (result) {
        HomeViewResult.LoadingSettings -> copy(isLoadingSettings = true)
        HomeViewResult.SettingsLoaded -> copy(isLoadingSettings = false)
        HomeViewResult.LoadingStation -> copy(isLoadingSettings = false, isLoadingStation = true)
        is HomeViewResult.StationLoaded -> copy(isLoadingStation = false, station = result.station)
    }
}

