package com.example.station.model

import com.example.station.util.atLocalZone
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.ZonedDateTime

class TrainTest {

    private val dateTime1 = ZonedDateTime.parse("2020-09-05T10:00:00.000Z")
    private val dateTime2 = ZonedDateTime.parse("2020-09-05T10:30:00.000Z")
    private val dateTime3 = ZonedDateTime.parse("2020-09-05T10:40:00.000Z")
    private val dateTime4 = ZonedDateTime.parse("2020-09-05T11:10:00.000Z")

    private val train1 = Train(
        1, "S", Train.Category.LongDistance, true, timetable = listOf(
            TimetableRow(
                "A", 100, TimetableRow.Type.Departure, "5",
                dateTime1, actualTime = dateTime1, differenceInMinutes = 0
            ),
            TimetableRow(
                "B", 200, TimetableRow.Type.Arrival, "1",
                dateTime2, actualTime = dateTime2.plusMinutes(1), differenceInMinutes = 1
            ),
            TimetableRow("B", 200, TimetableRow.Type.Departure, "1", dateTime3),
            TimetableRow("C", 300, TimetableRow.Type.Arrival, "3", dateTime4)
        )
    )

    private val train2 = Train(2, "IC", Train.Category.Commuter, false, timetable = emptyList())

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
        assertThat(result).isEqualTo(dateTime2.atLocalZone())
    }

    @Test fun `scheduledArrivalAt() returns null for a station not in the timetable`() {
        val result = train1.scheduledArrivalAt(stationUicCode = 400)
        assertThat(result).isNull()
    }

    @Test fun `scheduledArrivalAt() return null for the origin station`() {
        val result = train1.scheduledArrivalAt(stationUicCode = 100)
        assertThat(result).isNull()
    }

    @Test fun `scheduledDepartureAt() returns the time of departure at a station`() {
        val result = train1.scheduledDepartureAt(100)
        assertThat(result).isEqualTo(dateTime1.atLocalZone())
    }

    @Test fun `scheduledDepartureAt() returns null for the destination station`() {
        val result = train1.scheduledDepartureAt(300)
        assertThat(result).isNull()
    }

    @Test fun `onRoute() return false for the origin station`() {
        val result = train1.onRouteTo(100)
        assertThat(result).isFalse()
    }

    @Test fun `onRoute() return false for a station that has actualTime for arrival`() {
        val result = train1.onRouteTo(200)
        assertThat(result).isFalse()
    }

    @Test fun `onRoute() returns true for a station without actualTime for arrival`() {
        val result = train1.onRouteTo(300)
        assertThat(result).isTrue()
    }

    @Test fun `onStation() returns false for a station it has left`() {
        val result = train1.onStation(100)
        assertThat(result).isFalse()
    }

    @Test fun `onStation() returns false for a station it is yet to arrive`() {
        val result = train1.onStation(300)
        assertThat(result).isFalse()
    }

    @Test fun `onStation() returns true for a station it has arrived byt not left`() {
        val result = train1.onStation(200)
        assertThat(result).isTrue()
    }

    @Test fun `hasLeft() returns true for a station it has departed`() {
        val result = train1.hasLeft(100)
        assertThat(result).isTrue()
    }

    @Test fun `hasLeft() returns false for a station it has no yet departed`() {
        val result = train1.hasLeft(200)
        assertThat(result).isFalse()
    }
}
