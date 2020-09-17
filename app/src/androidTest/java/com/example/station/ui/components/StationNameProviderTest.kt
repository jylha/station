package com.example.station.ui.components

import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithText
import com.example.station.data.stations.StationNameMapper
import org.junit.Rule
import org.junit.Test


class StationNameProviderTest {

    @get:Rule
    val composeTestRule = createComposeRule(disableTransitions = true)

    @Composable
    private fun Station(uic: Int) {
        Text(StationName.forUic(uic) ?: "not found")
    }

    private fun launchWithoutStationNameProvider(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            content()
        }
    }

    private fun launchWithStationNameProvider(
        map: Map<Int, String>? = null,
        content: @Composable () -> Unit
    ) {
        val testMapper = if (map != null) object : StationNameMapper {
            override fun stationName(stationUic: Int): String? = map[stationUic]
            override fun stationName(stationShortCode: String): String? = null
        } else null

        composeTestRule.setContent {
            StationNameProvider(nameMapper = testMapper) {
                content()
            }
        }
    }

    private fun testMapper(map: Map<Int, String>): StationNameMapper {
        return object : StationNameMapper {
            override fun stationName(stationUic: Int): String? = map[stationUic]
            override fun stationName(stationShortCode: String): String? = null
        }
    }

    @Test(expected = IllegalStateException::class)
    fun launch_without_station_name_provider_fails() {
        launchWithoutStationNameProvider {
            Station(1)
        }
    }

    @Test fun launch_with_station_name_provider_and_no_map() {
        launchWithStationNameProvider {
            Station(1)
        }
        onNodeWithText("not found").assertIsDisplayed()
    }

    @Test fun launch_with_station_name_provider() {
        launchWithStationNameProvider(mapOf(1 to "something")) {
            Station(1)
        }
        onNodeWithText("something").assertIsDisplayed()
    }

}
