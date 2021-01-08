package dev.jylha.station.data.trains.network

import com.google.gson.annotations.SerializedName

data class PassengerTermNetworkEntity(
    @SerializedName("fi")
    val fi: String,
    @SerializedName("sv")
    val sv: String,
    @SerializedName("en")
    val en: String
)
