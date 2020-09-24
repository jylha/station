package com.example.station.ui.stations

import androidx.compose.runtime.Immutable
import com.dropbox.android.external.store4.StoreResponse
import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Station

@Immutable
data class StationsViewState(
    val stations: List<Station>,
    val recentStations: List<Int>,
    val nameMapper: StationNameMapper? = null,
    val isLoading: Boolean,
    val errorMessage: String?,
) {
    companion object {
        fun initial() = StationsViewState(
            stations = emptyList(),
            recentStations = emptyList(),
            isLoading = false,
            errorMessage = null
        )
    }
}

fun StationsViewState.reduce(result: StoreResponse<List<Station>>): StationsViewState {
    return when (result) {
        is StoreResponse.Loading -> copy(isLoading = true)
        is StoreResponse.Data ->
            copy(stations = result.value.updateNames(nameMapper), isLoading = false)
        is StoreResponse.NoNewData -> copy(isLoading = false)
        is StoreResponse.Error.Exception -> copy(errorMessage = result.errorMessageOrNull())
        is StoreResponse.Error.Message -> copy(errorMessage = result.message)
    }
}

fun StationsViewState.reduce(result: StationViewResult): StationsViewState {
    return when (result) {
        is StationViewResult.NameMapper -> {
            val mapper = result.mapper
            copy(nameMapper = mapper, stations = stations.updateNames(mapper))
        }

        is StationViewResult.RecentStations -> {
            copy(recentStations = result.stations)
        }
    }
}

fun List<Station>.updateNames(mapper: StationNameMapper?): List<Station> {
    return map { station ->
        val updatedName = mapper?.stationName(station.uic)
        if (updatedName != null) station.copy(name = updatedName) else station
    }.sortedBy { station -> station.name }
}

