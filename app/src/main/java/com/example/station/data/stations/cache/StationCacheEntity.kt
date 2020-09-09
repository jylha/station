package com.example.station.data.stations.cache

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "stations")
data class StationCacheEntity(

    @PrimaryKey(autoGenerate = false)
    val uicCode: Int,
    val passengerTraffic: Boolean,
    val name: String,
    val code: String,
    val countryCode: String,
    val longitude: Double,
    val latitude: Double
)
