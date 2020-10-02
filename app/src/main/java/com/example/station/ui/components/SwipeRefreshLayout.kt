package com.example.station.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offsetPx
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp

private val refreshDistanceDp = 80.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeRefreshLayout(
    modifier: Modifier = Modifier,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    refreshIndicator: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val refreshDistance = with(DensityAmbient.current) { refreshDistanceDp.toPx() }
    val swipeableState = rememberSwipeableState(false) { newValue ->
        if (newValue && !refreshing) onRefresh()
        true
    }
    Box(
        modifier = modifier.swipeable(
            state = swipeableState,
            anchors = mapOf(-refreshDistance to false, refreshDistance to true),
            thresholds = { _, _ -> FractionalThreshold(0.5f) },
            orientation = Orientation.Vertical
        )
    ) {
        content()
        Box(Modifier.align(Alignment.TopCenter).offsetPx(y = swipeableState.offset)) {
            if (swipeableState.offset.value != -refreshDistance) {
                refreshIndicator()
            }
        }
        onCommit(refreshing, swipeableState.value) {
            swipeableState.animateTo(refreshing)
        }
    }
}
