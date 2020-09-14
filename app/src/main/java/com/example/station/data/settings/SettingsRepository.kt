package com.example.station.data.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun station() : Flow<Int?>
    suspend fun setStation(stationUicCode: Int)
}
