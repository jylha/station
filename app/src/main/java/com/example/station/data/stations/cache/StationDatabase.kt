package com.example.station.data.stations.cache

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StationCacheEntity::class], version = 1)
abstract class StationDatabase: RoomDatabase() {
    abstract fun stationDao() : StationDao

    companion object {
        const val name = "stations.db"
    }
}
