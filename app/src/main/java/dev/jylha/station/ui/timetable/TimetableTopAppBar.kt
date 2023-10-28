package dev.jylha.station.ui.timetable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.jylha.station.R
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import dev.jylha.station.ui.LocalePreviews
import dev.jylha.station.ui.theme.StationTheme

/**
 * Timetable top app bar.
 *
 * @param stationName Station name.
 * @param selectedTimetableTypes Selected timetable types.
 * @param selectedTrainCategories Selected train categories.
 * @param filterSelectionEnabled Whether filters selection is shown.
 * @param onShowFilters Called to show filter selection.
 * @param onHideFilters Called to hide filter selection.
 * @param onSelectStation Called to navigate to the stations list.
 * @param modifier An optional modifier applied to the top app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableTopAppBar(
    stationName: String?,
    selectedTimetableTypes: TimetableTypes,
    selectedTrainCategories: TrainCategories,
    filterSelectionEnabled: Boolean,
    onShowFilters: () -> Unit,
    onHideFilters: () -> Unit,
    onSelectStation: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            AnimatedVisibility(stationName != null, enter = fadeIn(), exit = fadeOut()) {
                Column(modifier) {
                    TopAppBarTitle(stationName ?: "")
                    TopAppBarSubtitle(selectedTimetableTypes, selectedTrainCategories)
                }
            }
        },
        actions = {
            val selectStationLabel = stringResource(R.string.label_select_station)
            IconButton(onClick = onSelectStation) {
                Icon(Icons.Rounded.LocationCity, contentDescription = selectStationLabel)
            }
            if (filterSelectionEnabled) {
                val hideFiltersLabel = stringResource(R.string.label_hide_filters)
                IconButton(onClick = onHideFilters) {
                    Icon(Icons.Default.ExpandLess, contentDescription = hideFiltersLabel)
                }
            } else {
                val showFiltersLabel = stringResource(R.string.label_show_filters)
                IconButton(onClick = onShowFilters) {
                    Icon(Icons.Default.FilterList, contentDescription = showFiltersLabel)
                }
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    )
}

/** A title displaying the station name. */
@Composable
private fun TopAppBarTitle(stationName: String, modifier: Modifier = Modifier) {
    Text(stationName, modifier)
}

/** A subtitle displaying the selected categories. */
@Composable
private fun TopAppBarSubtitle(
    timetableTypes: TimetableTypes,
    trainCategories: TrainCategories,
    modifier: Modifier = Modifier
) {
    val subtitleText = if (trainCategories.size == 1) {
        if (trainCategories.contains(Train.Category.LongDistance)) {
            if (timetableTypes.size == 1) {
                if (timetableTypes.contains(TimetableRow.Type.Arrival)) {
                    stringResource(id = R.string.subtitle_arriving_long_distance_trains)
                } else {
                    stringResource(id = R.string.subtitle_departing_long_distance_trains)
                }
            } else {
                stringResource(id = R.string.subtitle_long_distance_trains)
            }
        } else {
            if (timetableTypes.size == 1) {
                if (timetableTypes.contains(TimetableRow.Type.Arrival)) {
                    stringResource(id = R.string.subtitle_arriving_commuter_trains)
                } else {
                    stringResource(id = R.string.subtitle_departing_commuter_trains)
                }
            } else {
                stringResource(id = R.string.subtitle_commuter_trains)
            }
        }
    } else {
        if (timetableTypes.size == 1) {
            if (timetableTypes.contains(TimetableRow.Type.Arrival)) {
                stringResource(id = R.string.subtitle_arriving_trains)
            } else {
                stringResource(id = R.string.subtitle_departing_trains)
            }
        } else {
            stringResource(id = R.string.subtitle_all_trains)
        }
    }
    Text(subtitleText, modifier, style = MaterialTheme.typography.labelMedium)
}

@LocalePreviews
@Composable
private fun TimetableTopAppBarPreview(
    @PreviewParameter(TimetableTopAppBarPreviewParameterProvider::class)
    pair: Pair<TimetableTypes, TrainCategories>
) {
    val (timetableTypes, trainCategories) = pair

    StationTheme {
        TimetableTopAppBar(
            stationName = "Helsinki",
            selectedTimetableTypes = timetableTypes,
            selectedTrainCategories = trainCategories,
            filterSelectionEnabled = false,
            onShowFilters = {},
            onHideFilters = {},
            onSelectStation = {},
        )
    }
}

internal class TimetableTopAppBarPreviewParameterProvider :
    PreviewParameterProvider<Pair<TimetableTypes, TrainCategories>> {
    private val params = listOf(
        Pair(
            TimetableTypes(TimetableRow.Type.Arrival),
            TrainCategories(Train.Category.LongDistance)
        ),
        Pair(
            TimetableTypes(TimetableRow.Type.Departure),
            TrainCategories(Train.Category.Commuter)
        ),
    )
    override val values: Sequence<Pair<TimetableTypes, TrainCategories>> get() = params.asSequence()
    override val count: Int get() = params.size
}
