package com.example.station.util

import com.example.station.data.trains.network.TimetableRowNetworkEntity
import com.example.station.model.TimetableRow
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

class TimetableRowDataMapperTest {

    private val arrivalNetworkData = TimetableRowNetworkEntity(
        stationCode = "AAA",
        stationUicCode = 123,
        type = "ARRIVAL",
        track = "1",
        scheduledTime = "2020-09-05T10:40:00.000Z",
        actualTime = "2020-09-05T10:42:00.000Z",
        differenceInMinutes = 2
    )

    private val departureNetworkData = TimetableRowNetworkEntity(
        stationCode = "BBB",
        stationUicCode = 456,
        type = "DEPARTURE",
        track = "2",
        scheduledTime = "2020-09-05T10:45:00.000Z"
    )

    @Test fun `stationCode is mapped correctly into domain model`() {
        val result = arrivalNetworkData.toDomainObject()
        assertThat(result.stationCode).isEqualTo("AAA")
    }

    @Test fun `stationUicCode is mapped correctly into domain model`() {
        val result = arrivalNetworkData.toDomainObject()
        assertThat(result.stationUicCode).isEqualTo(123)
    }

    @Test fun `arrival type is mapped correctly into domain model`() {
        val result = arrivalNetworkData.toDomainObject()
        assertThat(result.type).isEqualTo(TimetableRow.Type.Arrival)
    }

    @Test fun `departure type is mapped correctly into domain model`() {
        val result = departureNetworkData.toDomainObject()
        assertThat(result.type).isEqualTo(TimetableRow.Type.Departure)
    }

    @Test fun `track is mapped correctly into domain model`() {
        val result = arrivalNetworkData.toDomainObject()
        assertThat(result.track).isEqualTo("1")
    }

    @Test fun `scheduledTime is mapped correctly into domain model`() {
        val result = arrivalNetworkData.toDomainObject()
        assertThat(result.scheduledTime).isEqualTo(
            ZonedDateTime.of(
                LocalDateTime.of(2020, 9, 5, 10, 40),
                ZoneOffset.UTC
            )
        )
    }

    @Test fun `actualTime is mapped correctly into domain model`() {
        val result = arrivalNetworkData.toDomainObject()
        assertThat(result.actualTime).isEqualTo(
            ZonedDateTime.of(
                LocalDateTime.of(2020, 9, 5, 10, 42),
                ZoneOffset.UTC
            )
        )
    }

    @Test fun `actualTime is set to null when not in network DTO`() {
        val result = departureNetworkData.toDomainObject()
        assertThat(result.actualTime).isNull()
    }

    @Test fun `differenceInMinutes is mapped correctly into domain model`() {
        val result = arrivalNetworkData.toDomainObject()
        assertThat(result.differenceInMinutes).isEqualTo(2)
    }

    @Test fun `differenceInMinutes is set to null when not in network DTO`() {
        val result = departureNetworkData.toDomainObject()
        assertThat(result.differenceInMinutes).isNull()
    }
}
