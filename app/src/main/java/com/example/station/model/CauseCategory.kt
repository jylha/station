package com.example.station.model

import javax.annotation.concurrent.Immutable

@Immutable
data class CauseCategory(
    val id: Int,
    val name: String,
    val passengerFriendlyName: PassengerFriendlyName? = null
)

data class PassengerFriendlyName(
    val fi: String,
    val en: String,
    val sv: String
)

@Immutable
data class CauseCategories(
    val categories: List<CauseCategory>,
    val detailedCategories: List<CauseCategory>,
    val thirdLevelCategories: List<CauseCategory> = emptyList()
)
