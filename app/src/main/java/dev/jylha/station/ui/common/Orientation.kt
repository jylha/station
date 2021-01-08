package dev.jylha.station.ui.common

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientConfiguration

/** Returns true when the device is in landscape orientation. */
@Composable fun landscapeOrientation(): Boolean =
    AmbientConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

/** Returns true when the device is in portrait orientation. */
@Composable fun portraitOrientation(): Boolean =
    AmbientConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
