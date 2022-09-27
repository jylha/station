package dev.jylha.station.ui.timetable

import androidx.compose.runtime.Immutable
import dev.jylha.station.model.TimetableRow

/** An immutable wrapper for a set of timetable row types. */
@Immutable
data class TimetableTypes(private val types: Set<TimetableRow.Type>) :
    Set<TimetableRow.Type> by types {

    constructor(vararg types: TimetableRow.Type) : this(setOf(*types))
}
