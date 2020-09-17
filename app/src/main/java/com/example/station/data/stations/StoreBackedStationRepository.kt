package com.example.station.data.stations

import android.content.Context
import android.content.res.XmlResourceParser
import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.get
import com.example.station.R
import com.example.station.data.stations.cache.StationDatabase
import com.example.station.data.stations.network.StationService
import com.example.station.model.Station
import com.example.station.util.toCacheEntity
import com.example.station.util.toDomainModel
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
            fetcher = Fetcher.of { key ->
                stationService.fetchStations()
                    .filter { it.passengerTraffic && it.countryCode == "FI" }
                    .map { it.toDomainModel() }
                    .filter { it.type == Station.Type.Station }
            },
            sourceOfTruth = SourceOfTruth.of(
                reader = { key ->
                    if (key != 0) {
                         stationDatabase.stationDao().getStation(key)
                            .map { listOf(it.toDomainModel()) }
                    } else {
                        stationDatabase.stationDao().getAll().map { stations ->
                            stations.map { it.toDomainModel() }
                        }
                    }
                },
                writer = { _, stations ->
                    // FIXME: 9.9.2020 Delete all before insert?
                    stationDatabase.stationDao().insertAll(
                        stations.map { entity -> entity.toCacheEntity() }
                    )
                }
            )
        )
        .build()

    override fun fetchStations(): Flow<StoreResponse<List<Station>>> {
        return store.stream(StoreRequest.cached(key = 0, refresh = true))
            .flowOn(Dispatchers.IO)
    }

    override suspend fun fetchStation(stationUic: Int): Station {
        require(stationUic > 0)
        return store.get(key = stationUic)
            .first { station -> station.uic == stationUic }
    }

    override suspend fun getStationNameMapper(): StationNameMapper {
        withContext(Dispatchers.Default) {
            mutex.withLock {
                if (!::stationNameMapper.isInitialized) {
                    val stations = store.get(key = 0)
                    stationNameMapper = LocalizedStationNames.create(stations, context)
                }
            }
        }
        return stationNameMapper
    }
}

/** Gets localized or commercial station names from resources. */
private fun stationNamesFromResources(context: Context): Map<Int, String> {
    val stationNames = mutableMapOf<Int, String>()
    context.resources.getXml(R.xml.stations).use { parser ->
        while (parser.next() != XmlResourceParser.END_DOCUMENT) {
            if (parser.eventType == XmlResourceParser.START_TAG &&
                parser.name == "station"
            ) {
                var uic = 0
                var resId = 0
                for (index in 0 until parser.attributeCount) {
                    when (parser.getAttributeName(index)) {
                        "uic" -> uic = parser.getAttributeValue(index).toInt()
                        "name" -> resId = parser.getAttributeResourceValue(index, 0)
                    }
                }
                if (uic != 0 && resId != 0) {
                    val name = context.resources.getString(resId)
                    if (name.isNotBlank()) {
                        stationNames += uic to name
                    }
                }
            }
        }
    }
    return stationNames
}
