package dev.jylha.station.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowRightAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import dev.jylha.station.R
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.ui.theme.StationTheme
import dev.jylha.station.util.toLocalTimeString
import kotlinx.datetime.Instant

/**
 * Displays time of arrival.
 *
 * @param timetableRow Timetable row.
 * @param modifier An optional modifier.
 */
@Composable
fun TimeOfArrival(timetableRow: TimetableRow?, modifier: Modifier = Modifier) {
    timetableRow?.run {
        when {
            cancelled -> CancelledArrival(modifier)
            actualTime != null ->
                ActualTime(actualTime, differenceInMinutes, timetableRow.type, modifier)
            estimatedTime != null && differenceInMinutes != 0 ->
                EstimatedTime(scheduledTime, estimatedTime, timetableRow.type, modifier)
            else -> ScheduledTime(scheduledTime, timetableRow.type, modifier)
        }
    } ?: Box(modifier)
}

/**
 * Displays time of departure.
 *
 * @param timetableRow Timetable row.
 * @param modifier An optional modifier.
 */
@Composable
fun TimeOfDeparture(timetableRow: TimetableRow?, modifier: Modifier = Modifier) {
    timetableRow?.run {
        when {
            cancelled -> CancelledDeparture(modifier)
            actualTime != null ->
                ActualTime(actualTime, differenceInMinutes, timetableRow.type, modifier)
            estimatedTime != null && differenceInMinutes != 0 ->
                EstimatedTime(scheduledTime, estimatedTime, timetableRow.type, modifier)
            else -> ScheduledTime(scheduledTime, timetableRow.type, modifier)
        }
    } ?: Box(modifier)
}

/**
 * Displays scheduled time.
 *
 * @param scheduledTime Scheduled time.
 * @param type TimetableRow type.
 * @param modifier Modifier.
 * @param track Track name that will be included in the content description.
 */
@Composable
fun ScheduledTime(
    scheduledTime: Instant,
    type: TimetableRow.Type,
    modifier: Modifier = Modifier,
    track: String? = null,
) {
    val context = LocalContext.current
    val trackText = trackString(track, type)
    val scheduledTimeText by produceLocalTime(scheduledTime)
    val description = remember(type, trackText, scheduledTimeText) {
        if (scheduledTimeText.isNotBlank())
            context.getString(
                if (type == TimetableRow.Type.Arrival) R.string.accessibility_label_scheduled_arrival
                else R.string.accessibility_label_scheduled_departure,
                trackText,
                scheduledTimeText
            )
        else ""
    }
    Text(
        scheduledTimeText,
        modifier.semantics { contentDescription = description },
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        style = MaterialTheme.typography.bodyLarge,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Light
    )
}

@PreviewLightDark
@Composable
private fun ScheduledTimePreview() {
    StationTheme {
        Surface {
            ScheduledTime(
                scheduledTime = Instant.parse("2020-01-01T09:30Z"),
                type = TimetableRow.Type.Arrival
            )
        }
    }
}

/**
 * Composable for displaying estimated time of arrival or departure.
 * @param scheduledTime Scheduled time.
 * @param estimatedTime Estimated time.
 * @param type TimetableRow type.
 * @param modifier Modifier.
 * @param track Track name that will be included in the content description.
 */
