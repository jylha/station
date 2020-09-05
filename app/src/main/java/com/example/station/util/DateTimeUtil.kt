package com.example.station.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun ZonedDateTime.atLocalZone(): LocalDateTime =
    this.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()