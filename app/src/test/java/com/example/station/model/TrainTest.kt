package com.example.station.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class TrainTest {

    private val zoneId = ZoneId.systemDefault()

    private val dateTime1 = ZonedDateTime.of(
        LocalDateTime.of(2020, 9, 5, 10, 0), zoneId
    )
    private val dateTime2 = ZonedDateTime.of(
        LocalDateTime.of(2020, 9, 5, 10, 30), zoneId
    )
    private val dateTime3 = ZonedDateTime.of(
        LocalDateTime.of(2020, 9, 5, 10, 50), zoneId
    )

    private val train1 = Train(
        1, "S", timetable = listOf(
            TimetableRow("A", 100, TimetableRow.Type.Departure, "5", dateTime1),
            TimetableRow("B", 200, TimetableRow.Type.Arrival, "1", dateTime2),
            TimetableRow("B", 200, TimetableRow.Type.Departure, "1", dateTime2),
            TimetableRow("C", 300, TimetableRow.Type.Arrival, "3", dateTime3)
        )
    )

    private val train2 = Train(2, "IC", timetable = emptyList())

    @Test fun `origin() returns the station code of the first timetable row`() {
        val result = train1.origin()
        assertThat(result).isEqualTo("A")
    }

    @Test fun `origin() returns null for a train with empty timetable`() {
        val result = train2.origin()
        assertThat(result).isNull()
    }

    @Test fun `destination() returns the station code of the last timetable row`() {
        val result = train1.destination()
        assertThat(result).isEqualTo("C")
    }

    @Test fun `destination() returns null for a train with empty timetable`() {
        val result = train2.destination()
        assertThat(result).isNull()
    }

    @Test fun `track() returns the track number for the given station`() {
        val result = train1.track(200)
        assertThat(result).isEqualTo("1")
    }

    @Test fun `track() returns null for a station not in the timetable`() {
        val result = train1.track(5)
        assertThat(result).isNull()
    }

    @Test fun `scheduledArrivalAt() returns the time of arrival at given station`() {
        val result = train1.scheduledArrivalAt(stationUicCode = 200)
        assertThat(result).isEqualTo(
            dateTime2.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        )
    }

    @Test fun `scheduledArrivalAt() returns null for a station not in the timetable`() {
        val result = train1.scheduledArrivalAt(stationUicCode = 400)
        assertThat(result).isNull()
    }
}