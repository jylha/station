package com.example.station.ui.timetable

import com.example.station.model.Station
import com.example.station.model.TimetableRow
import com.example.station.model.Train

/** Events from TimetableScreen. */
sealed class TimetableEvent {
    data class LoadTimetable(val station: Station) : TimetableEvent()
    data class SelectCategories(val categories: Set<Train.Category>) : TimetableEvent()
    data class SelectTimetableTypes(val types: Set<TimetableRow.Type>) : TimetableEvent()
    data class ReloadTimetable(val station: Station): TimetableEvent()
}
