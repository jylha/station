package dev.jylha.station.ui.common

import androidx.compose.runtime.Immutable
import java.time.ZonedDateTime

/**
 * An immutable wrapper for an instance of [ZonedDateTime].
 */
@Immutable
data class ImmutableTime(
    private val time: ZonedDateTime
) {
    operator fun invoke(): ZonedDateTime = time
}

/**
 * Utility function for wrapping a [ZonedDateTime] instance as an [ImmutableTime].
 */
internal fun ZonedDateTime.toImmutable(): ImmutableTime = ImmutableTime(this)
