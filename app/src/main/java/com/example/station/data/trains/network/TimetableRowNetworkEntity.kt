package com.example.station.data.trains.network

import com.google.gson.annotations.SerializedName

data class TimetableRowNetworkEntity(

    @SerializedName("stationShortCode")
    val stationCode: String,

    @SerializedName("stationUICCode")
    val stationUicCode: Int,

    @SerializedName("type")
    val type: String,

    @SerializedName("commercialTrack")
    val track: String,

    @SerializedName("scheduledTime")
    val scheduledTime: String,

    @SerializedName("liveEstimateTime")
    val liveEstimateTime: String? = null,

    @SerializedName("actualTime")
    val actualTime: String? = null,

    @SerializedName("differenceInMinutes")
    val differenceInMinutes: Int? = null,

    @SerializedName("trainReady")
    val trainReady: TrainReadyNetworkEntry? = null
)


data class TrainReadyNetworkEntry(
    val timestamp: String
)

