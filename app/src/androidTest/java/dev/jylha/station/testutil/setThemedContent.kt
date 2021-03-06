package dev.jylha.station.testutil

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import dev.jylha.station.ui.theme.StationTheme

/**
 * Extension function that calls setContent on receiver with given [content] wrapped in
 * StationTheme with given [darkMode].
 * @receiver Compose test rule.
 */
fun ComposeContentTestRule.setThemedContent(
    darkMode: Boolean = true,
    content: @Composable () -> Unit
) {
    setContent {
        StationTheme(darkMode) {
            content()
        }
    }
}
