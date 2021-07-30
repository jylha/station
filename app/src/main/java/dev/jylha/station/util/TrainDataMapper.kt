package dev.jylha.station.util

import dev.jylha.station.data.trains.network.TrainNetworkEntity
import dev.jylha.station.model.Train
import dev.jylha.station.model.Train.Category.Commuter
import dev.jylha.station.model.Train.Category.LongDistance
import java.time.LocalDate

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
            commuterLineId = commuterLineId,
            isRunning = runningCurrently,
            isCancelled = cancelled,
            version = version,
            departureDate = LocalDate.parse(departureDate),
            timetable = timetable.map { row -> row.toDomainModel() }
        )
    } catch (e: IllegalArgumentException) {
        null
    }
}
