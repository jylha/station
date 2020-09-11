package com.example.station.data.trains

import com.example.station.data.trains.network.TrainService
import com.example.station.model.Train
import com.example.station.util.toDomainObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TrainRepository @Inject constructor(
    private val trainService: TrainService
) {
    fun fetchTrains(stationCode: String, stationUicCode: Int): Flow<List<Train>> {
        return flow {
            val trains = trainService.fetchTrains(stationCode)
                .map { entity -> entity.toDomainObject() }
                .sortedBy { train -> train.nextEvent(stationUicCode) }
            emit(trains)
        }.flowOn(Dispatchers.IO)
    }
}
