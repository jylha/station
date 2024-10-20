package dev.jylha.station.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.Instant

/**
 * Domain model for train's timetable row data.
 *
 * @param type Time table entry type (either Arrival or Departure).
 * @param stationCode The UIC code for the station.
 * @param trainStopping Whether train is stopping on the station.
 * @param commercialStop Whether the stop is commercial, or null if train is no stopping..
 * @param track Track number.
 * @param cancelled Whether the train's arrival or departure is cancelled.
 * @param scheduledTime Scheduled time for train's arrival or departure.
 * @param estimatedTime Estimated time for train's arrival or departure.
 * @param actualTime Actual time of of arrival or departure.
 * @param differenceInMinutes Difference between scheduled and actual time in minutes.
 * @param markedReady Train is marked ready to depart (used only on origin station).
 * @param causes List of causes for train being behind or ahead of schedule.
 */
@Immutable
data class TimetableRow(
    val type: Type,
    val stationCode: Int,
    val trainStopping: Boolean = true,
    val commercialStop: Boolean? = null,
    val track: String? = null,
    val cancelled: Boolean = false,
    val scheduledTime: Instant,
    val estimatedTime: Instant? = null,
    val actualTime: Instant? = null,
    val differenceInMinutes: Int = 0,
    val markedReady: Boolean = false,
    val causes: List<DelayCause> = emptyList(),
) {
    sealed class Type(val name: String) {
        object Arrival : Type("Arrival")
        object Departure : Type("Departure")

        override fun toString(): String = name
    }
}

/** Creates TimetableRow of commercial stop with type Arrival. */
internal fun arrival(
    stationCode: Int,
    track: String,
    scheduledTime: Instant,
    estimatedTime: Instant? = null,
    actualTime: Instant? = null,
    differenceInMinutes: Int = 0,
    trainStopping: Boolean = true,
    commercialStop: Boolean? = true,
    causes: List<DelayCause> = emptyList(),
    cancelled: Boolean = false,
): TimetableRow =
    TimetableRow(
        TimetableRow.Type.Arrival,
        stationCode,
        trainStopping,
        commercialStop,
        track,
        cancelled,
        scheduledTime,
        estimatedTime,
        actualTime,
        differenceInMinutes,
        markedReady = false,
        causes,
    )

/** Creates TimetableRow of commercial stop with type Departure. */
internal fun departure(
    stationCode: Int,
    track: String,
    scheduledTime: Instant,
    estimatedTime: Instant? = null,
    actualTime: Instant? = null,
    differenceInMinutes: Int = 0,
    markedReady: Boolean = false,
    trainStopping: Boolean = true,
    commercialStop: Boolean? = true,
    causes: List<DelayCause> = emptyList(),
    cancelled: Boolean = false,
): TimetableRow =
    TimetableRow(
        TimetableRow.Type.Departure,
        stationCode,
        trainStopping,
        commercialStop,
        track,
        cancelled,
        scheduledTime,
        estimatedTime,
        actualTime,
        differenceInMinutes,
        markedReady,
        causes,
    )
