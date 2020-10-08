package com.example.station.util

import com.example.station.data.trains.network.CauseCategoryNetworkEntity
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class CauseMapperTest {

    private val causeCategoryNetworkEntity = CauseCategoryNetworkEntity(
        id =1,
        categoryCode = "A1",
        categoryName = "Some category",
        validFrom = "2020-10-10T08:00:00.000Z",
        validTo = null,
        passengerTerm = null
    )

    @Test fun `map cause category correctly into domain model`() {
        val result = causeCategoryNetworkEntity.toDomainModel()
        assertThat(result.id).isEqualTo(1)
        assertThat(result.name).isEqualTo("Some category")
    }
}
