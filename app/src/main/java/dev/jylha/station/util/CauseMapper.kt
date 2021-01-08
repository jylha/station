package dev.jylha.station.util

import dev.jylha.station.data.trains.cache.CauseCategoryCacheEntity
import dev.jylha.station.data.trains.cache.PassengerFriendlyNameCacheEntity
import dev.jylha.station.data.trains.network.CauseCategoryNetworkEntity
import dev.jylha.station.data.trains.network.DetailedCauseCategoryNetworkEntity
import dev.jylha.station.data.trains.network.PassengerTermNetworkEntity
import dev.jylha.station.data.trains.network.ThirdLevelCauseCategoryNetworkEntity
import dev.jylha.station.model.CauseCategory
import dev.jylha.station.model.PassengerFriendlyName

fun CauseCategoryNetworkEntity.toDomainModel(): CauseCategory {
    return CauseCategory(
        id = id,
        name = categoryName,
        passengerFriendlyName = passengerTerm?.toDomainModel()
    )
}

fun DetailedCauseCategoryNetworkEntity.toDomainModel(): CauseCategory {
    return CauseCategory(
        id = id,
        name = detailedCategoryName,
        passengerFriendlyName = passengerTerm?.toDomainModel()
    )
}

fun ThirdLevelCauseCategoryNetworkEntity.toDomainModel(): CauseCategory {
    return CauseCategory(
        id = id,
        name = thirdCategoryName,
        passengerFriendlyName = passengerTerm?.toDomainModel()
    )
}

fun CauseCategory.toCacheEntity(level: Int): CauseCategoryCacheEntity {
    return CauseCategoryCacheEntity(
        id = id,
        name = name,
        level = level,
        passengerFriendlyName = passengerFriendlyName?.toCacheEntity()
    )
}

fun CauseCategoryCacheEntity.toDomainModel() : CauseCategory {
    return CauseCategory(
        id = id,
        name = name,
        passengerFriendlyName = passengerFriendlyName?.toDomainModel()
    )
}

private fun PassengerTermNetworkEntity.toDomainModel() =
    PassengerFriendlyName(
        fi = fi,
        en = en,
        sv = sv
    )

private fun PassengerFriendlyName.toCacheEntity() =
    PassengerFriendlyNameCacheEntity(
        fi = fi,
        en = en,
        sv = sv
    )

private fun PassengerFriendlyNameCacheEntity.toDomainModel() =
    PassengerFriendlyName(
        fi = fi,
        en = en,
        sv = sv
    )

