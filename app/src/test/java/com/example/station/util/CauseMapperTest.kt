package com.example.station.util

import com.example.station.data.trains.network.CauseCategoryNetworkEntity
import com.example.station.data.trains.network.DetailedCauseCategoryNetworkEntity
import com.example.station.data.trains.network.PassengerTermNetworkEntity
import com.example.station.data.trains.network.ThirdLevelCauseCategoryNetworkEntity
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class CauseMapperTest {

    private val causeCategoryNetworkEntity = CauseCategoryNetworkEntity(
        id =1,
        categoryCode = "A1",
        categoryName = "Some category",
        validFrom = "2020-10-10T08:00Z",
        validTo = null,
        passengerTerm = PassengerTermNetworkEntity(
            fi = "A - fi",
            en = "A - en",
            sv = "A - sv"
        )
    )

    @Test fun `map cause category correctly into domain model`() {
        val result = causeCategoryNetworkEntity.toDomainModel()
        assertThat(result.id).isEqualTo(1)
        assertThat(result.name).isEqualTo("Some category")
        assertThat(result.passengerFriendlyName?.fi).isEqualTo("A - fi")
        assertThat(result.passengerFriendlyName?.en).isEqualTo("A - en")
        assertThat(result.passengerFriendlyName?.sv).isEqualTo("A - sv")
    }

    private val detailedCauseCategoryNetworkEntity = DetailedCauseCategoryNetworkEntity(
        id = 2,
        detailedCategoryCode = "B2",
        detailedCategoryName = "Category name for B2",
        validFrom = "2020-10-10T00:30Z",
        validTo = null,
        passengerTerm = PassengerTermNetworkEntity(
            fi = "B - fi",
            en = "B - en",
            sv = "B - sv"
        )
    )

    @Test fun `map detailed cause category correctly into domain model`() {
        val result = detailedCauseCategoryNetworkEntity.toDomainModel()
        assertThat(result.id).isEqualTo(2)
        assertThat(result.name).isEqualTo("Category name for B2")
        assertThat(result.passengerFriendlyName?.fi).isEqualTo("B - fi")
        assertThat(result.passengerFriendlyName?.en).isEqualTo("B - en")
        assertThat(result.passengerFriendlyName?.sv).isEqualTo("B - sv")
    }

    private val thirdCauseCategoryNetworkEntity = ThirdLevelCauseCategoryNetworkEntity(
        id = 3,
        thirdCategoryCode = "C3",
        thirdCategoryName = "Category name for C3",
        validFrom = "2020-10-10T10:45Z",
        validTo = null,
        passengerTerm = PassengerTermNetworkEntity(
            fi = "C - fi",
            en = "C - en",
            sv = "C - sv"
        )
    )

    @Test fun `map third cause category correctly into domain model`() {
        val result = thirdCauseCategoryNetworkEntity.toDomainModel()
        assertThat(result.id).isEqualTo(3)
        assertThat(result.name).isEqualTo("Category name for C3")
        assertThat(result.passengerFriendlyName?.fi).isEqualTo("C - fi")
        assertThat(result.passengerFriendlyName?.en).isEqualTo("C - en")
        assertThat(result.passengerFriendlyName?.sv).isEqualTo("C - sv")
    }
}
