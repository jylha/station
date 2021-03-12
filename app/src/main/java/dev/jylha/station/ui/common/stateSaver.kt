package dev.jylha.station.ui.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.runtime.structuralEqualityPolicy

/**
 * TODO: 12.3.2021 This can be removed when default saver is working properly.
 * Temporary fix to prevent a crash with rememberSaveable():
 * java.lang.ClassCastException: androidx.compose.ui.platform.ParcelableMutableStateHolder
 * cannot be cast to androidx.compose.runtime.MutableState
 */
fun <T> stateSaver() = listSaver<MutableState<T>, Any>(
    save = { state ->
        listOf(
            state.value ?: "null",
            when ((state as SnapshotMutableState).policy) {
                structuralEqualityPolicy<T>() -> 0
                referentialEqualityPolicy<T>() -> 1
                neverEqualPolicy<T>() -> 2
                else -> throw IllegalStateException("Mutation policy is not supported")
            }
        )
    },
    restore = {
        val value = it[0]
        val policy = when (it[1]) {
            0 -> structuralEqualityPolicy<T>()
            1 -> referentialEqualityPolicy<T>()
            else -> neverEqualPolicy<T>()
        }
        @Suppress("UNCHECKED_CAST")
        mutableStateOf((if (value == "null") null else value) as T, policy)
    }
)
