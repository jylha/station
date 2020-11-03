package com.example.station.util

/** Filter a list with [predicate] only when given [condition] is true. */
inline fun <T> List<T>.filterWhen(
    condition: Boolean,
    predicate: (T) -> Boolean
): List<T> {
    return if (condition) filterTo(ArrayList(), predicate) else this
}
