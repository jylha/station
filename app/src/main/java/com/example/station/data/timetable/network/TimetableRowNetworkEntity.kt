package com.example.station.data.timetable.network

import com.google.gson.annotations.SerializedName

data class TimetableRowNetworkEntity(

    @SerializedName("stationShortCode")
    val stationCode: String,

    @SerializedName("stationUICCode")
    val stationUicCode: Int,

    @SerializedName("commercialTrack")
    val track: String
)