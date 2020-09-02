package com.example.station.data.timetable

import com.example.station.data.timetable.network.TimetableService
import com.example.station.model.Train
import com.example.station.util.toDomainObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TimetableRepository @Inject constructor(
    private val timetableService: TimetableService
) {
    fun fetchTimetable(stationCode: String): Flow<List<Train>> {
        return flow {
            val trains = timetableService.fetchTimetable(stationCode)
                .map { entity -> entity.toDomainObject() }
            emit(trains)
        }.flowOn(Dispatchers.IO)
    }
}
