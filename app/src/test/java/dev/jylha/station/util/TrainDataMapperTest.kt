package dev.jylha.station.util

import com.google.common.truth.Truth.assertThat
import dev.jylha.station.data.trains.network.TrainNetworkEntity
import dev.jylha.station.model.Train
import org.junit.Test


class TrainDataMapperTest {

    private val entity = TrainNetworkEntity(
        number = 0,
        departureDate = "2020-05-20",
        type = "",
        category = "Commuter",
        commuterLineId = null,
        runningCurrently = false,
        cancelled = false,
        version = 0,
        timetable = emptyList()
    )

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

    @Test fun `mapping train with unsupported category returns null`() {
        val dto = entity.copy(category = "Locomotive")
        val result = dto.toDomainModel()
        assertThat(result).isNull()
    }

    @Test fun `train commuterLineId is correctly mapped into domain model`() {
        val dto = entity.copy(commuterLineId = "F")
        val result = dto.toDomainModel()
        assertThat(result?.commuterLineId).isEqualTo("F")
    }

    @Test fun `train commuterLineId is set to null when not in network DTO`() {
        val dto = entity.copy(commuterLineId = null)
        val result = dto.toDomainModel()
        assertThat(result?.commuterLineId).isNull()
    }

    @Test fun `currently not running train is mapped correctly into domain model`() {
        val dto = entity.copy(runningCurrently = false)
        val result = dto.toDomainModel()
        assertThat(result?.isRunning).isFalse()
    }

    @Test fun `currently running train is mapped correctly into domain model`() {
        val dto = entity.copy(runningCurrently = true)
        val result = dto.toDomainModel()
        assertThat(result?.isRunning).isTrue()
    }

    @Test fun `cancelled is mapped correctly into domain model`() {
        val dto = entity.copy(cancelled = true)
        val result = dto.toDomainModel()
        assertThat(result?.isCancelled).isTrue()
    }

    @Test fun `train departure date is mapped correctly into domain model`() {
        val dto = entity.copy(departureDate = "2020-02-20")
        val result = dto.toDomainModel()
        assertThat(result?.departureDate?.year).isEqualTo(2020)
        assertThat(result?.departureDate?.monthNumber).isEqualTo(2)
        assertThat(result?.departureDate?.dayOfMonth).isEqualTo(20)
    }

    @Test fun `version is mapped correctly into domain model`() {
        val dto = entity.copy(version = 555)
        val result = dto.toDomainModel()
        assertThat(result?.version).isEqualTo(555)
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
