package com.example.station.data.trains.cache

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cause_categories")
data class CauseCategoryCacheEntity(

    @PrimaryKey(autoGenerate = false)
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


