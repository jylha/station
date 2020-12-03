package com.example.station.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
class DefaultLocationService @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationService {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun currentLocation(): Flow<Location> {
        return channelFlow {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.maxWaitTime = 1000

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult?) {
                    result?.locations?.forEach { location ->
                        runCatching { offer(location) }
                    }
                }
            }
            client.requestLocationUpdates(locationRequest, locationCallback, null).await()
            awaitClose { client.removeLocationUpdates(locationCallback) }
        }
    }
}

