package com.example.station.data.stations

import com.example.station.data.stations.network.StationsApi
import com.example.station.model.Station
import com.example.station.util.toDomainObject
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StationsRepository() {

    val gson = GsonBuilder().create()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://rata.digitraffic.fi/api/v1/metadata/")
        .addConverterFactory(GsonConverterFactory.create(gson))

    val api = retrofit.build().create(StationsApi::class.java)

    fun fetchStations(): Flow<List<Station>> {
        return flow {
            val stations = api.fetchStations()
                .filter { it.passengerTraffic }
                .map{ it.toDomainObject() }
                .filter { it.type == Station.Type.Station }
            emit(stations)
        }.flowOn(Dispatchers.IO)
    }
}
