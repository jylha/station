package com.example.station.data.settings

import com.example.station.model.Train
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    /** Returns currently selected station's UIC code, or null if none is selected. */
    fun station(): Flow<Int?>

    /** Set selected station. */
    suspend fun setStation(stationUicCode: Int)

    /**
     * Returns a list of recently selected station UIC codes. The list is updated
     * on setStation calls to include the set station.
     */
    fun recentStations(): Flow<List<Int>>

    /** Returns train categories currently selected to be shown in the timetable. */
    fun trainCategories(): Flow<Set<Train.Category>?>

    /** Set selected train categories. */
    suspend fun setTrainCategories(categories: Set<Train.Category>)

}
