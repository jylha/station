package com.example.station.data.trains.network

data class DetailedCauseCategoryNetworkEntity(
    val id: Int,
    val detailedCategoryCode: String,
    val detailedCategoryName: String,
    val validFrom: String,
    val validTo: String? = null,
    val passengerTerm: PassengerTermNetworkEntity? = null
)
