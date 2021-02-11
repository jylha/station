package dev.jylha.station.testutil

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasText

fun hasSubstring(text: String, ignoreCase: Boolean = false): SemanticsMatcher =
    hasText(text, substring = true, ignoreCase)
