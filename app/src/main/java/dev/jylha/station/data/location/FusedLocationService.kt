package dev.jylha.station.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
class FusedLocationService @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationService {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun lastKnownLocation(): Location? {
        var result: Location? = null
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? -> result = location }
            .await()
        return result
    }

    override fun locationUpdates(): Flow<Location> {
        return channelFlow {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
            locationRequest.maxWaitTime = 1000

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.locations.forEach { location ->
                        runCatching { this@channelFlow.trySend(location).isSuccess }
                    }
                }
            }

            fusedLocationClient
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                .await()
            awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
        }
    }
}
