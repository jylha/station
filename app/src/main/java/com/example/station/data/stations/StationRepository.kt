package com.example.station.data.stations

import com.dropbox.android.external.store4.StoreResponse
import com.example.station.model.Station
import kotlinx.coroutines.flow.Flow


/** Interface for fetching station information. */
interface StationRepository {

    /** Fetch all stations. */
    fun fetchStations(): Flow<StoreResponse<List<Station>>>

    /** Fetch a single station with given [stationUic]. */
    suspend fun fetchStation(stationUic: Int): Station
}
