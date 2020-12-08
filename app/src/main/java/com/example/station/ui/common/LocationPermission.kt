package com.example.station.ui.common

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticAmbientOf
import androidx.core.app.ActivityCompat

/**
 * Location permission provider.
 * @param locationPermission Location permission.
 * @param content The composable content that can query the provided location permission.
 */
@Composable fun LocationPermissionProvider(
    locationPermission: LocationPermission,
    content: @Composable () -> Unit
) {
    Providers(AmbientLocationPermission provides locationPermission) {
        content()
    }
}

val AmbientLocationPermission = staticAmbientOf<Permission> {
    error("LocationPermission is not set.")
}

/** Interface for checking and requesting a permission. */
interface Permission {
    fun isGranted(): Boolean
    fun request(onResult: (Boolean) -> Unit)
}

/**
 * Checks whether specified [permission] is granted, requests permission when it has not yet been
 * granted, and calls [onResult] with value depending on whether permission is granted.
 */
fun withPermission(permission: Permission, onResult: (Boolean) -> Unit) {
    if (permission.isGranted()) onResult.invoke(true)
    else permission.request(onResult)
}

/** A helper class for checking and requesting location permission. */
data class LocationPermission(private val activity: ComponentActivity) : Permission {
    private var callback: ((Boolean) -> Unit)? = null
    private val resultLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> callback?.invoke(granted); callback = null }

    /** Checks whether access to fine locations is granted. */
    override fun isGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** Requests permission to access fine location. */
    override fun request(onResult: (Boolean) -> Unit) {
        callback = onResult
        resultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /** Checks whether rationale for granting access to location should be shown. */
    private fun shouldShowRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
}
