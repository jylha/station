package dev.jylha.station.data.stations

import android.content.Context
import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.get
import dev.jylha.station.data.StationDatabase
import dev.jylha.station.data.stations.network.StationService
import dev.jylha.station.model.Station
import dev.jylha.station.util.toCacheEntity
import dev.jylha.station.util.toDomainModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

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

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val store = StoreBuilder
        .from<Int, List<Station>, List<Station>>(
            fetcher = Fetcher.of { _ ->
                stationService.fetchStations()
                    .filter { it.passengerTraffic || it.code == 769 /* Kempele (incorrectly marked in the data). */ }
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

    override fun fetchStations(): Flow<StoreResponse<List<Station>>> {
        return store.stream(StoreRequest.cached(key = 0, refresh = true))
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
