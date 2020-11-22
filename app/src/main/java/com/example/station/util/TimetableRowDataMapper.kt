package com.example.station.util

import com.example.station.data.trains.network.CauseNetworkEntity
import com.example.station.data.trains.network.TimetableRowNetworkEntity
import com.example.station.model.DelayCause
import com.example.station.model.TimetableRow
import java.time.ZonedDateTime

/** Maps timetable row network data transfer object into domain model. */
fun TimetableRowNetworkEntity.toDomainModel(): TimetableRow {
    return TimetableRow(
        stationCode = stationCode,
        type = when {
            type.equals("ARRIVAL", ignoreCase = true) -> TimetableRow.Type.Arrival
            type.equals("DEPARTURE", ignoreCase = true) -> TimetableRow.Type.Departure
            else -> throw IllegalArgumentException("Unknown type: $type")
        },
        trainStopping = trainStopping,
        commercialStop = commercialStop,
        track = track,
        cancelled = cancelled,
        scheduledTime = ZonedDateTime.parse(scheduledTime),
        estimatedTime = if (liveEstimateTime != null) ZonedDateTime.parse(liveEstimateTime) else null,
        actualTime = if (actualTime != null) ZonedDateTime.parse(actualTime) else null,
        differenceInMinutes = differenceInMinutes ?: 0,
        markedReady = trainReady != null,
        causes = causes.map { it.toDomainModel() }
    )
}

fun CauseNetworkEntity.toDomainModel(): DelayCause {
    return DelayCause(
        categoryId = categoryCodeId,
        detailedCategoryId = detailedCategoryCodeId,
        thirdLevelCategoryId = thirdCategoryCodeId,
    )
}

