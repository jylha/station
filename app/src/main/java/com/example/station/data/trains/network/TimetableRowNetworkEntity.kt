package com.example.station.data.trains.network

import com.google.gson.annotations.SerializedName

data class TimetableRowNetworkEntity(

    @SerializedName("type")
    val type: String,

    @SerializedName("stationShortCode")
    val stationCode: String,

    @SerializedName("stationUICCode")
    val stationUicCode: Int,

    @SerializedName("trainStopping")
    val trainStopping: Boolean,

    @SerializedName("commercialStop")
    val commercialStop: Boolean? = null,

    @SerializedName("commercialTrack")
    val track: String? = null,

    @SerializedName("scheduledTime")
    val scheduledTime: String,

    @SerializedName("liveEstimateTime")
    val liveEstimateTime: String? = null,

    @SerializedName("actualTime")
    val actualTime: String? = null,

    @SerializedName("differenceInMinutes")
    val differenceInMinutes: Int? = null,

    @SerializedName("trainReady")
    val trainReady: TrainReadyNetworkEntity? = null,

    @SerializedName("cause")
    val cause: CauseNetworkEntity? = null,
)

data class TrainReadyNetworkEntity(
    val timestamp: String
)

data class CauseNetworkEntity(
    val categoryCodeId: Int,
    val categoryCode: String,
    val detailedCategoryCodeId: Int? = null,
    val detailedCategoryCode: String? = null,
    val thirdCategoryCodeId: Int? = null,
    val thirdCategoryCode: String? = null
)

