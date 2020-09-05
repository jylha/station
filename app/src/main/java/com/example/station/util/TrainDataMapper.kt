package com.example.station.util

import com.example.station.data.timetable.network.TimetableRowNetworkEntity
import com.example.station.data.timetable.network.TrainNetworkEntity
import com.example.station.model.TimetableRow
import com.example.station.model.Train
import java.time.ZonedDateTime

/** Maps train network data transfer object into domain model. */
fun TrainNetworkEntity.toDomainObject(): Train {
    return Train(
        number = this.number,
        type = this.type,
        timetable = this.timetable.map { row -> row.toDomainObject() }
    )
}

/** Maps timetable row network data transfer object into domain model. */
fun TimetableRowNetworkEntity.toDomainObject(): TimetableRow {
    return TimetableRow(
        stationCode = this.stationCode,
        stationUicCode = this.stationUicCode,
        track = this.track,
        scheduledTime = ZonedDateTime.parse(this.scheduledTime).toLocalDateTime()
    )
}