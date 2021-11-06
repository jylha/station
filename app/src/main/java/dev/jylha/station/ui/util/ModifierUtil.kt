package dev.jylha.station.ui.util

import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.Modifier

/**
 * Concatenates this modifier with another when given [condition] is true.
 *
 * Returns a [Modifier] representing this modifier followed by [other] in sequence.
 */
fun Modifier.thenIf(condition: Boolean, other: Modifier): Modifier =
    if (other === Modifier || !condition) this else CombinedModifier(this, other)

/**
 * Concatenates this modifier with another returned by [factory] when given [condition] is true.
 *
 * Returns a [Modifier] representing this modifier followed by a modifier returned
 * by [factory] in sequence.
 */
fun Modifier.thenIf(condition: Boolean, factory: Modifier.() -> Modifier): Modifier {
    val modifier = if (condition) factory() else Modifier
    return if (modifier === Modifier) this else CombinedModifier(this, modifier)
}
