package dev.jylha.station.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.test.ExperimentalTesting
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithSubstring
import androidx.compose.ui.test.onNodeWithText
import dev.jylha.station.testutil.setThemedContent
import dev.jylha.station.ui.common.AmbientLocationPermission
import dev.jylha.station.ui.common.Permission
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTesting::class)
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
        rule.onNodeWithText("Select station").assertDoesNotExist()
        rule.onNodeWithText("Nearest station").assertDoesNotExist()
    }

    @Test fun displayWelcomeText() {
        val state = HomeViewState(isLoadingSettings = false)
        rule.setThemedContent(darkMode = false) {
            MockLocationPermissionProvider(isGranted = true, grantRequest = true) {
                HomeScreen(viewState = state)
            }
        }

        rule.onNodeWithText("Loading application settings.").assertDoesNotExist()
        rule.onNodeWithContentDescription("Show information about the application")
            .assertIsDisplayed()
        rule.onNodeWithSubstring("Welcome").assertIsDisplayed()
        rule.onNodeWithText("Select station").assertIsDisplayed()
        rule.onNodeWithText("Nearest station").assertIsDisplayed()
    }
}

@Suppress("TestFunctionName")
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
