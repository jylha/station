package com.example.station.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime

class TrainTest {

    private val dateTime1 = LocalDateTime.of(2020, 9, 5, 10, 0)
    private val dateTime2 = LocalDateTime.of(2020, 9, 5, 10, 30)
    private val dateTime3 = LocalDateTime.of(2020, 9, 5, 10, 50)
    private val train1 = Train(
        1, "S", timetable = listOf(
            TimetableRow("A", 100, "5", dateTime1),
            TimetableRow("B", 200, "1", dateTime2),
            TimetableRow("C", 300, "3", dateTime3)
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
        assertThat(result).isEqualTo(dateTime2)
    }

    @Test fun `scheduledArrivalAt() returns null for a station not in the timetable`() {
        val result = train1.scheduledArrivalAt(stationUicCode = 400)
        assertThat(result).isNull()
    }
}