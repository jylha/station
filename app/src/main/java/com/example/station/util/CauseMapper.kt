package com.example.station.util

import com.example.station.data.trains.network.CauseCategoryNetworkEntity
import com.example.station.data.trains.network.DetailedCauseCategoryNetworkEntity
import com.example.station.data.trains.network.PassengerTermNetworkEntity
import com.example.station.data.trains.network.ThirdLevelCauseCategoryNetworkEntity
import com.example.station.model.CauseCategory
import com.example.station.model.PassengerFriendlyName

fun CauseCategoryNetworkEntity.toDomainModel() : CauseCategory {
    return CauseCategory(
        id = id,
        name = categoryName,
        passengerFriendlyName = passengerTerm?.toDomainModel()
    )
}

fun DetailedCauseCategoryNetworkEntity.toDomainModel() : CauseCategory {
    return CauseCategory(
        id = id,
        name = detailedCategoryName,
        passengerFriendlyName = passengerTerm?.toDomainModel()
    )
}

fun ThirdLevelCauseCategoryNetworkEntity.toDomainModel() : CauseCategory {
    return CauseCategory(
        id = id,
        name = thirdCategoryName,
        passengerFriendlyName = passengerTerm?.toDomainModel()
    )
}

fun PassengerTermNetworkEntity.toDomainModel() : PassengerFriendlyName{
    return PassengerFriendlyName(
        fi = fi,
        en = en,
        sv = sv
    )
}
