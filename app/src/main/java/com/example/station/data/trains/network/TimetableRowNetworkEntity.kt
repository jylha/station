package com.example.station.data.trains.network

import com.google.gson.annotations.SerializedName

data class TimetableRowNetworkEntity(

    @SerializedName("type")
    val type: String,

    @SerializedName("stationShortCode")
    val stationShortCode: String,

    @SerializedName("stationUICCode")
    val stationCode: Int,

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

    @SerializedName("causes")
    val causes: List<CauseNetworkEntity> = emptyList()
)

data class TrainReadyNetworkEntity(
    @SerializedName("timestamp")
    val timestamp: String
)

data class CauseNetworkEntity(
    @SerializedName("categoryCodeId")
    val categoryCodeId: Int,
    @SerializedName("categoryCode")
    val categoryCode: String,
    @SerializedName("detailedCategoryCodeId")
    val detailedCategoryCodeId: Int? = null,
    @SerializedName("detailedCategoryCode")
    val detailedCategoryCode: String? = null,
    @SerializedName("thirdCategoryCodeId")
    val thirdCategoryCodeId: Int? = null,
    @SerializedName("thirdCategoryCode")
    val thirdCategoryCode: String? = null
)

