package com.example.station.model

import com.google.common.truth.Truth.assertThat
import java.time.ZonedDateTime
import org.junit.Test

class TrainTest {

    private val scheduledTime1 = ZonedDateTime.parse("2020-09-05T10:00:00.000Z")
    private val actualTime1 = ZonedDateTime.parse("2020-09-05T10:02:00.000Z")
    private val scheduledTime2 = ZonedDateTime.parse("2020-09-05T10:30:00.000Z")
    private val actualTime2 = ZonedDateTime.parse("2020-09-05T10:31:00.000Z")
    private val scheduledTime3 = ZonedDateTime.parse("2020-09-05T10:40:00.000Z")
    private val scheduledTime4 = ZonedDateTime.parse("2020-09-05T11:10:00.000Z")

    private val train = Train(
        1, "S", Train.Category.LongDistance, true, timetable = listOf(
            TimetableRow.departure(
                "A", 100, "5", scheduledTime1, actualTime = actualTime1,
                differenceInMinutes = 2, markedReady = true
            ),
            TimetableRow.arrival(
                "B", 200, "1", scheduledTime2, actualTime = actualTime2,
                differenceInMinutes = 1
            ),
            TimetableRow.departure("B", 200, "1", scheduledTime3),
            TimetableRow.arrival("C", 300, "3", scheduledTime4)
        )
    )

    private val trainWithEmptyTimetable = Train(
        2, "IC", Train.Category.Commuter, false, timetable = emptyList()
    )

    private val readyTrain = trainWithEmptyTimetable.copy(
        timetable = listOf(
            TimetableRow.departure(
                "1", 1, "1", scheduledTime1, markedReady = true
            ),
            TimetableRow.arrival("2", 2, "1", scheduledTime2)
        )
    )

    private val notReadyTrain = trainWithEmptyTimetable.copy(
        timetable = listOf(
            TimetableRow.departure("1", 1, "1", scheduledTime1),
            TimetableRow.arrival("2", 2, "1", scheduledTime2)
        )
    )

    private val trainWithSameEndpoints = trainWithEmptyTimetable.copy(
        timetable = listOf(
            TimetableRow.departure("A", 3, "4", scheduledTime1),
            TimetableRow.arrival("A", 3, "2", scheduledTime2)
        )
    )

    @Test fun `origin() returns the station code of the first timetable row`() {
        val result = train.origin()
        assertThat(result).isEqualTo(100)
    }

    @Test fun `origin() returns null for a train with empty timetable`() {
        val result = trainWithEmptyTimetable.origin()
        assertThat(result).isNull()
    }

    @Test fun `destination() returns the station code of the last timetable row`() {
        val result = train.destination()
        assertThat(result).isEqualTo(300)
    }

    @Test fun `destination() returns null for a train with empty timetable`() {
        val result = trainWithEmptyTimetable.destination()
        assertThat(result).isNull()
    }

    @Test fun `track() returns the track number for the given station`() {
        val result = train.track(200)
        assertThat(result).isEqualTo("1")
    }

    @Test fun `track() returns null for a station not in the timetable`() {
        val result = train.track(5)
        assertThat(result).isNull()
    }

    @Test fun `isReady() returns true when train is marked ready on origin`() {
        val result = readyTrain.isReady()
        assertThat(result).isTrue()
    }

    @Test fun `isReady() returns false when train is not marked ready on origin`() {
        val result = notReadyTrain.isReady()
        assertThat(result).isFalse()
    }

    @Test fun `isOrigin() returns true for a the first station`() {
        val result = train.isOrigin(100)
        assertThat(result).isTrue()
    }

    @Test fun `isOrigin() returns false for other than first station`() {
        val result = train.isOrigin(200)
        assertThat(result).isFalse()
    }

    @Test fun `isDestination() returns false for a midpoint station`() {
        val result = train.isDestination(200)
        assertThat(result).isFalse()
    }

    @Test fun `isDestination() returns true for the last station`() {
        val result = train.isDestination(300)
        assertThat(result).isTrue()
    }

    @Test fun `stops() returns the trains timetable rows as a list of stops`() {
        val result = train.stops()
        assertThat(result.size).isEqualTo(3)
        assertThat(result[0].stationUic()).isEqualTo(100)
        assertThat(result[1].stationUic()).isEqualTo(200)
        assertThat(result[2].stationUic()).isEqualTo(300)
    }

    @Test fun `stops() returns empty list for a train with empty timetable`() {
        val result = trainWithEmptyTimetable.stops()
        assertThat(result).isEmpty()
    }

    @Test fun `stops() returns separate stops when origin and destination are the same`() {
        val result = trainWithSameEndpoints.stops()
        assertThat(result).hasSize(2)
        assertThat(result.first().isOrigin()).isTrue()
        assertThat(result.last().isDestination()).isTrue()
    }

    @Test fun `stopsAt() for origin returns list of single stop`() {
        val result = train.stopsAt(100)
        assertThat(result).hasSize(1)
        assertThat(result.first().stationUic()).isEqualTo(100)
    }

    @Test fun `stopsAt() for destination return list of single stop`() {
        val result = train.stopsAt(300)
        assertThat(result).hasSize(1)
        assertThat(result.first().stationUic()).isEqualTo(300)
    }

    @Test fun `stopsAt() for a origin and destination returns separate stops`() {
        val result = trainWithSameEndpoints.stopsAt(3)
        assertThat(result).hasSize(2)
        assertThat(result.first().isOrigin()).isTrue()
        assertThat(result.last().isDestination()).isTrue()
    }

    @Test fun `stopsAt() for a station not in timetable returns empty list`() {
        val result = train.stopsAt(400)
        assertThat(result).isEmpty()
    }
}
