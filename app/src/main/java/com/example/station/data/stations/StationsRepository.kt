package com.example.station.data.stations

import com.example.station.data.stations.network.StationsService
import com.example.station.model.Station
import com.example.station.util.toDomainObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StationsRepository @Inject constructor(
    private val stationsService: StationsService
) {
    fun fetchStations(): Flow<List<Station>> {
        return flow {
            val stations = stationsService.fetchStations()
                .filter { it.passengerTraffic && it.countryCode == "FI" }
                .map { it.toDomainObject() }
                .filter { it.type == Station.Type.Station }
            emit(stations)
        }.flowOn(Dispatchers.IO)
    }
}
