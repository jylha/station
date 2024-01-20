package dev.jylha.station.data.stations

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.jylha.station.data.StationDatabase
import dev.jylha.station.data.stations.network.StationService
import dev.jylha.station.model.Station
import dev.jylha.station.util.toCacheEntity
import dev.jylha.station.util.toDomainModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.impl.extensions.get

/**
 * StationRepository implementation that uses Store to manage fetching station data from
 * the network with [stationService] and caching it locally on [stationDatabase].
 */
class StoreBackedStationRepository @Inject constructor(
    private val stationService: StationService,
    private val stationDatabase: StationDatabase,
    @ApplicationContext val context: Context
) : StationRepository {

    private val mutex = Mutex()
    private lateinit var stationNameMapper: StationNameMapper

    // This was needed because data contained incorrect value for the passengerTraffic field.
    private val KempeleUicCode = 769

    private val store = StoreBuilder
        .from<Int, List<Station>, List<Station>>(
            fetcher = Fetcher.of { _ ->
                stationService.fetchStations()
                    .filter { it.countryCode == "FI" && (it.passengerTraffic || it.code == KempeleUicCode) }
                    .map { it.toDomainModel() }
                    .filter { it.type == Station.Type.Station || it.type == Station.Type.StoppingPoint }
            },
            sourceOfTruth = SourceOfTruth.of(
                reader = { key ->
                    if (key != 0) {
                        stationDatabase.stationDao().getStation(key)
                            .map { listOf(it.toDomainModel()) }
                    } else {
                        stationDatabase.stationDao().getAll()
                            .map { stations ->
                                stations.map { it.toDomainModel() }
                                    .ifEmpty { null }
                            }
                    }
                },
                writer = { _, stations ->
                    with(stationDatabase.stationDao()) {
                        deleteAll()
                        insertAll(stations.map { entity -> entity.toCacheEntity() })
                    }
                }
            )
        )
        .build()

    override fun fetchStations(): Flow<StoreReadResponse<List<Station>>> {
        return store.stream(StoreReadRequest.cached(key = 0, refresh = true))
            .flowOn(Dispatchers.IO)
    }

    override suspend fun fetchStation(stationCode: Int): Station {
        require(stationCode > 0)
        return withContext(Dispatchers.IO) {
            store.get(key = stationCode)
                .first { station -> station.code == stationCode }
        }
    }

    override suspend fun getStationNameMapper(): StationNameMapper {
        return withContext(Dispatchers.IO) {
            mutex.withLock {
                if (!::stationNameMapper.isInitialized) {
                    val stations = store.get(key = 0)
                    stationNameMapper = LocalizedStationNames.from(stations, context)
                }
                stationNameMapper
            }
        }
    }
}
