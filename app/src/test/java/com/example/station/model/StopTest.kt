package com.example.station.model

import com.google.common.truth.Truth.assertThat
import java.time.ZonedDateTime
import org.junit.Test

class StopTest {

    private val arrival = arrival(
        1, "1", ZonedDateTime.parse("2020-10-10T10:10:00.000Z")
    )

    private val departure = arrival.copy(type = TimetableRow.Type.Departure)

    @Test(expected = IllegalArgumentException::class)
    fun `Creating Stop with two arrival timetable rows throws an exception`() {
        Stop(arrival, arrival)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Creating Stop with two departure timetable rows of throws an exception`() {
        Stop(departure, departure)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Creating Stop with arrival set with departure timetable row throws an exception`() {
        Stop(arrival = departure)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Creating Stop with departure set with arrival timetable row throws an exception`() {
        Stop(departure = arrival)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Creating Stop with different stationUic in arrival and departure throws an exception`() {
        Stop(arrival, departure.copy(stationUic = 5))
    }

    @Test fun `Creating Stop with only arrival succeeds`() {
        val stop = Stop(arrival)
        assertThat(stop.arrival).isEqualTo(arrival)
        assertThat(stop.departure).isNull()
    }

    @Test fun `Creating Stop with only departure succeeds`() {
        val stop = Stop(departure = departure)
        assertThat(stop.arrival).isNull()
        assertThat(stop.departure).isEqualTo(departure)
    }

    @Test fun `Creating Stop with arrival and departure succeeds`() {
        val stop = Stop(arrival, departure)
        assertThat(stop.arrival).isEqualTo(arrival)
        assertThat(stop.departure).isEqualTo(departure)
    }

    @Test fun `isOrigin() returns false for a stop with only arrival`() {
        val stop = Stop(arrival)
        val result = stop.isOrigin()
        assertThat(result).isFalse()
    }

    @Test fun `isOrigin() returns true for a stop with only departure`() {
        val stop = Stop(departure = departure)
        val result = stop.isOrigin()
        assertThat(result).isTrue()
    }

    @Test fun `isOrigin() returns false for a stop with arrival and departure`() {
        val stop = Stop(arrival, departure)
        val result = stop.isOrigin()
        assertThat(result).isFalse()
    }

    @Test fun `isDestination() returns true for a stop with only arrival`() {
        val stop = Stop(arrival)
        val result = stop.isDestination()
        assertThat(result).isTrue()
    }

    @Test fun `isDestination() returns false for a stop with only departure`() {
        val stop = Stop(departure = departure)
        val result = stop.isDestination()
        assertThat(result).isFalse()
    }

    @Test fun `isDestination() returns false for a stop with arrival and departure`() {
        val stop = Stop(arrival, departure)
        val result = stop.isDestination()
        assertThat(result).isFalse()
    }

    @Test fun `isWaypoint() returns false for a stop with only arrival`() {
        val stop = Stop(arrival)
        val result = stop.isWaypoint()
        assertThat(result).isFalse()
    }

    @Test fun `isWaypoint() returns false for a stop with only departure`() {
        val stop = Stop(departure = departure)
        val result = stop.isWaypoint()
        assertThat(result).isFalse()
    }

    @Test fun `isWaypoint() returns true for a stop with arrival and departure`() {
        val stop = Stop(arrival, departure)
        val result = stop.isWaypoint()
        assertThat(result).isTrue()
    }

    @Test fun `stationUic() returns the station uic of stops arrival`() {
        val stop = Stop(arrival, departure)
        val result = stop.stationUic()
        assertThat(result).isEqualTo(1)
    }

    @Test fun `stationUic() returns the station uic of stops departure if arrival is not set`() {
        val stop = Stop(departure = departure.copy(stationUic = 5))
        val result = stop.stationUic()
        assertThat(result).isEqualTo(5)
    }

    @Test fun `track() returns the arrival track`() {
        val stop = Stop(arrival.copy(track = "foo"), departure)
        val result = stop.track()
        assertThat(result).isEqualTo("foo")
    }

    @Test fun `track() returns the departure track when arrival is not set`() {
        val stop = Stop(departure = departure.copy(track = "bar"))
        val result = stop.track()
        assertThat(result).isEqualTo("bar")
    }

    private val time1 = ZonedDateTime.parse("2020-01-01T08:30:00.000Z")
    private val time2 = ZonedDateTime.parse("2020-01-01T08:35:00.000Z")
    private val time3 = ZonedDateTime.parse("2020-01-01T09:00:00.000Z")
    private val time4 = ZonedDateTime.parse("2020-01-01T09:01:00.000Z")

    @Test
    fun `timeOfNextEvent() returns scheduled time of arrival when no actualTime or departure`() {
        val stop = Stop(arrival.copy(scheduledTime = time1, actualTime = null))
        val result = stop.timeOfNextEvent()
        assertThat(result).isEqualTo(time1)
    }

    @Test fun `timeOfNextEvent() returns actualTime of arrival when no departure`() {
        val stop = Stop(arrival.copy(scheduledTime = time1, actualTime = time2))
        val result = stop.timeOfNextEvent()
        assertThat(result).isEqualTo(time2)
    }

    @Test fun `timeOfNextEvent() returns scheduledTime of arrival when no actual time`() {
        val stop = Stop(arrival.copy(scheduledTime = time1), departure.copy(scheduledTime = time3))
        val result = stop.timeOfNextEvent()
        assertThat(result).isEqualTo(time1)
    }

    @Test
    fun `timeOfNextEvent() returns scheduledTime of departure when no actualTime or arrival`() {
        val stop = Stop(arrival = null, departure.copy(scheduledTime = time1))
        val result = stop.timeOfNextEvent()
        assertThat(result).isEqualTo(time1)
    }

    @Test fun `timeOfNextEvent() returns actualTime of departure when no arrival`() {
        val stop = Stop(arrival = null, departure.copy(scheduledTime = time1, actualTime = time2))
        val result = stop.timeOfNextEvent()
        assertThat(result).isEqualTo(time2)
    }

    @Test fun `timeOfNextEvent() returns actualTime of departure when it is set`() {
        val stop = Stop(
            arrival.copy(scheduledTime = time1, actualTime = time2),
            departure.copy(scheduledTime = time3, actualTime = time4)
        )
        val result = stop.timeOfNextEvent()
        assertThat(result).isEqualTo(time4)
    }

    @Test fun `isReached() returns false when actualTime of arrival is not set `() {
        val stop = Stop(arrival.copy(scheduledTime = time1, actualTime = null))
        val result = stop.isReached()
        assertThat(result).isFalse()
    }

    @Test fun `isReached() returns true when actualTime of arrival is set`() {
        val stop = Stop(arrival.copy(scheduledTime = time1, actualTime = time2))
        val result = stop.isReached()
        assertThat(result).isTrue()
    }

    @Test fun `isReached() return true when arrival is not set()`() {
        val stop = Stop(departure = departure.copy(scheduledTime = time3))
        val result = stop.isReached()
        assertThat(result).isTrue()
    }

    @Test fun `isNotReached() returns true when actualTime of arrival is not set`() {
        val stop = Stop(arrival.copy(scheduledTime = time1, actualTime = null))
        val result = stop.isNotReached()
        assertThat(result).isTrue()
    }

    @Test fun `isDeparted() returns true when actualTime of departure is set`() {
        val stop = Stop(departure = departure.copy(scheduledTime = time1, actualTime = time2))
        val result = stop.isDeparted()
        assertThat(result).isTrue()
    }

    @Test fun `isDeparted() returns true when actualTime of departure is not set`() {
        val stop = Stop(departure = departure.copy(scheduledTime = time1, actualTime = null))
        val result = stop.isDeparted()
        assertThat(result).isFalse()
    }

    @Test fun `isNotDeparted() returns true when actualTime of departure is not set`() {
        val stop = Stop(departure = departure.copy(scheduledTime = time1))
        val result = stop.isNotDeparted()
        assertThat(result).isTrue()
    }

    @Test fun `isNotDeparted() returns false when actualTime of departure is set`() {
        val stop = Stop(departure = departure.copy(scheduledTime = time1, actualTime = time2))
        val result = stop.isNotDeparted()
        assertThat(result).isFalse()
    }
}
