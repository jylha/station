package dev.jylha.station.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.jylha.station.data.stations.cache.StationCacheEntity
import dev.jylha.station.data.stations.cache.StationDao
import dev.jylha.station.data.trains.cache.CauseCategoryCacheEntity
import dev.jylha.station.data.trains.cache.CauseCategoryDao

@Database(
    entities = [StationCacheEntity::class, CauseCategoryCacheEntity::class],
    version = 2
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
