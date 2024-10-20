package dev.jylha.station.util

import dev.jylha.station.data.trains.network.CauseNetworkEntity
import dev.jylha.station.data.trains.network.TimetableRowNetworkEntity
import dev.jylha.station.model.DelayCause
import dev.jylha.station.model.TimetableRow
import kotlinx.datetime.Instant

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
        scheduledTime = Instant.parse(scheduledTime),
        estimatedTime = if (liveEstimateTime != null) Instant.parse(liveEstimateTime) else null,
        actualTime = if (actualTime != null) Instant.parse(actualTime) else null,
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

