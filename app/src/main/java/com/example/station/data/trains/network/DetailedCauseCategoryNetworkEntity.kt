package com.example.station.data.trains.network

data class DetailedCauseCategoryNetworkEntity(
    val id: Int,
    val detailedCategoryCode: String,
    val detailedCategoryName: String,
    val validFrom: String,
    val validTo: String? = null,
    val passengerTerm: PassengerTerm? = null
) {
    data class PassengerTerm(
        val fi: String,
        val sv: String,
        val en: String
    )
}
