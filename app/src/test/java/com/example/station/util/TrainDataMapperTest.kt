package com.example.station.util

import com.example.station.data.timetable.network.TrainNetworkEntity
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class TrainDataMapperTest {

    @Test
    fun `map train network DTO into domain model`() {
        val dto = TrainNetworkEntity(5, "P")
        val result = dto.toDomainObject()
        assertThat(result.number).isEqualTo(5)
        assertThat(result.type).isEqualTo("P")
    }
}