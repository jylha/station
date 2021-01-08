package dev.jylha.station.ui.stations

import dev.jylha.station.data.stations.StationNameMapper
import dev.jylha.station.model.Station

/** Base class for all stations results. */
sealed class StationsResult

/** Result for update on recently selected stations list. */
data class RecentStationsUpdated(val stations: List<Int>) : StationsResult()

/** Results for loading stations. */
sealed class LoadStations : StationsResult() {
    object Loading : LoadStations()
    object Reloading : LoadStations()
    object NoNewData : LoadStations()
    data class Success(val stations: List<Station>) : LoadStations()
    data class Error(val message: String?) : LoadStations()
}

/** Results for loading station name mapper. */
sealed class LoadNameMapper : StationsResult() {
    object Loading : LoadNameMapper()
    data class Success(val mapper: StationNameMapper) : LoadNameMapper()
    data class Error(val message: String?) : LoadNameMapper()
}

/** Results for fetching location and selecting nearest station. */
sealed class FetchLocation : StationsResult() {
    object Fetching : FetchLocation()
    data class Success(val latitude: Double, val longitude: Double) : FetchLocation()
    data class Error(val message: String?) : FetchLocation()

    /** This is used for changing mode back to selecting station from list of stations. */
    object Cancel : FetchLocation()
}
