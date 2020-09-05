package com.example.station.util

import com.example.station.data.timetable.network.TimetableRowNetworkEntity
import com.example.station.data.timetable.network.TrainNetworkEntity
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime


class TrainDataMapperTest {

    @Test
    fun `map timetable row network DTO into domain model`() {
        val dto = TimetableRowNetworkEntity(
            stationCode = "AAA", stationUicCode = 123, track = "5", scheduledTime = "2020-09-05T10:40:00.000Z"
        )
        val result = dto.toDomainObject()
        assertThat(result.stationCode).isEqualTo("AAA")
        assertThat(result.stationUicCode).isEqualTo(123)
        assertThat(result.track).isEqualTo("5")
        assertThat(result.scheduledTime).isEqualTo(LocalDateTime.of(2020, 9, 5, 10 , 40))
    }

    @Test
    fun `map train network DTO into domain model`() {
        val dto = TrainNetworkEntity(5, "P", emptyList())
        val result = dto.toDomainObject()
        assertThat(result.number).isEqualTo(5)
        assertThat(result.type).isEqualTo("P")
        assertThat(result.timetable).isEmpty()
    }
}