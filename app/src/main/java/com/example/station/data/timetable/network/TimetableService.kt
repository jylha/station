package com.example.station.data.timetable.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TimetableService {

    @GET("live-trains/station/{stationCode}")
    suspend fun fetchTimetable(
        @Path("stationCode") stationCode: String,
        @Query("minutes_before_departure") minutesBeforeDeparture: Int = 120,
        @Query("minutes_after_departure") minutesAfterDeparture: Int = 30,
        @Query("minutes_before_arrival") minutesBeforeArrival: Int = 120,
        @Query("minutes_after_arrival") minutesAfterArrival: Int = 30
    ): List<TrainNetworkEntity>
}