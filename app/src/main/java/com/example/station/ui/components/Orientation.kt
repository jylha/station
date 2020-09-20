package com.example.station.ui.components

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ConfigurationAmbient

/** Returns true when the device is in landscape orientation. */
@Composable fun landscapeOrientation(): Boolean =
    ConfigurationAmbient.current.orientation == Configuration.ORIENTATION_LANDSCAPE

/** Returns true when the device is in portrait orientation. */
@Composable fun portraitOrientation(): Boolean =
    ConfigurationAmbient.current.orientation == Configuration.ORIENTATION_PORTRAIT
