package dev.jylha.station.ui.timetable

import androidx.compose.runtime.Immutable
import dev.jylha.station.model.Train

/** An immutable wrapper for a list of trains. */
@Immutable
data class Trains(private val trains: List<Train>) : List<Train> by trains {
    constructor(vararg trains: Train) : this(listOf(*trains))
}
