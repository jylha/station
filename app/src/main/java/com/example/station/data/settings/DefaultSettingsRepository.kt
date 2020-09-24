package com.example.station.data.settings

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.datastore.preferences.preferencesSetKey
import com.example.station.model.Train
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class DefaultSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val maxRecentCount = 3
    private val currentStationKey = preferencesKey<Int>("stationUicCode")
    private val recentStationsKey = preferencesSetKey<String>("recentStations")
    private val trainCategoriesKey = preferencesSetKey<String>("trainCategories")

    private val dataStore = context.createDataStore("preferences")

    override fun station(): Flow<Int?> {
        return dataStore.data.map { preferences -> preferences[currentStationKey] }
    }

    @OptIn(InternalCoroutinesApi::class)
    override suspend fun setStation(stationUicCode: Int) {
        dataStore.edit { preferences ->
            val recent = (preferences[recentStationsKey] ?: emptySet()).toMutableList()
            if (!recent.contains(stationUicCode.toString())) {
                if (recent.size == maxRecentCount) {
                    recent.removeLast()
                }
                recent.add(0, stationUicCode.toString())
            }
            preferences[currentStationKey] = stationUicCode
            preferences[recentStationsKey] = recent.toSet()
        }
    }

    override fun recentStations(): Flow<List<Int>> {
        return dataStore.data.map { preferences ->
            (preferences[recentStationsKey] ?: emptySet())
                .toList()
                .map { it.toInt() }
        }
    }

    override fun trainCategories(): Flow<Set<Train.Category>?> {
        return dataStore.data.map { preferences ->
            preferences[trainCategoriesKey]?.map { category ->
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
            preferences[trainCategoriesKey] = categories.map { it.name }.toSet()
        }
    }
}
