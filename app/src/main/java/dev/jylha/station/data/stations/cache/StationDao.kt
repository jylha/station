package dev.jylha.station.data.stations.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stationEntity: StationCacheEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stations: List<StationCacheEntity>)

    @Query("SELECT * FROM stations ORDER BY name ASC")
    fun getAll(): Flow<List<StationCacheEntity>>

    @Query("SELECT * FROM stations WHERE uic = :stationCode")
    fun getStation(stationCode: Int): Flow<StationCacheEntity>

    @Query("DELETE FROM stations")
    suspend fun deleteAll()
}
