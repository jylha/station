package com.example.station.ui.select

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.example.station.Screen
import com.example.station.model.Station
import timber.log.Timber

@Composable
fun SelectStationScreen(
    navigateTo: (Screen) -> Unit
) {
    val viewModel = viewModel<SelectStationsViewModel>()
    val stations by viewModel.stations.observeAsState()

    var searchEnabled by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val filteredStation by remember(searchText, stations) {
        mutableStateOf(stations?.filter { it.name.contains(searchText, ignoreCase = true) }
            ?: emptyList())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Select station")
                },
                actions = {
                    if (!searchEnabled) {
                        IconButton(onClick = { searchEnabled = true }) {
                            Icon(Icons.Default.Search)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (stations != null) {
            StationList(filteredStation, onSelect = { station ->
                navigateTo(Screen.Timetable(station))
            }, Modifier.padding(innerPadding), searchBar = {
                if (searchEnabled) {
                    SearchBar(
                        text = searchText,
                        onChange = { text ->
                            searchText = text
                            Timber.d("search text: $text")
                        },
                        onClose = { searchEnabled = false; searchText = "" }
                    )
                }
            })
        } else {
            LoadingStations()
        }
    }
}

@Composable
fun StationList(
    stations: List<Station>,
    onSelect: (Station) -> Any,
    modifier: Modifier = Modifier,
    searchBar: @Composable (() -> Unit)? = null
) {
    Column(modifier) {
        if (searchBar != null) {
            searchBar()
        }
        LazyColumnFor(stations) { station ->
            Text(
                text = station.name,
                modifier = Modifier
                    .clickable(onClick = { onSelect(station) })
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun LoadingStations() {
    Surface(Modifier.fillMaxSize()) {
        Text(
            text = "Loading stations...",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun SearchBar(
    text: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)?
) {
    Surface(modifier, color = MaterialTheme.colors.surface, elevation = 4.dp) {
        Row(
            Modifier.fillMaxWidth().padding(8.dp),
            verticalGravity = Alignment.CenterVertically
        ) {
            if (onClose != null) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.KeyboardBackspace)
                }
            }
            TextField(
                value = text,
                onValueChange = onChange,
                label = {},
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.surface,
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Done,
                onImeActionPerformed = { _, kb ->
                    kb?.hideSoftwareKeyboard()
                    onClose?.invoke()
                }
            )
        }
    }
}