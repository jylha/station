package dev.jylha.station.testutil

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider

fun SemanticsNodeInteractionsProvider.onNodeWithSubstring(
    text: String,
    ignoreCase: Boolean = false,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNode(hasSubstring(text, ignoreCase), useUnmergedTree)
