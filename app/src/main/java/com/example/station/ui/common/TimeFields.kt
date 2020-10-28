package com.example.station.ui.common

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRightAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.semantics.accessibilityLabel
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.station.R
import com.example.station.model.TimetableRow
import com.example.station.ui.theme.StationTheme
import com.example.station.util.toLocalTimeString
import java.time.ZonedDateTime

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
    val context = ContextAmbient.current
    val scheduledTimeText = remember(scheduledTime) { scheduledTime.toLocalTimeString() }
    val label = remember(scheduledTimeText, type) {
        context.getString(
            if (type == TimetableRow.Type.Arrival) R.string.accessibility_label_scheduled_arrival
            else R.string.accessibility_label_scheduled_departure,
            scheduledTimeText
        )
    }
    Text(
        scheduledTimeText,
        modifier.semantics { accessibilityLabel = label },
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
    val context = ContextAmbient.current
    val scheduledTimeText = remember(scheduledTime) { scheduledTime.toLocalTimeString() }
    val estimatedTimeText = remember(estimatedTime) { estimatedTime.toLocalTimeString() }
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
        modifier.semantics { accessibilityLabel = label },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            scheduledTimeText, color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
            style = textStyle, fontStyle = fontStyle, fontWeight = fontWeight
        )
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
    val context = ContextAmbient.current
    val actualTimeText = remember(actualTime) { actualTime.toLocalTimeString() }
    val label = remember(type, actualTimeText) {
        context.getString(
            if (type == TimetableRow.Type.Arrival) R.string.accessibility_label_actual_arrival
            else R.string.accessibility_label_actual_departure,
            actualTimeText
        )
    }
    Row(
        modifier.semantics { accessibilityLabel = label },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            actualTimeText,
            style = MaterialTheme.typography.body1,
            fontStyle = FontStyle.Normal
        )
        if (differenceInMinutes != 0) {
            Spacer(Modifier.width(4.dp))
            val (text, color) = when {
                differenceInMinutes > 0 -> Pair("+$differenceInMinutes", StationTheme.colors.late)
                else -> Pair("$differenceInMinutes", StationTheme.colors.early)
            }
            Text(text, color = color, style = MaterialTheme.typography.caption)
        }
    }
}
