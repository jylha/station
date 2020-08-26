package com.example.station.data.stations

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


data class Station(val name: String)

class StationsRepository() {

    fun fetchStations(): Flow<List<Station>> {
        return flow {
            val stations = listOf("Helsinki", "Pasila", "Tikkurila")
                .map { Station(it) }
            emit(stations)
        }.flowOn(Dispatchers.IO)
    }
}
