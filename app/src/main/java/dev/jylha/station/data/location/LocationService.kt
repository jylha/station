package dev.jylha.station.data.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

/** An interface for retrieving the device location. */
interface LocationService {

    /** Returns the last known location, or null, if it is not available. */
    suspend fun lastKnownLocation(): Location?

    /** Returns a flow of location updates. */
    fun locationUpdates(): Flow<Location>
}
