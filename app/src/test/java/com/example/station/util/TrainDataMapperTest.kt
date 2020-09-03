package com.example.station.util

import com.example.station.data.timetable.network.TimetableRowNetworkEntity
import com.example.station.data.timetable.network.TrainNetworkEntity
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class TrainDataMapperTest {

    @Test
    fun `map timetable row network DTO into domain model`() {
        val dto = TimetableRowNetworkEntity(
            stationCode = "AAA", stationUicCode = 123, track = "5"
        )
        val result = dto.toDomainObject()
        assertThat(result.stationCode).isEqualTo("AAA")
        assertThat(result.stationUicCode).isEqualTo(123)
        assertThat(result.track).isEqualTo("5")
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