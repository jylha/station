package dev.jylha.station.data.trains.cache

import androidx.room.Embedded
import androidx.room.Entity

@Entity(tableName = "cause_categories", primaryKeys = ["id", "level"])
data class CauseCategoryCacheEntity(
    val id: Int,
    val name: String,
    val level: Int,
    @Embedded
    val passengerFriendlyName: PassengerFriendlyNameCacheEntity?
)

data class PassengerFriendlyNameCacheEntity(
    val fi: String,
    val en: String,
    val sv: String
)


