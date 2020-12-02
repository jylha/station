/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * The implementation for SwipeToRefreshLayout has been copied with minor changes from
 * android / compose-samples / JetNews -sample project.
 * See https://github.com/android/compose-samples/blob/main/JetNews/
 */

package com.example.station.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onCommit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp

private val refreshDistanceDp = 80.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToRefreshLayout(
    modifier: Modifier = Modifier,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    refreshIndicator: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val refreshDistance = with(AmbientDensity.current) { refreshDistanceDp.toPx() }
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
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = { swipeableState.offset.value })
        ) {
            if (swipeableState.offset.value != -refreshDistance) {
                refreshIndicator()
            }
        }
        onCommit(refreshing, swipeableState.value) {
            swipeableState.animateTo(refreshing)
        }
    }
}
