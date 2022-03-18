package dev.jylha.station.domain

import android.location.Location
import android.os.SystemClock
import dev.jylha.station.data.location.LocationService
import dev.jylha.station.di.DefaultDispatcher
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/** A use case retrieving current or at lest a relatively recent device location. */
interface GetLocationUseCase {
    suspend operator fun invoke(): Location
}

/** A default implementation for the [GetLocationUseCase] interface. */
class DefaultGetLocationUseCase @Inject constructor(
    private val service: LocationService,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : GetLocationUseCase {

    override suspend fun invoke(): Location {
        return withContext(dispatcher) {
            val location = service.lastKnownLocation()
            if (location != null && location.isRecent()) location else
            service.locationUpdates().first()
        }
    }
}

/** Checks whether the location is recent (set within a minute). */
private fun Location.isRecent() : Boolean {
    val limit: Long = TimeUnit.MINUTES.toNanos(1)
    val time = SystemClock.elapsedRealtimeNanos() - elapsedRealtimeNanos
    return time < limit
}
