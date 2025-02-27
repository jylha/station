package dev.jylha.station.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import dev.jylha.station.domain.SettingsRepository
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DefaultSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private val maxRecentCount = 3
    private val currentStationKey = intPreferencesKey("stationUicCode")
    private val recentStationsKey = stringSetPreferencesKey("recentStations")
    private val trainCategoriesKey = stringSetPreferencesKey("trainCategories")
    private val timetableTypesKey = stringSetPreferencesKey("timetableTypes")

    override fun station(): Flow<Int?> {
        return dataStore.data.map { preferences -> preferences[currentStationKey] }
            .distinctUntilChanged()
    }

    override suspend fun setStation(stationCode: Int) {
        dataStore.edit { preferences ->
            val currentStation = stationCode.toString()
            val recentStations = (preferences[recentStationsKey] ?: emptySet()).toMutableList()
            if (recentStations.contains(currentStation)) {
                recentStations.remove(currentStation)
            }

            val updatedRecentStations = listOf(currentStation) + recentStations.take(maxRecentCount - 1)
            preferences[currentStationKey] = stationCode
            preferences[recentStationsKey] = updatedRecentStations.toSet()
        }
    }

    override fun recentStations(): Flow<List<Int>> {
        return dataStore.data.map { preferences ->
            (preferences[recentStationsKey] ?: emptySet())
                .toList()
                .map { it.toInt() }
        }.distinctUntilChanged()
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
        }.distinctUntilChanged()
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
        }.distinctUntilChanged()
    }

    override suspend fun setTimetableTypes(types: Set<TimetableRow.Type>) {
        dataStore.edit { preferences ->
            preferences[timetableTypesKey] = types.map { it.name }.toSet()
        }
    }
}
