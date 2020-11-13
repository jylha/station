package com.example.station.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithSubstring
import androidx.ui.test.onNodeWithText
import com.example.station.testutil.setThemedContent
import com.example.station.ui.common.AmbientLocationPermission
import com.example.station.ui.common.Permission
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule val rule = createComposeRule()

    @Test fun loadingApplicationSettings() {
        val state = HomeViewState(isLoadingSettings = true)

        rule.clockTestRule.pauseClock()
        rule.setThemedContent {
            MockLocationPermissionProvider(isGranted = true, grantRequest = true) {
                HomeScreen(viewState = state)
            }
        }
        rule.clockTestRule.advanceClock(500)
        rule.onNodeWithText("Loading application settings.")
        rule.onNodeWithSubstring("Welcome").assertDoesNotExist()
    }

    @Test fun displayWelcomeText() {
        val state = HomeViewState(isLoadingSettings = false)
        rule.setThemedContent(darkMode = false) {
            MockLocationPermissionProvider(isGranted = true, grantRequest = true) {
                HomeScreen(viewState = state)
            }
        }

        rule.onNodeWithText("Loading application settings.").assertDoesNotExist()
        rule.onNodeWithSubstring("Welcome").assertIsDisplayed()
        rule.onNodeWithText("Select station").assertIsDisplayed()
        rule.onNodeWithText("Nearest station").assertIsDisplayed()
    }
}

@Composable fun MockLocationPermissionProvider(
    isGranted: Boolean,
    grantRequest: Boolean,
    content: @Composable () -> Unit
) {
    val permission = MockLocationPermission(isGranted, grantRequest)
    Providers(AmbientLocationPermission provides permission) {
        content()
    }
}

data class MockLocationPermission(
    private val isGranted: Boolean, private val grantRequest: Boolean
) : Permission {
    override fun isGranted(): Boolean = isGranted
    override fun request(onResult: (Boolean) -> Unit) = onResult(grantRequest)
}
