package com.example.station.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat

@Composable fun LocationPermissionProvider(
    activity: AppCompatActivity,
    content: @Composable () -> Unit
) {
    val locationPermission = remember { LocationPermission(activity) }
    Providers(LocationPermissionAmbient provides locationPermission) {
        content()
    }
}

val LocationPermissionAmbient = ambientOf<LocationPermission> {
    error("LocationPermission is not set.")
}

/**
 * Checks whether location permission is granted, requests permission when it has not yet been
 * granted, and calls [onResult] with value depending on whether permission is granted.
 */
fun withPermission(locationPermission: LocationPermission, onResult: (Boolean) -> Unit) {
    if (locationPermission.isGranted()) onResult.invoke(true)
    else locationPermission.request(onResult)
}

/** A helper class for checking and requesting location permission. */
data class LocationPermission(val activity: AppCompatActivity) {

    /** Checks whether access to coarse locations is granted. */
    fun isGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** Requests permission to access coarse location. */
    fun request(onResult: (Boolean) -> Unit) {
        val startForResult = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result -> onResult(result) }
        startForResult.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    /** Checks whether rationale for granting access to location should be shown. */
    private fun shouldShowRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}
