package com.example.station.ui.stations

import android.location.Location
import androidx.compose.runtime.Immutable
import com.example.station.data.stations.StationNameMapper
import com.example.station.model.Station

@Immutable
data class StationsViewState(
    val stations: List<Station>,
    val recentStations: List<Int> = emptyList(),
    val nameMapper: StationNameMapper? = null,
    private val isLoadingStations: Boolean = false,
    private val isLoadingNameMapper: Boolean = false,
    val selectNearest: Boolean = false,
    val isFetchingLocation: Boolean = false,
    val longitude: Double? = null,
    val latitude: Double? = null,
    val nearestStation: Station? = null,
    val errorMessage: String? = null,
) {
    companion object {
        fun initial(): StationsViewState = StationsViewState(
            stations = emptyList()
        )
    }

    val isLoading: Boolean
        get() = isLoadingStations || isLoadingNameMapper
}

fun StationsViewState.reduce(result: StationsViewResult): StationsViewState {
    return when (result) {

        StationsViewResult.SelectStation -> {
            copy(
                selectNearest = false,
                isFetchingLocation = false,
                nearestStation = null
            )
        }

        StationsViewResult.LoadingStations -> copy(isLoadingStations = true)
        StationsViewResult.ReloadingStations -> this
        StationsViewResult.NoNewData -> this

        is StationsViewResult.StationsData -> {
            val updatedStations = result.stations.updateNames(nameMapper)
            if (selectNearest && !isFetchingLocation) {
                val nearest = if (latitude != null && longitude != null)
                    findNearest(result.stations, latitude, longitude) else null
                copy(
                    stations = updatedStations,
                    isLoadingStations = false,
                    nearestStation = nearest
                )
            } else {
                copy(stations = updatedStations, isLoadingStations = false)
            }
        }

        is StationsViewResult.RecentStations -> copy(recentStations = result.stations)
        is StationsViewResult.Error -> copy(errorMessage = result.message)

        is StationsViewResult.NameMapper -> copy(
            nameMapper = result.mapper, isLoadingNameMapper = false,
            stations = stations.updateNames(result.mapper)
        )

        is StationsViewResult.LoadingNameMapper -> copy(isLoadingNameMapper = true)

        StationsViewResult.SelectNearest -> {
            copy(
                selectNearest = true,
                nearestStation = null,
                isFetchingLocation = true
            )
        }

        is StationsViewResult.Location -> {
            copy(
                isFetchingLocation = false,
                latitude = result.latitude,
                longitude = result.longitude,
                nearestStation = if (isLoading) null else
                    findNearest(stations, result.latitude, result.longitude)
            )
        }

        is StationsViewResult.LocationError -> {
            copy(
                isFetchingLocation = false,
                selectNearest = false
            )
        }
    }
}

private fun findNearest(stations: List<Station>, latitude: Double, longitude: Double): Station? {
    return stations.minByOrNull { station ->
        distanceBetween(station.latitude, station.longitude, latitude, longitude)
    }
}

private fun distanceBetween(
    latitudeFirst: Double,
    longitudeFirst: Double,
    latitudeSecond: Double,
    longitudeSecond: Double
): Float {
    val result = FloatArray(3)
    Location.distanceBetween(latitudeFirst, longitudeFirst, latitudeSecond, longitudeSecond, result)
    return result[0]
}

private fun List<Station>.updateNames(mapper: StationNameMapper?): List<Station> {
    return map { station ->
        val updatedName = mapper?.stationName(station.uic)
        if (updatedName != null) station.copy(name = updatedName) else station
    }.sortedBy { station -> station.name }
}

