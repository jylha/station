package com.example.station.ui.stations

import androidx.compose.runtime.Immutable
import com.dropbox.android.external.store4.StoreResponse
import com.example.station.model.Station

@Immutable
data class StationsViewState(
    val stations: List<Station>,
    val isLoading: Boolean,
    val errorMessage: String?,
) {
    companion object {
        fun initial() = StationsViewState(
            stations = emptyList(),
            isLoading = false,
            errorMessage = null
        )
    }
}

fun StationsViewState.reduce(result: StoreResponse<List<Station>>) : StationsViewState {
    return when(result) {
        is StoreResponse.Loading -> copy(isLoading = true)
        is StoreResponse.Data -> copy(stations = result.value, isLoading = false)
        is StoreResponse.NoNewData -> copy(isLoading = false)
        is StoreResponse.Error.Exception -> copy(errorMessage = result.errorMessageOrNull())
        is StoreResponse.Error.Message -> copy(errorMessage = result.message)
    }
}
