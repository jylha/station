package com.example.station.data.trains

import com.example.station.model.Station
import com.example.station.model.Train
import kotlinx.coroutines.flow.Flow

interface TrainRepository {

    /** Returns the list of trains stopping at the specified station. */
    fun trainsAtStation(station: Station): Flow<List<Train>>
}
