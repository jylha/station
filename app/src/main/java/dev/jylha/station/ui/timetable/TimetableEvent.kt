package dev.jylha.station.ui.timetable

import dev.jylha.station.model.Station
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train

/** Events from TimetableScreen. */
sealed class TimetableEvent {
    data class LoadTimetable(val stationCode: Int) : TimetableEvent()
    data class SelectCategories(val categories: Set<Train.Category>) : TimetableEvent()
    data class SelectTimetableTypes(val types: Set<TimetableRow.Type>) : TimetableEvent()
    data class ReloadTimetable(val station: Station): TimetableEvent()
}
