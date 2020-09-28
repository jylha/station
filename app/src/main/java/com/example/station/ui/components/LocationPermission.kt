package com.example.station.ui.components

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ContextAmbient
import androidx.core.app.ActivityCompat
import timber.log.Timber

@Composable fun LocationPermissionProvider(
    activity: AppCompatActivity,
    content: @Composable () -> Unit
) {
    val locationPermissionHandler = remember {
        LocationPermissionHandler(activity)
    }

    Providers(LocationPermissionAmbient provides locationPermissionHandler) {
        content()
    }
}

object LocationPermission {
    @Composable
    fun isGranted(): Boolean = LocationPermissionAmbient.current.isGranted()

    @Composable
    fun request() = LocationPermissionAmbient.current.request()
}


@Composable fun isLocationPermissionGranted(): Boolean {
    return LocationPermissionAmbient.current.isGranted()
}

val LocationPermissionAmbient = ambientOf<LocationPermissionHandler> {
    error("LocationPermission is not set.")
}

data class LocationPermissionHandler(val activity: AppCompatActivity) {
    private val locationRequestCode = 555
    private var granted = false

    init {
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            granted = result
            Timber.d("Granted = $granted!")
        }
    }

    /** Checks whether access to coarse locations is granted. */
    fun isGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** Checks whether rationale for granting access to location should be shown. */
    private fun should(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    /** Request permission to access coarse location. */
    fun request() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            locationRequestCode
        )
    }

    private fun onResult() {}
}


@Composable fun something() {

    val context = ContextAmbient.current
    val activity = context as Activity

    //activity.onRequestPermissionsResult()

    when {
        context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED -> {
            // Ok.
            Timber.d("Permission has been granted.")
        }

        ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) -> {
            // Show
            Timber.d("Should show rationale.")
        }

        else -> {
            val requestCode = 555
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), requestCode
            )
        }
    }
}
