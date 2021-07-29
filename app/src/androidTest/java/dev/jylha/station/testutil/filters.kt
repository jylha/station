package dev.jylha.station.testutil

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher

fun hasSubstring(text: String, ignoreCase: Boolean = false): SemanticsMatcher {
    val propertyName = SemanticsProperties.Text.name
    return SemanticsMatcher(
        "$propertyName contains '$text' (ignoreCase: $ignoreCase) as substring"
    ) { node ->
        val content = node.config.getOrNull(SemanticsProperties.Text)?.joinToString(", ") ?: ""
        content.contains(text, ignoreCase)
    }
}
