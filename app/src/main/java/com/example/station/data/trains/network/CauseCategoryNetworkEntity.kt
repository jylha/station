package com.example.station.data.trains.network

import com.google.gson.annotations.SerializedName

data class CauseCategoryNetworkEntity(
    @SerializedName("id")
    val id: Int,
    @SerializedName("categoryCode")
    val categoryCode: String,
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("validFrom")
    val validFrom: String,
    @SerializedName("validTo")
    val validTo: String? = null,
    @SerializedName("passengerTerm")
    val passengerTerm: PassengerTermNetworkEntity? = null
)
