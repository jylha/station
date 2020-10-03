package com.example.station.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithSubstring
import androidx.ui.test.onNodeWithText
import com.example.station.ui.components.LocationPermissionAmbient
import com.example.station.ui.components.Permission
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val rule = createComposeRule(disableTransitions = true)

    @Test fun loadingApplicationSettings() {
        val state = HomeViewState(loading = true)
        rule.setContent { HomeScreen(viewState = state) }

        rule.onNodeWithText("Loading application settings.")
        rule.onNodeWithSubstring("Welcome").assertDoesNotExist()
    }

    @Test fun displayWelcomeText() {
        val state = HomeViewState(loading = false)
        rule.setContent {
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
    Providers(LocationPermissionAmbient provides permission) {
        content()
    }
}

data class MockLocationPermission(
    private val isGranted: Boolean, private val grantRequest: Boolean
) : Permission {
    override fun isGranted(): Boolean = isGranted
    override fun request(onResult: (Boolean) -> Unit) {
        onResult(grantRequest)
    }
}
