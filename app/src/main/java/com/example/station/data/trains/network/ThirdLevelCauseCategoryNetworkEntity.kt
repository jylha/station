package com.example.station.data.trains.network

data class ThirdLevelCauseCategoryNetworkEntity(
    val id: Int,
    val thirdCategoryCode: String,
    val thirdCategoryName: String,
    val validFrom: String,
    val validTo: String? = null,
    val passengerTerm: PassengerTerm? = null
) {
    data class PassengerTerm(
        val fi: String,
        val en: String,
        val sv: String
    )
}
