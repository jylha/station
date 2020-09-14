package com.example.station.data.stations

import com.dropbox.android.external.store4.StoreResponse
import com.example.station.model.Station
import kotlinx.coroutines.flow.Flow

interface StationRepository {
    fun fetchStations(): Flow<StoreResponse<List<Station>>>
    suspend fun fetchStation(stationUicCode: Int): Station
}
