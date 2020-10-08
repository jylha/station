package com.example.station.data.trains.network

data class CauseCategoryNetworkEntity(
    val id: Int,
    val categoryCode: String,
    val categoryName: String,
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
