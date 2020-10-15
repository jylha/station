package com.example.station.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.station.data.stations.cache.StationCacheEntity
import com.example.station.data.stations.cache.StationDao
import com.example.station.data.trains.cache.CauseCategoryCacheEntity
import com.example.station.data.trains.cache.CauseCategoryDao

@Database(
    entities = [StationCacheEntity::class, CauseCategoryCacheEntity::class],
    version = 2f
)
abstract class StationDatabase : RoomDatabase() {

    /** Data access object for stations table. */
    abstract fun stationDao(): StationDao

    /** Data access object for cause_categories table. */
    abstract fun causeCategoryDao(): CauseCategoryDao

    companion object {
        const val name = "stations.db"
    }
}
