package com.example.station.util

import com.example.station.data.timetable.network.TrainNetworkEntity
import com.example.station.model.Train

/** Maps train network data transfer object into domain model. */
fun TrainNetworkEntity.toDomainObject(): Train {
    return Train(
        number = this.number
    )
}