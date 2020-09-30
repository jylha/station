package com.example.station.data.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationService {

    fun currentLocation(): Flow<Location>
}
