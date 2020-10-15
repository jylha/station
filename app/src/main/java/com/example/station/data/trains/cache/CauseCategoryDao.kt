package com.example.station.data.trains.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CauseCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CauseCategoryCacheEntity>)

    @Query("SELECT * FROM cause_categories WHERE level = :level ORDER BY id ASC")
    fun getCategories(level: Int): Flow<List<CauseCategoryCacheEntity>>

}
