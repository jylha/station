package dev.jylha.station.ui.common

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/** Returns true when the device is in landscape orientation. */
@Composable fun landscapeOrientation(): Boolean =
    LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

/** Returns true when the device is in portrait orientation. */
@Composable fun portraitOrientation(): Boolean =
    LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
