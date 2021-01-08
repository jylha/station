package dev.jylha.station.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRightAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
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
import java.time.ZonedDateTime
import java.util.Locale

/**
 * Composable for displaying time field for arrival or departure.
 * @param timetableRow Timetable row.
 * @param modifier Modifier.
 */
@Composable fun TimeField(timetableRow: TimetableRow?, modifier: Modifier = Modifier) {
    timetableRow?.run {
        when {
            cancelled -> CancelledTime(type = timetableRow.type, modifier)
            actualTime != null ->
                ActualTime(actualTime, differenceInMinutes, timetableRow.type, modifier)
            estimatedTime != null && differenceInMinutes != 0 ->
                EstimatedTime(scheduledTime, estimatedTime, timetableRow.type, modifier)
            else -> ScheduledTime(scheduledTime, timetableRow.type, modifier)
        }
    } ?: Box(modifier)
}

/**
 * Composable for displaying schedule time.
 * @param scheduledTime Scheduled time.
 * @param type TimetableRow type.
 * @param modifier Modifier.
 */
@Composable fun ScheduledTime(
    scheduledTime: ZonedDateTime,
    type: TimetableRow.Type,
    modifier: Modifier = Modifier
) {
    val context = AmbientContext.current
    val scheduledTimeText by produceLocalTime(scheduledTime)
    val label = remember(scheduledTimeText, type) {
        context.getString(
            if (type == TimetableRow.Type.Arrival) R.string.accessibility_label_scheduled_arrival
            else R.string.accessibility_label_scheduled_departure,
            scheduledTimeText
        )
    }
    Text(
        scheduledTimeText,
        modifier.semantics { contentDescription = label },
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
        style = MaterialTheme.typography.body1,
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
 */
@Composable fun EstimatedTime(
    scheduledTime: ZonedDateTime,
    estimatedTime: ZonedDateTime,
    type: TimetableRow.Type,
    modifier: Modifier = Modifier
) {
    val context = AmbientContext.current
    val localTimes by produceLocalTimes(scheduledTime, estimatedTime)
    val (scheduledTimeText, estimatedTimeText) = localTimes
    val label = remember(type, estimatedTimeText) {
        context.getString(
            if (type == TimetableRow.Type.Arrival) R.string.accessibility_label_estimated_arrival
            else R.string.accessibility_label_estimated_departure,
            estimatedTimeText
        )
    }
    val textStyle = MaterialTheme.typography.body1
    val fontStyle = FontStyle.Italic
    val fontWeight = FontWeight.Light
    Row(
        modifier.semantics { contentDescription = label },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            scheduledTimeText, color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
            style = textStyle, fontStyle = fontStyle, fontWeight = fontWeight
        )
        if (scheduledTimeText.isNotBlank() && estimatedTimeText.isNotBlank()) {
            Icon(
                Icons.Rounded.ArrowRightAlt,
                Modifier.padding(horizontal = 4.dp, vertical = 0.dp).preferredSize(16.dp),
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
 */
@Composable fun ActualTime(
    actualTime: ZonedDateTime,
    differenceInMinutes: Int,
    type: TimetableRow.Type,
    modifier: Modifier = Modifier
) {
    val context = AmbientContext.current
    val actualTimeText by produceLocalTime(actualTime)
    val label = remember(type, actualTimeText) {
        context.getString(
            if (type == TimetableRow.Type.Arrival) R.string.accessibility_label_actual_arrival
            else R.string.accessibility_label_actual_departure,
            actualTimeText
        )
    }
    Row(
        modifier.semantics { contentDescription = label },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            actualTimeText,
            style = MaterialTheme.typography.body1,
        )
        if (actualTimeText.isNotBlank() && differenceInMinutes != 0) {
            Spacer(Modifier.width(4.dp))
            val (text, color) = when {
                differenceInMinutes > 0 -> Pair("+$differenceInMinutes", StationTheme.colors.late)
                else -> Pair("$differenceInMinutes", StationTheme.colors.early)
            }
            Text(text, color = color, style = MaterialTheme.typography.caption)
        }
    }
}

/**
 * Composable function for displaying a cancellation instead of arrival or departure time.
 * @param type TimetableRow type.
 * @param modifier Modifier.
 */
@Composable fun CancelledTime(
    type: TimetableRow.Type,
    modifier: Modifier = Modifier
) {
    val label = stringResource(R.string.label_cancelled).toUpperCase(Locale.getDefault())
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
            color = Color.Red,
            style = MaterialTheme.typography.body2,
        )
    }
}

@Composable private fun produceLocalTime(time: ZonedDateTime): State<String> {
    return produceState("", time) {
        value = time.toLocalTimeString()
    }
}

@Composable private fun produceLocalTimes(
    time1: ZonedDateTime,
    time2: ZonedDateTime
): State<Pair<String, String>> {
    return produceState(Pair("", ""), time1, time2) {
        value = Pair(time1.toLocalTimeString(), time2.toLocalTimeString())
    }
}
