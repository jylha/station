package com.example.station.testutil

import androidx.compose.runtime.Composable
import androidx.ui.test.ComposeTestRuleJUnit
import com.example.station.ui.theme.StationTheme

/**
 * Extension function that calls setContent on receiver with given [content] wrapped in
 * StationTheme with given [darkMode]].
 * @receiver Compose test rule.
 */
fun ComposeTestRuleJUnit.setThemedContent(
    darkMode: Boolean = true,
    content: @Composable () -> Unit
) {
    setContent {
        StationTheme(darkMode) {
            content()
        }
    }
}
