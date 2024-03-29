package dev.jylha.station.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.jylha.station.data.stations.StationNameMapper
import org.junit.Rule
import org.junit.Test

class StationNameProviderTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Suppress("TestFunctionName")
    @Composable private fun Station(stationCode: Int) {
        Text(stationName(stationCode) ?: "name not found")
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
        val testMapper = if (map != null)
            StationNameMapper { stationCode -> map[stationCode] } else null

        composeTestRule.setContent {
            StationNameProvider(stationNameMapper = testMapper) {
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
