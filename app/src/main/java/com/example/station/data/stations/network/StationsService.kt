package com.example.station.data.stations.network

import retrofit2.http.GET

interface StationsService {

    @GET("metadata/stations")
    suspend fun fetchStations(): List<StationNetworkEntity>
}