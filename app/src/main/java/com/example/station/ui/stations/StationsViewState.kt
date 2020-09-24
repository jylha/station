package com.example.station.ui.stations

import androidx.compose.runtime.Immutable
import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Station

@Immutable
data class StationsViewState(
    val stations: List<Station>,
    val recentStations: List<Int> = emptyList(),
    val nameMapper: StationNameMapper? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    companion object {
        fun initial(): StationsViewState = StationsViewState(
            stations = emptyList()
        )
    }
}

fun StationsViewState.reduce(result: StationsViewResult): StationsViewState {
    return when (result) {
        StationsViewResult.LoadingStations -> copy(isLoading = true)
        StationsViewResult.NoNewData -> copy(isLoading = false)
        is StationsViewResult.StationsData ->
            copy(stations = result.stations.updateNames(nameMapper), isLoading = false)
        is StationsViewResult.NameMapper ->
            copy(nameMapper = result.mapper, stations = stations.updateNames(result.mapper))
        is StationsViewResult.RecentStations -> copy(recentStations = result.stations)
        is StationsViewResult.Error -> copy(errorMessage = result.message)
    }
}

private fun List<Station>.updateNames(mapper: StationNameMapper?): List<Station> {
    return map { station ->
        val updatedName = mapper?.stationName(station.uic)
        if (updatedName != null) station.copy(name = updatedName) else station
    }.sortedBy { station -> station.name }
}

