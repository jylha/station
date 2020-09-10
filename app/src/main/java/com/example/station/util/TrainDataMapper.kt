package com.example.station.util

import com.example.station.data.trains.network.TrainNetworkEntity
import com.example.station.model.Train
import java.util.Locale

/** Maps train network data transfer object into domain model. */
fun TrainNetworkEntity.toDomainObject(): Train {
    return Train(
        number = this.number,
        type = this.type,
        category = when (this.category.toLowerCase(Locale.ROOT)) {
            "long-distance" -> Train.Category.LongDistance
            "commuter" -> Train.Category.Commuter
            else -> Train.Category.Other
        },
        isRunning = this.runningCurrently,
        timetable = this.timetable.map { row -> row.toDomainObject() }
    )
}
