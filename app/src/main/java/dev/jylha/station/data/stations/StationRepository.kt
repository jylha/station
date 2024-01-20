package dev.jylha.station.data.stations

import dev.jylha.station.model.Station
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.StoreReadResponse


/** Interface for fetching station information. */
interface StationRepository {

    /** Fetch all stations. */
    fun fetchStations(): Flow<StoreReadResponse<List<Station>>>

    /** Fetch a single station with given [stationCode]. */
    suspend fun fetchStation(stationCode: Int): Station

    /** Station name mapper. */
    suspend fun getStationNameMapper(): StationNameMapper

}
