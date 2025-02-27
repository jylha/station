package dev.jylha.station.model

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Instant
import org.junit.Test

class StopTest {

    private val arrival = arrival(
        1, "1", Instant.parse("2020-10-10T10:10:00Z")
    )

    private val departure = arrival.copy(type = TimetableRow.Type.Departure)

    @Test(expected = IllegalArgumentException::class)
    fun `Creating a Stop with two arrivals throws an exception`() {
        Stop(arrival, arrival)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Creating a Stop with two departures throws an exception`() {
        Stop(departure, departure)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Creating a Stop with a departure set as the arrival throws an exception`() {
        Stop(arrival = departure)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Creating a Stop with an arrival set as the departure throws an exception`() {
        Stop(departure = arrival)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Creating a Stop with different stationCode in arrival and departure throws an exception`() {
        Stop(arrival, departure.copy(stationCode = 5))
    }

    @Test fun `Creating a Stop by only setting the arrival succeeds`() {
        val result = Stop(arrival)
        assertThat(result.arrival).isEqualTo(arrival)
        assertThat(result.departure).isNull()
    }

    @Test fun `Creating a Stop by only setting the departure succeeds`() {
        val result = Stop(departure = departure)
        assertThat(result.arrival).isNull()
        assertThat(result.departure).isEqualTo(departure)
    }

    @Test fun `Creating a Stop with arrival and departure succeeds`() {
        val result = Stop(arrival, departure)
        assertThat(result.arrival).isEqualTo(arrival)
        assertThat(result.departure).isEqualTo(departure)
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

    @Test fun `stationCode() returns the station code of waypoint stop`() {
        val waypoint = Stop(arrival, departure)
        val result = waypoint.stationCode()
        assertThat(result).isEqualTo(1)
    }

    @Test fun `stationCode() returns the station code of origin stop`() {
        val origin = Stop(departure = departure.copy(stationCode = 5))
        val result = origin.stationCode()
        assertThat(result).isEqualTo(5)
    }

    @Test fun `stationCode() returns the station code of destination stop`() {
        val destination = Stop(arrival = arrival.copy(stationCode = 123))
        val result = destination.stationCode()
        assertThat(result).isEqualTo(123)
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

    private val time1 = Instant.parse("2020-01-01T08:30:00Z")
    private val time2 = Instant.parse("2020-01-01T08:35:00Z")
    private val time3 = Instant.parse("2020-01-01T09:00:00Z")
    private val time4 = Instant.parse("2020-01-01T09:01:00Z")

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

    @Test fun `isDeparted() returns false when actualTime of departure is not set`() {
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

    @Test fun `arrivalAfter() returns false for a origin station`() {
        val stop = Stop(departure = departure.copy(scheduledTime = time1, actualTime = time2))
        val result = stop.arrivalAfter(time1)
        assertThat(result).isFalse()
    }

    @Test fun `arrivalAfter() returns false when actual time of arrival is before given time`() {
        val stop = Stop(arrival = arrival.copy(scheduledTime = time1, actualTime = time1))
        val result = stop.arrivalAfter(time2)
        assertThat(result).isFalse()
    }

    @Test fun `arrivalAfter() returns true when actual time of arrival is after given time`() {
        val stop = Stop(arrival = arrival.copy(scheduledTime = time1, actualTime = time2))
        val result = stop.arrivalAfter(time1)
        assertThat(result).isTrue()
    }

    @Test fun `arrivalAfter() returns true when actual time of arrival is not set`() {
        val stop = Stop(arrival = arrival.copy(scheduledTime = time1))
        val result = stop.arrivalAfter(time2)
        assertThat(result).isTrue()
    }

    @Test fun `departureAfter()  returns false for a destination station`() {
        val stop = Stop(arrival = arrival.copy(scheduledTime = time1, actualTime = time2))
        val result = stop.departureAfter(time1)
        assertThat(result).isFalse()
    }

    @Test
    fun `departureAfter() returns false when actual time of departure is before given time`() {
        val stop = Stop(departure = departure.copy(scheduledTime = time1, actualTime = time1))
        val result = stop.departureAfter(time2)
        assertThat(result).isFalse()
    }

    @Test fun `departureAfter() returns true when actual time of departure is after given time`() {
        val stop = Stop(departure = departure.copy(scheduledTime = time1, actualTime = time2))
        val result = stop.departureAfter(time1)
        assertThat(result).isTrue()
    }

    @Test fun `departureAfter() returns true when actual time of departure is not set`() {
        val stop = Stop(departure = departure.copy(scheduledTime = time1))
        val result = stop.departureAfter(time2)
        assertThat(result).isTrue()
    }
}
