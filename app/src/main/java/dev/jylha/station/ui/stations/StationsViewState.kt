package dev.jylha.station.ui.stations

import android.location.Location
import androidx.compose.runtime.Immutable
import dev.jylha.station.data.stations.StationNameMapper
import dev.jylha.station.data.stations.renameAndSort
import dev.jylha.station.model.Station

@Immutable
data class StationsViewState(
    val stations: List<Station> = emptyList(),
    val recentStations: List<Int> = emptyList(),
    val nameMapper: StationNameMapper? = null,
    private val isLoadingStations: Boolean = false,
    private val isLoadingNameMapper: Boolean = false,
    val isReloadingStations: Boolean = false,
    val selectNearest: Boolean = false,
    val isFetchingLocation: Boolean = false,
    val longitude: Double? = null,
    val latitude: Double? = null,
    val nearestStation: Station? = null,
    val errorMessage: String? = null,
) {
    companion object {
        fun initial(): StationsViewState = StationsViewState()
    }

    val isLoading: Boolean
        get() = isLoadingStations || isLoadingNameMapper

    fun reduce(result: StationsResult): StationsViewState {
        return when (result) {
            LoadStations.Loading -> copy(isLoadingStations = true)
            LoadStations.Reloading -> copy(isReloadingStations = true)
            LoadStations.NoNewData -> copy(isLoadingStations = false, isReloadingStations = false)
            is LoadStations.Success -> {
                val updatedStations = nameMapper?.renameAndSort(result.stations) ?: result.stations
                if (selectNearest && !isFetchingLocation) {
                    val nearest = findNearest(result.stations, latitude, longitude)
                    copy(
                        stations = updatedStations,
                        isLoadingStations = false,
                        nearestStation = nearest
                    )
                } else {
                    copy(
                        stations = updatedStations,
                        isLoadingStations = false,
                        isReloadingStations = false
                    )
                }
            }
            is LoadStations.Error -> copy(
                isLoadingStations = false,
                isReloadingStations = false,
                errorMessage = result.message
            )

            is RecentStationsUpdated -> copy(recentStations = result.stations)

            LoadNameMapper.Loading -> copy(isLoadingNameMapper = true)
            is LoadNameMapper.Success -> copy(
                nameMapper = result.mapper,
                isLoadingNameMapper = false,
                stations = result.mapper.renameAndSort(stations)
            )
            is LoadNameMapper.Error -> copy(
                isLoadingNameMapper = false,
                errorMessage = result.message
            )

            FetchLocation.Fetching -> copy(
                selectNearest = true,
                nearestStation = null,
                isFetchingLocation = true
            )
            is FetchLocation.Success -> copy(
                isFetchingLocation = false,
                latitude = result.latitude,
                longitude = result.longitude,
                nearestStation = if (isLoadingStations) null else
                    findNearest(stations, result.latitude, result.longitude)
            )
            is FetchLocation.Error -> copy(
                isFetchingLocation = false,
                selectNearest = false,
                errorMessage = result.message
            )

            FetchLocation.Cancel -> copy(
                isFetchingLocation = false,
                selectNearest = false,
                nearestStation = null
            )
        }
    }
}

private fun findNearest(stations: List<Station>, latitude: Double?, longitude: Double?): Station? {
    return if (latitude != null && longitude != null) {
        stations.minByOrNull { station ->
            distanceBetween(station.latitude, station.longitude, latitude, longitude)
        }
    } else {
        null
    }
}

private fun distanceBetween(
    latitudeFirst: Double, longitudeFirst: Double, latitudeSecond: Double, longitudeSecond: Double
): Float {
    val result = FloatArray(3)
    Location.distanceBetween(latitudeFirst, longitudeFirst, latitudeSecond, longitudeSecond, result)
    return result[0]
}

