package com.example.station.util

import com.example.station.data.trains.network.TrainNetworkEntity
import com.example.station.model.Train
import com.example.station.model.Train.Category.Commuter
import com.example.station.model.Train.Category.LongDistance


/**
 * Maps a list of train network data transfer object into domain model at the same time
 * filtering any unnecessary entries.
 */
fun List<TrainNetworkEntity>.toDomainModel(): List<Train> {
    return mapNotNull { it.toDomainModel() }
}

fun TrainNetworkEntity.toDomainModel(): Train? {
    return try {
        Train(
            number = this.number,
            type = this.type,
            category = when {
                this.category.equals("Long-distance", ignoreCase = true) -> LongDistance
                this.category.equals("Commuter", ignoreCase = true) -> Commuter
                else -> throw IllegalArgumentException()
            },
            isRunning = this.runningCurrently,
            timetable = this.timetable.map { row -> row.toDomainObject() }
        )
    } catch (e: IllegalArgumentException) {
        null
    }
}
