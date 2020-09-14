package com.example.station.data.settings

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.datastore.preferences.preferencesSetKey
import com.example.station.model.Train
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val STATION_UIC_CODE = preferencesKey<Int>("stationUicCode")
private val TRAIN_CATEGORIES = preferencesSetKey<String>("trainCategories")

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

    override fun trainCategories(): Flow<Set<Train.Category>?> {
        return dataStore.data.map { preferences ->
            preferences[TRAIN_CATEGORIES]?.map { category ->
                when (category) {
                    Train.Category.Commuter.name -> Train.Category.Commuter
                    Train.Category.LongDistance.name -> Train.Category.LongDistance
                    else -> null
                }
            }?.mapNotNull { it }?.toSet()
        }
    }

    override suspend fun setTrainCategories(categories: Set<Train.Category>) {
        dataStore.edit { preferences ->
            preferences[TRAIN_CATEGORIES] = categories.map { it.name }.toSet()
        }
    }
}
