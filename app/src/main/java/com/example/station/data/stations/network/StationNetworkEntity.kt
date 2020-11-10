package com.example.station.data.stations.network

import com.google.gson.annotations.SerializedName

data class StationNetworkEntity(

    @SerializedName("passengerTraffic")
    val passengerTraffic: Boolean,

    @SerializedName("type")
    val type: String,

    @SerializedName("stationName")
    val name: String,

    @SerializedName("stationShortCode")
    val shortCode: String,

    @SerializedName("stationUICCode")
    val code: Int,

    @SerializedName("countryCode")
    val countryCode: String,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("latitude")
    val latitude: Double
)
