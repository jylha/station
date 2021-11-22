package dev.jylha.station.ui.stations

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.jylha.station.R
import dev.jylha.station.model.Station
import dev.jylha.station.ui.util.applyIf
import dev.jylha.station.util.findAllMatches

/**
 * A component that displays a list of stations. The list is divided into two sections: the first
 * section contains a list of recent stations, the second section contains a list of all stations,
 * or a list matching stations if [searchText] is not blank.
 *
 * @param recentStations The list of stations displayed in the first section.
 * @param stations The list of stations displayed in the second section.
 * @param onSelect Called when a station is selected from the list.
 * @param modifier Optional modifier applied to the list.
 * @param searchText A text that will have all its occurrences in the list highlighted.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StationList(
    recentStations: List<Station>,
    stations: List<Station>,
    onSelect: (Station) -> Unit,
    modifier: Modifier,
    searchText: String = ""
) {
    Surface(modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            if (searchText.isBlank() && recentStations.isNotEmpty()) {
                item { StationListLabel(stringResource(R.string.label_recent)) }
                items(recentStations) { station ->
                    StationListEntry(station.name, onSelect = { onSelect(station) })
                }
                item { Divider() }
            }

            item {
                StationListLabel(
                    stringResource(
                        if (searchText.isBlank()) R.string.label_all_stations
                        else R.string.label_matching_stations
                    )
                )
            }

            stations
                .groupBy { station -> station.name.first() }
                .forEach { (letter, group) ->
                    // Add the first station in each group both above and below the sticky header
                    // to make it it look like it appears and disappear properly when scrolling
                    // the list. Both of the items are aligned with the sticky header.

                    item {
                        val station = group.first()
                        StationListEntry(
                            stationName = station.name,
                            onSelect = { onSelect(station) },
                            modifier = Modifier
                                .requiredHeight(Dp.Hairline)
                                .wrapContentHeight(align = Alignment.Top, unbounded = true),
                            searchText = searchText
                        )
                    }

                    stickyHeader { StationListStickyLetter(letter) }

                    itemsIndexed(group) { index, station ->
                        StationListEntry(
                            stationName = station.name,
                            onSelect = { onSelect(station) },
                            modifier = Modifier.applyIf(index == 0) {
                                Modifier
                                    .requiredHeight(Dp.Hairline)
                                    .wrapContentHeight(align = Alignment.Bottom, unbounded = true)
                            },
                            searchText = searchText
                        )
                    }
                }
        }
    }
}

@Composable
private fun StationListLabel(label: String) {
    Text(
        label.uppercase(),
        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
        style = MaterialTheme.typography.caption,
    )
}

@Composable
private fun StationListStickyLetter(letter: Char) {
    Box(
        modifier = Modifier.requiredWidth(StickyLetterColumnWidth),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter.toString().uppercase(),
            modifier = Modifier.padding(vertical = 10.dp),
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.body1,
        )
    }
}

@Composable
private fun StationListEntry(
    stationName: String,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    searchText: String = ""
) {
    val surfaceColor = MaterialTheme.colors.surface
    val highlightColor = MaterialTheme.colors.onSurface.compositeOver(surfaceColor)
    val dimmedColor = highlightColor.copy(alpha = 0.7f).compositeOver(surfaceColor)
    val textColor = if (searchText.isBlank()) highlightColor else dimmedColor
    val text = rememberHighlightedText(stationName, searchText, highlightColor)
    Box(
        modifier
            .fillMaxWidth()
            .padding(start = StickyLetterColumnWidth)
            .background(surfaceColor)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onSelect)
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Text(text, style = MaterialTheme.typography.body1, color = textColor)
    }
}

@Composable
private fun rememberHighlightedText(
    text: String,
    highlightedText: String,
    highlightedTextColor: Color
) = remember(text, highlightedText, highlightedTextColor) {
    with(AnnotatedString.Builder(text)) {
        if (highlightedText.isNotBlank()) {
            text.findAllMatches(highlightedText).forEach { (startIndex, endIndex) ->
                addStyle(
                    SpanStyle(highlightedTextColor, fontWeight = FontWeight.Bold),
                    startIndex,
                    endIndex
                )
            }
        }
        toAnnotatedString()
    }
}

private val StickyLetterColumnWidth = 32.dp
