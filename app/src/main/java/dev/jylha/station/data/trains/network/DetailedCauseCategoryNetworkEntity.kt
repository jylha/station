package dev.jylha.station.data.trains.network

import com.google.gson.annotations.SerializedName

data class DetailedCauseCategoryNetworkEntity(
    @SerializedName("id")
    val id: Int,
    @SerializedName("detailedCategoryCode")
    val detailedCategoryCode: String,
    @SerializedName("detailedCategoryName")
    val detailedCategoryName: String,
    @SerializedName("validFrom")
    val validFrom: String,
    @SerializedName("validTo")
    val validTo: String? = null,
    @SerializedName("passengerTerm")
    val passengerTerm: PassengerTermNetworkEntity? = null
)
