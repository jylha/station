package com.example.station.data.settings

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val STATION_UIC_CODE = preferencesKey<Int>("stationUicCode")

class DefaultSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val dataStore = context.createDataStore("preferences")

    override fun station(): Flow<Int?> {
        return dataStore.data.map { preferences -> preferences[STATION_UIC_CODE] }
    }

    override suspend fun setStation(stationUicCode: Int) {
        dataStore.edit { preferences ->
            preferences[STATION_UIC_CODE] = stationUicCode
        }
    }
}
