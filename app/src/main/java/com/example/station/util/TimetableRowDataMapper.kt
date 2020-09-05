package com.example.station.util

import com.example.station.data.timetable.network.TimetableRowNetworkEntity
import com.example.station.model.TimetableRow
import java.lang.IllegalArgumentException
import java.time.ZonedDateTime

/** Maps timetable row network data transfer object into domain model. */
fun TimetableRowNetworkEntity.toDomainObject(): TimetableRow {
    return TimetableRow(
        stationCode = this.stationCode,
        stationUicCode = this.stationUicCode,
        type = when (this.type) {
            "ARRIVAL" -> TimetableRow.Type.Arrival
            "DEPARTURE" -> TimetableRow.Type.Departure
            else -> throw IllegalArgumentException("Unknown type: ${this.type}")
        },
        track = this.track,
        scheduledTime = ZonedDateTime.parse(this.scheduledTime).toLocalDateTime()
    )
}