package com.example.station.model

import java.time.ZonedDateTime

/**
 * Domain model for train's timetable row data.
 *
 * @param type Time table entry type (either Arrival or Departure).
 * @param stationCode The short code for the station.
 * @param stationUicCode The UIC code for the station.
 * @param trainStopping Whether train is stopping on the station.
 * @param commercialStop Whether the stop is commercial.
 * @param track Track number.
 * @param scheduledTime The scheduled time for train's arrival or departure.
 * @param actualTime Actual time of of arrival or departure.
 * @param differenceInMinutes Difference between scheduled and actual time in minutes.
 * @param markedReady Train is marked ready to depart (used only on origin station).
 */
data class TimetableRow(
    val type: Type,
    val stationCode: String,
    val stationUicCode: Int,
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
            stationCode: String,
            stationUicCode: Int,
            track: String,
            scheduledTime: ZonedDateTime,
            estimatedTime: ZonedDateTime? = null,
            actualTime: ZonedDateTime? = null,
            differenceInMinutes: Int? = null
        ): TimetableRow =
            TimetableRow(
                Type.Arrival,
                stationCode,
                stationUicCode,
                trainStopping  = true,
                commercialStop = true,
                track,
                scheduledTime,
                estimatedTime,
                actualTime,
                differenceInMinutes,
                markedReady = false
            )

        /** Creates TimetableRow of commercial stop with type Departure. */
        fun departure(
            stationCode: String,
            stationUicCode: Int,
            track: String,
            scheduledTime: ZonedDateTime,
            estimatedTime: ZonedDateTime? = null,
            actualTime: ZonedDateTime? = null,
            differenceInMinutes: Int? = null,
            markedReady: Boolean = false
        ): TimetableRow =
            TimetableRow(
                Type.Departure,
                stationCode,
                stationUicCode,
                trainStopping = true,
                commercialStop = true,
                track,
                scheduledTime,
                estimatedTime,
                actualTime,
                differenceInMinutes,
                markedReady
            )
    }
}
