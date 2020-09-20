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
        Text(stationName(uic) ?: "name not found")
    }

    private fun composeWithoutStationNameProvider(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            content()
        }
    }

    private fun composeWithStationNameProvider(
        map: Map<Int, String>? = null,
        content: @Composable () -> Unit
    ) {
        val testMapper = if (map != null) object : StationNameMapper {
            override fun stationName(stationUic: Int): String? = map[stationUic]
        } else null

        composeTestRule.setContent {
            StationNameProvider(nameMapper = testMapper) {
                content()
            }
        }
    }

    @Test(expected = IllegalStateException::class)
    fun composeWithoutStationNameProvider_fails() {
        composeWithoutStationNameProvider {
            Station(1)
        }
    }

    @Test fun composeWithEmptyStationNameProvider_succeedsWithoutName() {
        composeWithStationNameProvider {
            Station(1)
        }
        composeTestRule.onNodeWithText("name not found").assertIsDisplayed()
    }

    @Test fun composeWithStationNameProvider_succeeds() {
        composeWithStationNameProvider(mapOf(1 to "something")) {
            Station(1)
        }
        composeTestRule.onNodeWithText("something").assertIsDisplayed()
    }

}
