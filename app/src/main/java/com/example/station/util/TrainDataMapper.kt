package com.example.station.util

import com.example.station.data.trains.network.TrainNetworkEntity
import com.example.station.model.Train
import com.example.station.model.Train.Category.Commuter
import com.example.station.model.Train.Category.LongDistance


/**
 * Maps a list of train network data transfer objects into domain model at the same time
 * filtering any unnecessary entries.
 */
fun List<TrainNetworkEntity>.toDomainModel(): List<Train> {
    return mapNotNull { it.toDomainModel() }
}

fun TrainNetworkEntity.toDomainModel(): Train? {
    return try {
        Train(
            number = number,
            type = type,
            category = when {
                category.equals(LongDistance.name, ignoreCase = true) -> LongDistance
                category.equals(Commuter.name, ignoreCase = true) -> Commuter
                else -> throw IllegalArgumentException("Unknown category: $category")
            },
            isRunning = runningCurrently,
            timetable = timetable.map { row -> row.toDomainModel() }
        )
    } catch (e: IllegalArgumentException) {
        null
    }
}
