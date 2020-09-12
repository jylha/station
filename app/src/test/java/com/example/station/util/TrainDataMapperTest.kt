package com.example.station.util

import com.example.station.data.trains.network.TrainNetworkEntity
import com.example.station.model.Train
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class TrainDataMapperTest {

    private val entity = TrainNetworkEntity(0, "", "Commuter", false, emptyList())

    @Test fun `train number is mapped correctly into domain model`() {
        val dto = entity.copy(number = 123)
        val result = dto.toDomainModel()
        assertThat(result?.number).isEqualTo(123)
    }

    @Test fun `train type is mapped correctly into domain model`() {
        val dto = entity.copy(type = "IC")
        val result = dto.toDomainModel()
        assertThat(result?.type).isEqualTo("IC")
    }

    @Test fun `train category of 'Long-Distance' is mapped correctly into domain model`() {
        val dto = entity.copy(category = "Long-Distance")
        val result = dto.toDomainModel()
        assertThat(result?.category).isEqualTo(Train.Category.LongDistance)
    }

    @Test fun `train category of 'Commuter' is mapped correctly into domain model`() {
        val dto = entity.copy(category = "Commuter")
        val result = dto.toDomainModel()
        assertThat(result?.category).isEqualTo(Train.Category.Commuter)
    }

    @Test fun `mapping train with unsupported category return null`() {
        val dto = entity.copy(category = "Locomotive")
        val result = dto.toDomainModel()
        assertThat(result).isNull()
    }

    @Test fun `currently not running train is mapped correctly into domain model`() {
        val dto = entity.copy(runningCurrently = false)
        val result = dto.toDomainModel()
        assertThat(result?.isRunning).isFalse()
    }

    @Test fun `currently running train mapped correctly into domain model`() {
        val dto = entity.copy(runningCurrently = true)
        val result = dto.toDomainModel()
        assertThat(result?.isRunning).isTrue()
    }

    @Test fun `map list of entities into domain model`() {
        val entities = listOf(
            entity.copy(number = 1, category = "Long-distance"),
            entity.copy(number = 2, category = "Locomotive"),
            entity.copy(number = 3, category = "Commuter")
        )
        val result = entities.toDomainModel()
        assertThat(result).hasSize(2)
        assertThat(result.first().category).isEqualTo(Train.Category.LongDistance)
        assertThat(result.last().category).isEqualTo(Train.Category.Commuter)
    }
}