@Composable
fun EstimatedTime(
    scheduledTime: Instant,
    estimatedTime: Instant,
    type: TimetableRow.Type,
    modifier: Modifier = Modifier,
    track: String? = null
) {
    val context = LocalContext.current
    val trackText = trackString(track, type)
    val localTimes by produceLocalTimes(scheduledTime, estimatedTime)
    val (scheduledTimeText, estimatedTimeText) = localTimes
    val description = remember(type, trackText, estimatedTimeText) {
        if (estimatedTimeText.isNotBlank())
            context.getString(
                if (type == TimetableRow.Type.Arrival) R.string.accessibility_label_estimated_arrival
                else R.string.accessibility_label_estimated_departure,
                trackText,
                estimatedTimeText
            )
        else ""
    }
    val textStyle = MaterialTheme.typography.bodyLarge
    val fontStyle = FontStyle.Italic
    val fontWeight = FontWeight.Light
    Row(
        modifier.semantics { contentDescription = description },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            scheduledTimeText, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            style = textStyle, fontStyle = fontStyle, fontWeight = fontWeight
        )
        if (scheduledTimeText.isNotBlank() && estimatedTimeText.isNotBlank()) {
            Icon(
                Icons.AutoMirrored.Rounded.ArrowRightAlt, contentDescription = null,
                Modifier.padding(horizontal = 4.dp, vertical = 0.dp).size(16.dp),
                tint = StationTheme.colors.delayed
            )
            Text(
                estimatedTimeText, color = StationTheme.colors.delayed,
                style = textStyle, fontStyle = fontStyle, fontWeight = fontWeight
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EstimatedTimePreview() {
    StationTheme {
        Surface {
            EstimatedTime(
                scheduledTime = Instant.parse("2020-01-01T09:30Z"),
                estimatedTime = Instant.parse("2020-01-01T09:32Z"),
                type = TimetableRow.Type.Arrival
            )
        }
    }
}

/**
 * Displays actual time of arrival or departure along with time difference in minutes.
 *
 * @param actualTime Actual time.
 * @param differenceInMinutes Time difference from scheduled time in minutes.
 * @param type TimetableRow type.
 * @param modifier Modifier.
 * @param track Track name that will be included in the content description.
 */
@Composable
fun ActualTime(
    actualTime: Instant,
    differenceInMinutes: Int,
    type: TimetableRow.Type,
    modifier: Modifier = Modifier,
    track: String? = null
) {
    val context = LocalContext.current
    val trackText = trackString(track, type)
    val actualTimeText by produceLocalTime(actualTime)
    val description = remember(type, trackText, actualTimeText) {
        if (actualTimeText.isNotBlank()) {
            context.getString(
                if (type == TimetableRow.Type.Arrival) R.string.accessibility_label_actual_arrival
                else R.string.accessibility_label_actual_departure,
                trackText,
                actualTimeText
            )
        } else ""
    }
    Row(
        modifier.semantics { contentDescription = description },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            actualTimeText,
            style = MaterialTheme.typography.bodyLarge,
        )
        if (actualTimeText.isNotBlank() && differenceInMinutes != 0) {
            Spacer(Modifier.width(4.dp))
            val (text, color) = when {
                differenceInMinutes > 0 -> Pair("+$differenceInMinutes", StationTheme.colors.late)
                else -> Pair("$differenceInMinutes", StationTheme.colors.early)
            }
            Text(text, color = color, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@PreviewLightDark
@Composable
private fun ActualTimePreview() {
    StationTheme {
        Surface {
            ActualTime(
                actualTime = Instant.parse("2020-01-01T09:30Z"),
                differenceInMinutes = 1,
                type = TimetableRow.Type.Arrival
            )
        }
    }
}

/**
 * Displays a cancelled arrival.
 *
 * @param modifier An optional modifier.
 */
@Composable
fun CancelledArrival(modifier: Modifier = Modifier) {
    val accessibilityLabel = stringResource(R.string.accessibility_label_cancelled_arrival)
    CancelledLabel(modifier.semantics { contentDescription = accessibilityLabel })
}

/**
 * Displays a cancelled departure.
 *
 * @param modifier An optional modifier.
 */
@Composable
fun CancelledDeparture(modifier: Modifier = Modifier) {
    val accessibilityLabel = stringResource(R.string.accessibility_label_cancelled_departure)
    CancelledLabel(modifier.semantics { contentDescription = accessibilityLabel })
}

@Composable
private fun CancelledLabel(modifier: Modifier) {
    val label = stringResource(R.string.label_cancelled).uppercase()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@PreviewLightDark
@Composable
private fun CancelledLabelPreview() {
    StationTheme {
        Surface {
            CancelledLabel(Modifier)
        }
    }
}

@Composable
private fun trackString(track: String?, type: TimetableRow.Type): String {
    return if (track?.isNotBlank() == true) {
        " " + stringResource(
            if (type == TimetableRow.Type.Arrival) R.string.accessibility_label_to_track
            else R.string.accessibility_label_from_track,
            track
        )
    } else {
        ""
    }
}

@Composable
private fun produceLocalTime(time: Instant): State<String> {
    return produceState("", time) {
        value = time.toLocalTimeString()
    }
}

@Composable
private fun produceLocalTimes(time1: Instant, time2: Instant)
        : State<Pair<String, String>> {
    return produceState(Pair("", ""), time1, time2) {
        value = Pair(time1.toLocalTimeString(), time2.toLocalTimeString())
    }
}
