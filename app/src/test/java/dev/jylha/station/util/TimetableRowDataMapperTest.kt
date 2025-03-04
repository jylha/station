package dev.jylha.station.util

import com.google.common.truth.Truth.assertThat
import dev.jylha.station.data.trains.network.CauseNetworkEntity
import dev.jylha.station.data.trains.network.TimetableRowNetworkEntity
import dev.jylha.station.model.TimetableRow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Test

class TimetableRowDataMapperTest {

    private val networkEntity = TimetableRowNetworkEntity(
        "ARRIVAL", "", 1, false, cancelled = false,
        scheduledTime = "2020-01-01T00:00:00Z"
    )

    @Test fun `stationCode is mapped correctly into domain model`() {
        val result = networkEntity.copy(stationCode = 123).toDomainModel()
        assertThat(result.stationCode).isEqualTo(123)
    }

    @Test fun `arrival type is mapped correctly into domain model`() {
        val result = networkEntity.copy(type = "ARRIVAL").toDomainModel()
        assertThat(result.type).isEqualTo(TimetableRow.Type.Arrival)
    }

    @Test fun `departure type is mapped correctly into domain model`() {
        val result = networkEntity.copy(type = "DEPARTURE").toDomainModel()
        assertThat(result.type).isEqualTo(TimetableRow.Type.Departure)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `mapping unknown type into domain model causes an exception`() {
        networkEntity.copy(type = "SOMETHING").toDomainModel()
    }

    @Test fun `trainStopping is correctly mapped into domain model`() {
        val result = networkEntity.copy(trainStopping = true).toDomainModel()
        assertThat(result.trainStopping).isTrue()
    }

    @Test fun `commercialStop is correctly mapped into domain model`() {
        val result = networkEntity.copy(commercialStop = true).toDomainModel()
        assertThat(result.commercialStop).isTrue()
    }

    @Test fun `track is mapped correctly into domain model`() {
        val result = networkEntity.copy(track = "12").toDomainModel()
        assertThat(result.track).isEqualTo("12")
    }

    @Test fun `cancelled is mapped correctly into domain model`() {
        val result = networkEntity.copy(cancelled = true).toDomainModel()
        assertThat(result.cancelled).isTrue()
    }

    @Test fun `scheduledTime is mapped correctly into domain model`() {
        val result = networkEntity.copy(scheduledTime = "2020-09-05T10:40:20.000Z")
            .toDomainModel()
        assertThat(result.scheduledTime).isEqualTo(
            LocalDateTime(
                2020, 9, 5,
                10, 40, 20
            ).toInstant(TimeZone.UTC)
        )
    }

    @Test fun `estimatedTime is mapped correctly into domain model`() {
        val result = networkEntity.copy(liveEstimateTime = "2020-09-05T10:40:15.000Z")
            .toDomainModel()
        assertThat(result.estimatedTime).isEqualTo(
            LocalDateTime(
                2020, 9, 5,
                10, 40, 15, 0
            ).toInstant(TimeZone.UTC)
        )
    }

    @Test fun `estimatedTime is set to null when not in network DTO`() {
        val result = networkEntity.copy(liveEstimateTime = null).toDomainModel()
        assertThat(result.estimatedTime).isNull()
    }

    @Test fun `actualTime is mapped correctly into domain model`() {
        val result = networkEntity.copy(
            actualTime = "2020-09-05T06:10:30.000Z"
        ).toDomainModel()
        assertThat(result.actualTime).isEqualTo(
            LocalDateTime(
                2020, 9, 5,
                6, 10, 30, 0
            ).toInstant(TimeZone.UTC)
        )
    }

    @Test fun `actualTime is set to null when not in network DTO`() {
        val result = networkEntity.copy(actualTime = null).toDomainModel()
        assertThat(result.actualTime).isNull()
    }

    @Test fun `differenceInMinutes is mapped correctly into domain model`() {
        val result = networkEntity.copy(differenceInMinutes = 5).toDomainModel()
        assertThat(result.differenceInMinutes).isEqualTo(5)
    }

    @Test fun `differenceInMinutes is set to zero when not in network DTO`() {
        val result = networkEntity.copy(differenceInMinutes = null).toDomainModel()
        assertThat(result.differenceInMinutes).isEqualTo(0)
    }

    @Test fun `cause is mapped correctly into domain model`() {
        val result = networkEntity.copy(
            causes = listOf(
                CauseNetworkEntity(
                    111, "AAA",
                    222, "BBB",
                    333, "CCC"
                )
            )
        ).toDomainModel()

        assertThat(result.causes).isNotNull()
        assertThat(result.causes.first().categoryId).isEqualTo(111)
        assertThat(result.causes.first().detailedCategoryId).isEqualTo(222)
        assertThat(result.causes.first().thirdLevelCategoryId).isEqualTo(333)
    }
}
