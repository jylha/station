package com.example.station.util

import com.example.station.data.trains.network.TrainNetworkEntity
import com.example.station.model.Train
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class TrainDataMapperTest {

    private val entity = TrainNetworkEntity(0, "", "", false, emptyList())

    @Test fun `map train network DTO into domain model`() {
        val dto = TrainNetworkEntity(5, "P", "", false, emptyList())
        val result = dto.toDomainObject()
        assertThat(result.number).isEqualTo(5)
        assertThat(result.type).isEqualTo("P")
        assertThat(result.timetable).isEmpty()
    }

    @Test fun `train category of 'Long-Distance' is mapped correctly into domain model`() {
        val dto = entity.copy(category = "Long-Distance")
        val result = dto.toDomainObject()
        assertThat(result.category).isEqualTo(Train.Category.LongDistance)
    }

    @Test fun `train category of 'Commuter' is mapped correctly into domain model`() {
        val dto = entity.copy(category = "Commuter")
        val result = dto.toDomainObject()
        assertThat(result.category).isEqualTo(Train.Category.Commuter)

    }

    @Test fun `currently not running train is mapped correctly into domain model`() {
        val dto = entity.copy(runningCurrently = false)
        val result = dto.toDomainObject()
        assertThat(result.isRunning).isFalse()
    }

    @Test fun `currently running train mapped correctly into domain model`() {
        val dto = entity.copy(runningCurrently = true)
        val result = dto.toDomainObject()
        assertThat(result.isRunning).isTrue()
    }
}
