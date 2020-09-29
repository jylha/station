package com.example.station.model

import androidx.compose.runtime.Immutable
import java.time.ZonedDateTime

/**
 * Domain model for train's timetable row data.
 *
 * @param type Time table entry type (either Arrival or Departure).
 * @param stationShortCode The short code for the station.
 * @param stationUic The UIC code for the station.
 * @param trainStopping Whether train is stopping on the station.
 * @param commercialStop Whether the stop is commercial, or null if train is no stopping..
 * @param track Track number.
 * @param scheduledTime Scheduled time for train's arrival or departure.
 * @param estimatedTime Estimated time for train's arrival or departure.
 * @param actualTime Actual time of of arrival or departure.
 * @param differenceInMinutes Difference between scheduled and actual time in minutes.
 * @param markedReady Train is marked ready to depart (used only on origin station).
 */
@Immutable
data class TimetableRow(
    val type: Type,
    val stationShortCode: String,
    val stationUic: Int,
    val trainStopping: Boolean = true,
    val commercialStop: Boolean? = null,
    val track: String? = null,
    val scheduledTime: ZonedDateTime,
    val estimatedTime: ZonedDateTime? = null,
    val actualTime: ZonedDateTime? = null,
    val differenceInMinutes: Int? = null,
    val markedReady: Boolean = false
) {
    sealed class Type {
        object Arrival : Type()
        object Departure : Type()
    }

    companion object {

        /** Creates TimetableRow of commercial stop with type Arrival. */
        fun arrival(
            stationShortCode: String,
            stationUic: Int,
            track: String,
            scheduledTime: ZonedDateTime,
            estimatedTime: ZonedDateTime? = null,
            actualTime: ZonedDateTime? = null,
            differenceInMinutes: Int? = null,
            trainStopping: Boolean = true,
            commercialStop: Boolean? = true
        ): TimetableRow =
            TimetableRow(
                Type.Arrival,
                stationShortCode,
                stationUic,
                trainStopping,
                commercialStop,
                track,
                scheduledTime,
                estimatedTime,
                actualTime,
                differenceInMinutes,
                markedReady = false
            )

        /** Creates TimetableRow of commercial stop with type Departure. */
        fun departure(
            stationShortCode: String,
            stationUic: Int,
            track: String,
            scheduledTime: ZonedDateTime,
            estimatedTime: ZonedDateTime? = null,
            actualTime: ZonedDateTime? = null,
            differenceInMinutes: Int? = null,
            markedReady: Boolean = false,
            trainStopping: Boolean = true,
            commercialStop: Boolean? = true
        ): TimetableRow =
            TimetableRow(
                Type.Departure,
                stationShortCode,
                stationUic,
                trainStopping,
                commercialStop ,
                track,
                scheduledTime,
                estimatedTime,
                actualTime,
                differenceInMinutes,
                markedReady
            )
    }
}
