package com.example.station.util

import com.example.station.data.trains.network.CauseCategoryNetworkEntity
import com.example.station.model.CauseCategory

fun CauseCategoryNetworkEntity.toDomainModel() : CauseCategory {
    return CauseCategory(
        id = id,
        name = categoryName
    )
}
