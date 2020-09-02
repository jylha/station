package com.example.station.data.timetable.network

import retrofit2.http.GET
import retrofit2.http.Path

interface TimetableService {

    @GET("live-trains/station/{stationCode}?minutes_before_departure=15&minutes_after_departure=15&minutes_before_arrival=15&minutes_after_arrival=15")
    suspend fun fetchTimetable(@Path("stationCode") stationCode: String): List<TrainNetworkEntity>

}