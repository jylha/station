package com.example.station.ui.home

import com.example.station.model.Station

data class HomeViewState(
    val loading: Boolean = false,
    val stationUicCode: Int? = null,
    val station: Station? = null
) {
    companion object {
        fun initial() = HomeViewState(loading = true)
    }
}
