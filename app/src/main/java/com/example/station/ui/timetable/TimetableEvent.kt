package com.example.station.ui.timetable

import com.example.station.model.Station
import com.example.station.model.Train

/** Events from TimetableScreen. */
sealed class TimetableEvent {
    data class LoadTimetable(val station: Station) : TimetableEvent()
    data class SelectCategories(val categories: Set<Train.Category>) : TimetableEvent()
    data class ReloadTimetable(val station: Station): TimetableEvent()
}
