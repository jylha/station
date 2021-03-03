package dev.jylha.station.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("preferences")

class DefaultSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val maxRecentCount = 3
    private val currentStationKey = intPreferencesKey("stationUicCode")
    private val recentStationsKey = stringSetPreferencesKey("recentStations")
    private val trainCategoriesKey = stringSetPreferencesKey("trainCategories")
    private val timetableTypesKey = stringSetPreferencesKey("timetableTypes")

    private val dataStore = context.dataStore

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
