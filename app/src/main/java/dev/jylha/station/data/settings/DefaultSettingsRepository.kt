package dev.jylha.station.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.core.preferencesSetKey
import androidx.datastore.preferences.createDataStore
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val timetableTypesKey = preferencesSetKey<String>("timetableTypes")

    private val dataStore = context.createDataStore("preferences")

    override fun station(): Flow<Int?> {
        return dataStore.data.map { preferences -> preferences[currentStationKey] }
    }

    override suspend fun setStation(stationCode: Int) {
        dataStore.edit { preferences ->
            val recent = (preferences[recentStationsKey] ?: emptySet()).toMutableList()
            val station = stationCode.toString()
            if (recent.contains(station)) {
                recent.remove(station)
            } else if (recent.size == maxRecentCount) {
                recent.removeLast()
            }
            recent.add(0, station)
            preferences[currentStationKey] = stationCode
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
            preferences[trainCategoriesKey]?.mapNotNull { category ->
                when (category) {
                    Train.Category.Commuter.name -> Train.Category.Commuter
                    Train.Category.LongDistance.name -> Train.Category.LongDistance
                    else -> null
                }
            }?.toSet()
        }
    }

    override suspend fun setTrainCategories(categories: Set<Train.Category>) {
        dataStore.edit { preferences ->
            preferences[trainCategoriesKey] = categories.map { it.name }.toSet()
        }
    }

    override fun timetableTypes(): Flow<Set<TimetableRow.Type>?> {
        return dataStore.data.map { preferences ->
            preferences[timetableTypesKey]?.mapNotNull { type ->
                when (type) {
                    TimetableRow.Type.Arrival.name -> TimetableRow.Type.Arrival
                    TimetableRow.Type.Departure.name -> TimetableRow.Type.Departure
                    else -> null
                }
            }?.toSet()
        }
    }

    override suspend fun setTimetableTypes(types: Set<TimetableRow.Type>) {
        dataStore.edit { preferences ->
            preferences[timetableTypesKey] = types.map { it.name }.toSet()
        }
    }
}