package dev.jylha.station.data.stations.cache

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "stations")
data class StationCacheEntity(

    @PrimaryKey(autoGenerate = false)
    val uic: Int,
    val type: String,
    val passengerTraffic: Boolean,
    val name: String,
    val shortCode: String,
    val countryCode: String,
    val longitude: Double,
    val latitude: Double
)
