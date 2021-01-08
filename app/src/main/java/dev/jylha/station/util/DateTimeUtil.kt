package dev.jylha.station.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun ZonedDateTime.atLocalZone(): LocalDateTime =
    this.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

fun ZonedDateTime.toLocalTimeString(): String = atLocalZone().format(formatter)

/** Checks whether the difference between two times is at least a minute. */
fun ZonedDateTime.differsFrom(other: ZonedDateTime): Boolean =
    abs(ChronoUnit.MINUTES.between(this, other)) >= 1

