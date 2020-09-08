package com.example.station.data.stations.network

import retrofit2.http.GET

interface StationService {

    @GET("metadata/stations")
    suspend fun fetchStations(): List<StationNetworkEntity>
}
