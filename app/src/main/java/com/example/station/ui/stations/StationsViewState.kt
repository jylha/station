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

fun StationsViewState.reduce(result: StationsResult): StationsViewState {
    return when (result) {

        StationsResult.SelectStation -> {
            copy(
                selectNearest = false,
                isFetchingLocation = false,
                nearestStation = null
            )
        }

        StationsResult.LoadingStations -> copy(isLoadingStations = true)
        StationsResult.ReloadingStations -> this
        StationsResult.NoNewData -> this

        is StationsResult.StationsData -> {
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

        is StationsResult.RecentStations -> copy(recentStations = result.stations)
        is StationsResult.Error -> copy(errorMessage = result.message)

        is StationsResult.NameMapper -> copy(
            nameMapper = result.mapper, isLoadingNameMapper = false,
            stations = stations.updateNames(result.mapper)
        )

        is StationsResult.LoadingNameMapper -> copy(isLoadingNameMapper = true)

        StationsResult.SelectNearest -> {
            copy(
                selectNearest = true,
                nearestStation = null,
                isFetchingLocation = true
            )
        }

        is StationsResult.Location -> {
            copy(
                isFetchingLocation = false,
                latitude = result.latitude,
                longitude = result.longitude,
                nearestStation = if (isLoading) null else
                    findNearest(stations, result.latitude, result.longitude)
            )
        }

        is StationsResult.LocationError -> {
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

