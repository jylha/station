package dev.jylha.station.data.stations

import com.dropbox.android.external.store4.StoreResponse
import dev.jylha.station.model.Station
import kotlinx.coroutines.flow.Flow


/** Interface for fetching station information. */
interface StationRepository {

    /** Fetch all stations. */
    fun fetchStations(): Flow<StoreResponse<List<Station>>>

    /** Fetch a single station with given [stationCode]. */
    suspend fun fetchStation(stationCode: Int): Station

    /** Station name mapper. */
    suspend fun getStationNameMapper(): StationNameMapper

}
