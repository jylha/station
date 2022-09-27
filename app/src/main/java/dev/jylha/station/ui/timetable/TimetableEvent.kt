package dev.jylha.station.ui.timetable

import dev.jylha.station.model.Station

/** Events from TimetableScreen. */
sealed class TimetableEvent {
    data class LoadTimetable(val stationCode: Int) : TimetableEvent()
    data class SelectCategories(val categories: TrainCategories) : TimetableEvent()
    data class SelectTimetableTypes(val types: TimetableTypes) : TimetableEvent()
    data class ReloadTimetable(val station: Station): TimetableEvent()
}
