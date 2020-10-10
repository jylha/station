package com.example.station.data.trains

import com.example.station.data.trains.network.TrainService
import com.example.station.model.CauseCategory
import com.example.station.model.Station
import com.example.station.model.Train
import com.example.station.util.toDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DefaultTrainRepository @Inject constructor(
    private val trainService: TrainService
) : TrainRepository {
    override fun trainsAtStation(station: Station): Flow<List<Train>> {
        return flow {
            val trains = trainService.fetchTrains(station.shortCode).toDomainModel()
            emit(trains)
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun causeCategories(): List<CauseCategory> {
        return trainService.fetchCauseCategoryCodes().map { it.toDomainModel() }
    }

    override suspend fun detailedCauseCategories(): List<CauseCategory> {
        return trainService.fetchDetailedCauseCategoryCodes().map { it.toDomainModel() }
    }

}
