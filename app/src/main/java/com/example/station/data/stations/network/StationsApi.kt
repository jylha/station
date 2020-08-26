package com.example.station.data.stations.network

import retrofit2.http.GET

interface StationsApi {

    @GET("stations")
    suspend fun fetchStations(): List<StationNetworkEntity>
}