package dev.jylha.station.testutil

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasText

fun SemanticsNodeInteractionsProvider.onNodeWithSubstring(
    text: String,
    ignoreCase: Boolean = false,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasText(text, substring = true, ignoreCase), useUnmergedTree)
