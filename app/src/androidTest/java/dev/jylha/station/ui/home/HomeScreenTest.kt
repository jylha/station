package dev.jylha.station.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import dev.jylha.station.testutil.onNodeWithSubstring
import dev.jylha.station.testutil.setThemedContent
import dev.jylha.station.ui.common.LocalLocationPermission
import dev.jylha.station.ui.common.Permission
import org.junit.Rule
import org.junit.Test

private const val TEXT_WELCOME = "Welcome"
private const val TEXT_LOADING_SETTINGS = "Retrieving application settings."
private const val TEXT_SELECT_STATION = "Select station"
private const val TEXT_NEAREST_STATION = "Nearest station"
private const val DESCRIPTION_ABOUT = "Show information about the application"

class HomeScreenTest {

    @get:Rule val rule = createComposeRule()

    @Test fun loadingApplicationSettings() {
        val state = HomeViewState(isLoadingSettings = true)
        rule.mainClock.autoAdvance = false
        rule.setThemedContent {
            MockLocationPermissionProvider(isGranted = true, grantRequest = true) {
                HomeScreen(state)
            }
        }
        rule.mainClock.advanceTimeBy(500)
        rule.onNodeWithText(TEXT_LOADING_SETTINGS).assertIsDisplayed()
        rule.onNodeWithContentDescription(DESCRIPTION_ABOUT).assertDoesNotExist()
        rule.onNodeWithSubstring(TEXT_WELCOME).assertDoesNotExist()
        rule.onNodeWithText(TEXT_SELECT_STATION).assertDoesNotExist()
        rule.onNodeWithText(TEXT_NEAREST_STATION).assertDoesNotExist()
    }

    @Test fun displayWelcomeText() {
        val state = HomeViewState(isLoadingSettings = false)
        rule.setThemedContent(darkMode = false) {
            MockLocationPermissionProvider(isGranted = true, grantRequest = true) {
                HomeScreen(state)
            }
        }
        rule.onNodeWithText(TEXT_LOADING_SETTINGS).assertDoesNotExist()
        rule.onNodeWithContentDescription(DESCRIPTION_ABOUT).assertIsDisplayed()
        rule.onNodeWithSubstring(TEXT_WELCOME).assertIsDisplayed()
        rule.onNodeWithText(TEXT_SELECT_STATION).assertIsDisplayed()
        rule.onNodeWithText(TEXT_NEAREST_STATION).assertIsDisplayed()
    }
}

@Suppress("TestFunctionName")
@Composable fun MockLocationPermissionProvider(
    isGranted: Boolean,
    grantRequest: Boolean,
    content: @Composable () -> Unit
) {
    val permission = MockLocationPermission(isGranted, grantRequest)
    CompositionLocalProvider(LocalLocationPermission provides permission) {
        content()
    }
}

data class MockLocationPermission(
    private val isGranted: Boolean, private val grantRequest: Boolean
) : Permission {
    override fun isGranted(): Boolean = isGranted
    override fun request(onResult: (Boolean) -> Unit) = onResult(grantRequest)
}
