package com.example.station.data.timetable.network

import com.google.gson.annotations.SerializedName

data class TimetableRowNetworkEntity(

    @SerializedName("stationShortCode")
    val stationCode: String,

    @SerializedName("stationUICCode")
    val stationUicCode: Int,

    @SerializedName("commercialTrack")
    val track: String,

    @SerializedName("scheduledTime")
    val scheduledTime: String,

    @SerializedName("liveEstimateTime")
    val liveEstimateTime: String? = null,

    @SerializedName("actualTime")
    val actualTime: String? = null,

    )