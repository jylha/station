package com.example.station.data.stations

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.example.station.data.stations.cache.StationDatabase
import com.example.station.data.stations.network.StationService
import com.example.station.model.Station
import com.example.station.util.toCacheEntity
import com.example.station.util.toDomainObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StoreBackedStationRepository @Inject constructor(
    private val stationService: StationService,
    private val stationDatabase: StationDatabase
) : StationRepository {

    @ExperimentalCoroutinesApi
    @OptIn(FlowPreview::class)
    private val store = StoreBuilder
        .from<Any, List<Station>, List<Station>>(
            fetcher = Fetcher.of {
                stationService.fetchStations()
                    .filter { it.passengerTraffic && it.countryCode == "FI" }
                    .map { it.toDomainObject() }
                    .filter { it.type == Station.Type.Station }
            },
            sourceOfTruth = SourceOfTruth.of(
                reader = {
                    stationDatabase.stationDao().getAll().map { stations ->
                        stations.map { it.toDomainObject() }
                    }
                },
                writer = { _, stations ->
                    // FIXME: 9.9.2020 Delete all before insert?
                    stationDatabase.stationDao().insertAll(
                        stations.map { it.toCacheEntity() }
                    )
                }
            )
        )
        .build()

    @ExperimentalCoroutinesApi
    override fun fetchStations(): Flow<StoreResponse<List<Station>>> {
        return store.stream(StoreRequest.cached(key = 1, refresh = true))
            .flowOn(Dispatchers.IO)
    }
}

