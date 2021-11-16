package dev.jylha.station.ui.util

import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.Modifier

/**
 * Concatenates this modifier with another modifier returned by [factory] when given [condition]
 * is true.
 *
 * Returns a [Modifier] representing this modifier followed by a modifier returned
 * by [factory] in sequence.
 */
fun Modifier.applyIf(condition: Boolean, factory: () -> Modifier): Modifier {
    val modifier = if (condition) factory() else Modifier
    return if (modifier === Modifier) this else CombinedModifier(this, modifier)
}
