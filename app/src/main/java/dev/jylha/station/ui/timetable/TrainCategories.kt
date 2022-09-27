package dev.jylha.station.ui.timetable

import androidx.compose.runtime.Immutable
import dev.jylha.station.model.Train

/** An immutable wrapper for a set of train categories. */
@Immutable
data class TrainCategories(private val categories: Set<Train.Category>) :
    Set<Train.Category> by categories {

    constructor(vararg categories: Train.Category) : this(setOf(*categories))
}
