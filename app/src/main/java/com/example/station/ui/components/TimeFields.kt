package com.example.station.ui.components

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRightAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.station.ui.theme.StationTheme
import com.example.station.util.atLocalZone
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable fun ScheduledTime(scheduledTime: ZonedDateTime, modifier: Modifier = Modifier) {
    val time = scheduledTime.atLocalZone().format(formatter)
    Text(
        time,
        modifier,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
        style = MaterialTheme.typography.body1,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Light
    )
}

@Composable fun EstimatedTime(
    scheduledTime: ZonedDateTime,
    estimatedTime: ZonedDateTime,
    modifier: Modifier = Modifier
) {
    val scheduledTimeText = scheduledTime.atLocalZone().format(formatter)
    val estimatedTimeText = estimatedTime.atLocalZone().format(formatter)
    val textStyle = MaterialTheme.typography.body1
    val fontStyle = FontStyle.Italic
    val fontWeight = FontWeight.Light
    Row(
        modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            scheduledTimeText, color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
            style = textStyle, fontStyle = fontStyle, fontWeight = fontWeight
        )
        if (scheduledTimeText != estimatedTimeText) {
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

@Composable fun ActualTime(
    actualTime: ZonedDateTime,
    differenceInMinutes: Int,
    modifier: Modifier = Modifier
) {
    val time = actualTime.atLocalZone().format(formatter)
    Row(
        modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            time,
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
