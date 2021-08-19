package dev.jylha.station.ui.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout

/**
 * Creates a modifier that sets the height of a composable to a fraction of its height.
 * @receiver Modifier.
 * @param fraction Requested fractions of the height.
 */
fun Modifier.heightFraction(fraction: Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val constrainedHeight = (placeable.height * fraction).toInt()
        layout(placeable.width, constrainedHeight) {
            placeable.placeRelative(0, 0)
        }
    }

