package com.example.station.data.trains.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TrainService {

    @GET("trains/latest/{number}")
    suspend fun fetchTrain(@Path("number") number: Int): List<TrainNetworkEntity>

    @GET("live-trains/station/{stationCode}")
    suspend fun fetchTrains(
        @Path("stationCode") stationCode: String,
        @Query("minutes_before_departure") minutesBeforeDeparture: Int = 120,
        @Query("minutes_after_departure") minutesAfterDeparture: Int = 30,
        @Query("minutes_before_arrival") minutesBeforeArrival: Int = 120,
        @Query("minutes_after_arrival") minutesAfterArrival: Int = 30,
        @Query("train_categories") trainCategories: String? = "Long-Distance,Commuter"
    ): List<TrainNetworkEntity>

    @GET("metadata/cause-category-codes")
    suspend fun fetchCauseCategoryCodes(): List<CauseCategoryNetworkEntity>

    @GET("metadata/detailed-cause-category-codes")
    suspend fun fetchDetailedCauseCategoryCodes(): List<DetailedCauseCategoryNetworkEntity>

    @GET("metadata/third-cause-category-codes")
    suspend fun fetchThirdLevelCauseCategoryCodes(): List<ThirdLevelCauseCategoryNetworkEntity>
}
