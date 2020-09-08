package com.example.station.data.stations

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.example.station.data.stations.network.StationService
import com.example.station.model.Station
import com.example.station.util.toDomainObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class StoreBackedStationRepository @Inject constructor(
    private val stationService: StationService
) : StationRepository {

    @ExperimentalCoroutinesApi
    @OptIn(FlowPreview::class)
    private val store = StoreBuilder
        .from(
            Fetcher.of {
                stationService.fetchStations()
                    .filter { it.passengerTraffic && it.countryCode == "FI" }
                    .map { it.toDomainObject() }
                    .filter { it.type == Station.Type.Station }
            }
        )
        .build()

    @ExperimentalCoroutinesApi
    override fun fetchStations(): Flow<StoreResponse<List<Station>>> {
        return store.stream(StoreRequest.fresh(1))
            .flowOn(Dispatchers.IO)
    }


    /*
    override fun fetchStations(): Flow<List<Station>> {
        return flow {
            val stations = stationService.fetchStations()
                .filter { it.passengerTraffic && it.countryCode == "FI" }
                .map { it.toDomainObject() }
                .filter { it.type == Station.Type.Station }
            emit(stations)
        }.flowOn(Dispatchers.IO)
    }
    */
}

