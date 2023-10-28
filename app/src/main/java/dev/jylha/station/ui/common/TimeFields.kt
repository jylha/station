package dev.jylha.station.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRightAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jylha.station.R
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.ui.theme.StationTheme
import dev.jylha.station.util.toLocalTimeString

/**
 * Composable for displaying time field for arrival or departure.
 * @param timetableRow Timetable row.
 * @param modifier Modifier.
 */
@Composable
fun TimeField(timetableRow: TimetableRow?, modifier: Modifier = Modifier) {
    timetableRow?.run {
        when {
            cancelled -> CancelledTime(type = timetableRow.type, modifier)
            actualTime != null ->
                ActualTime(
                    actualTime.toImmutable(),
                    differenceInMinutes,
                    timetableRow.type,
                    modifier
                )

            estimatedTime != null && differenceInMinutes != 0 ->
                EstimatedTime(
                    scheduledTime.toImmutable(),
                    estimatedTime.toImmutable(),
                    timetableRow.type,
                    modifier
                )

            else -> ScheduledTime(scheduledTime.toImmutable(), timetableRow.type, modifier)
        }
    } ?: Box(modifier)
}

/**
 * Composable for displaying schedule time.
 * @param scheduledTime Scheduled time.
 * @param type TimetableRow type.
 * @param modifier Modifier.
 * @param track Track name that will be included in the content description.
 */
@Composable
fun ScheduledTime(
    scheduledTime: ImmutableTime,
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
    scheduledTime: ImmutableTime,
    estimatedTime: ImmutableTime,
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
                Icons.Rounded.ArrowRightAlt, contentDescription = null,
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

/**
 * Composable for displaying actual time of arrival or departure along with time difference
 * in minutes.
 * @param actualTime Actual time.
 * @param differenceInMinutes Time difference from scheduled time in minutes.
 * @param type TimetableRow type.
 * @param modifier Modifier.
 * @param track Track name that will be included in the content description.
 */
@Composable
fun ActualTime(
    actualTime: ImmutableTime,
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
            Text(text, color = color, style = MaterialTheme.typography.labelSmall)
        }
    }
}

/**
 * Composable function for displaying a cancellation instead of arrival or departure time.
 * @param type TimetableRow type.
 * @param modifier Modifier.
 */
@Composable
fun CancelledTime(
    type: TimetableRow.Type,
    modifier: Modifier = Modifier
) {
    val label = stringResource(R.string.label_cancelled).uppercase()
    val accessibilityLabel = when (type) {
        TimetableRow.Type.Arrival -> stringResource(R.string.accessibility_label_cancelled_arrival)
        else -> stringResource(R.string.accessibility_label_cancelled_departure)
    }
    Row(
        modifier = modifier.semantics { contentDescription = accessibilityLabel },
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
private fun produceLocalTime(time: ImmutableTime): State<String> {
    return produceState("", time) {
        value = time().toLocalTimeString()
    }
}

@Composable
private fun produceLocalTimes(
    time1: ImmutableTime,
    time2: ImmutableTime
): State<Pair<String, String>> {
    return produceState(Pair("", ""), time1, time2) {
        value = Pair(time1().toLocalTimeString(), time2().toLocalTimeString())
    }
}
