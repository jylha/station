package dev.jylha.station.data.trains.network

import com.google.gson.annotations.SerializedName

data class ThirdLevelCauseCategoryNetworkEntity(
    @SerializedName("id")
    val id: Int,
    @SerializedName("thirdCategoryCode")
    val thirdCategoryCode: String,
    @SerializedName("thirdCategoryName")
    val thirdCategoryName: String,
    @SerializedName("validFrom")
    val validFrom: String,
    @SerializedName("validTo")
    val validTo: String? = null,
    @SerializedName("passengerTerm")
    val passengerTerm: PassengerTermNetworkEntity? = null
)
